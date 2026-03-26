package qa.autotest.framework.drivers;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import qa.autotest.framework.config.TestConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages WebDriver instances for different browsers.
 * Thread-safe: ThreadLocal per-thread driver + Semaphore for browser count cap.
 * <p>
 * - Configuration.* инициализируется один раз до параллельного старта через
 * configureSelenideOnce(), не в каждом потоке — глобальные static поля Selenide
 * не thread-safe для записи.
 * - config передаётся параметром во все private методы — исключает повторные
 * вызовы ConfigFactory.getConfig() внутри createChromeDriver/Firefox/Edge.
 */
@Slf4j
public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private static final int MAX_BROWSERS = Integer.parseInt(
            System.getProperty("max.parallel.browsers", "3")
    );
    private static final Semaphore browserSemaphore = new Semaphore(MAX_BROWSERS, true);
    private static final AtomicInteger activeBrowsers = new AtomicInteger(0);

    private static volatile boolean selenideConfigured = false;

    private DriverManager() {
    }

    public static WebDriver getCurrentThreadDriver() {
        return driver.get();
    }

    public static boolean isDriverHealthy() {
        try {
            WebDriver current = driver.get();
            if (current != null) {
                current.getTitle();
                return true;
            }
        } catch (Exception e) {
            log.debug("Thread {}: driver is not healthy: {}",
                    Thread.currentThread().getName(), e.getMessage());
        }
        return false;
    }

    /**
     * Инициализирует WebDriver для текущего треда.
     * config передаётся снаружи — DriverManager не обращается к ConfigFactory напрямую.
     */
    public static void initDriver(TestConfig config) {
        String threadName = Thread.currentThread().getName();

        if (driver.get() != null) {
            log.debug("Thread {}: driver already exists, closing", threadName);
            quitDriver();
        }

        configureSelenideOnce(config);

        try {
            log.info("Thread {}: waiting for browser permit (active: {})",
                    threadName, activeBrowsers.get());
            browserSemaphore.acquire();
            activeBrowsers.incrementAndGet();
            log.info("Thread {}: permit acquired, active browsers: {}",
                    threadName, activeBrowsers.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for browser permit", e);
        }

        try {
            String browser = config.browser().toLowerCase();
            boolean headless = Boolean.TRUE.equals(config.browserHeadless());
            String remoteUrl = config.browserRemoteUrl();

            log.info("Thread {}: initializing {} driver (headless: {})",
                    threadName, browser, headless);

            WebDriver webDriver = (remoteUrl != null && !remoteUrl.isEmpty())
                    ? createRemoteDriver(browser, headless, remoteUrl, config)
                    : createLocalDriver(browser, headless, config);

            webDriver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofMillis(config.pageLoadTimeout()));

            webDriver.manage().window().setSize(
                    new org.openqa.selenium.Dimension(config.browserWidth(), config.browserHeight())
            );

            driver.set(webDriver);
            WebDriverRunner.setWebDriver(webDriver);

            log.info("Thread {}: driver initialized", threadName);

        } catch (Exception e) {
            activeBrowsers.decrementAndGet();
            browserSemaphore.release();
            log.error("Thread {}: failed to initialize driver: {}", threadName, e.getMessage());
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    public static void quitDriver() {
        String threadName = Thread.currentThread().getName();
        WebDriver current = driver.get();

        if (current != null) {
            try {
                log.info("Thread {}: quitting driver", threadName);
                current.quit();
                driver.remove();
                WebDriverRunner.closeWebDriver();
                log.info("Thread {}: driver quit successfully", threadName);
            } catch (Exception e) {
                log.error("Thread {}: error while quitting driver: {}", threadName, e.getMessage());
            } finally {
                activeBrowsers.decrementAndGet();
                browserSemaphore.release();
                log.info("Thread {}: browser permit released, active: {}",
                        threadName, activeBrowsers.get());
            }
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void logDriversStatus() {
        log.info("Active browsers: {}, available permits: {}",
                activeBrowsers.get(), browserSemaphore.availablePermits());
    }

    /**
     * Selenide Configuration содержит глобальные static поля — запись из нескольких
     * тредов одновременно создаёт race condition. Инициализируем один раз до старта
     * параллельного выполнения. Значения одинаковы для всех тредов.
     */
    private static void configureSelenideOnce(TestConfig config) {
        if (!selenideConfigured) {
            synchronized (DriverManager.class) {
                if (!selenideConfigured) {
                    Configuration.timeout = config.explicitTimeout();
                    Configuration.screenshots = config.screenshotOnFailure();
                    Configuration.reportsFolder = config.screenshotFolder();
                    selenideConfigured = true;
                    log.info("Selenide configured: timeout={}ms, screenshots={}, folder={}",
                            config.explicitTimeout(), config.screenshotOnFailure(), config.screenshotFolder());
                }
            }
        }
    }

    private static WebDriver createLocalDriver(String browser, boolean headless, TestConfig config) {
        return switch (browser) {
            case "chrome" -> createChromeDriver(headless, config);
            case "firefox" -> createFirefoxDriver(headless, config);
            case "edge" -> createEdgeDriver(headless, config);
            case "safari" -> createSafariDriver();
            default -> {
                log.warn("Unknown browser: {}. Using Chrome as default", browser);
                yield createChromeDriver(headless, config);
            }
        };
    }

    private static WebDriver createRemoteDriver(String browser, boolean headless,
                                                String remoteUrl, TestConfig config)
            throws MalformedURLException {
        log.info("Creating remote driver: {} at {}", browser, remoteUrl);
        return switch (browser) {
            case "chrome" -> new RemoteWebDriver(new URL(remoteUrl), getChromeOptions(headless));
            case "firefox" -> new RemoteWebDriver(new URL(remoteUrl), getFirefoxOptions(headless));
            case "edge" -> new RemoteWebDriver(new URL(remoteUrl), getEdgeOptions(headless));
            case "safari" -> new RemoteWebDriver(new URL(remoteUrl), new SafariOptions());
            default -> throw new IllegalArgumentException("Unsupported browser for remote: " + browser);
        };
    }

    private static WebDriver createChromeDriver(boolean headless, TestConfig config) {
        if (Boolean.TRUE.equals(config.useLocalDrivers()) && config.chromeDriverPath() != null) {
            log.info("Using local Chrome driver: {}", config.chromeDriverPath());
            System.setProperty("webdriver.chrome.driver", config.chromeDriverPath());
        } else {
            log.info("Using WebDriverManager for Chrome");
            WebDriverManager.chromedriver().setup();
        }
        return new ChromeDriver(getChromeOptions(headless));
    }

    private static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--disable-gpu",
                "--disable-blink-features=AutomationControlled",
                "--incognito",
                "--disable-save-password-bubble",
                "--disable-password-generation",
                "--disable-password-manager-reauthentication"
        );
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.default_content_setting_values.notifications", 2,
                "profile.default_content_settings.popups", 0,
                "autofill.profile_enabled", false
        ));
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static WebDriver createFirefoxDriver(boolean headless, TestConfig config) {
        if (Boolean.TRUE.equals(config.useLocalDrivers()) && config.firefoxDriverPath() != null) {
            log.info("Using local Firefox driver: {}", config.firefoxDriverPath());
            System.setProperty("webdriver.gecko.driver", config.firefoxDriverPath());
        } else {
            log.info("Using WebDriverManager for Firefox");
            WebDriverManager.firefoxdriver().setup();
        }
        return new FirefoxDriver(getFirefoxOptions(headless));
    }

    private static FirefoxOptions getFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static WebDriver createEdgeDriver(boolean headless, TestConfig config) {
        if (Boolean.TRUE.equals(config.useLocalDrivers()) && config.edgeDriverPath() != null) {
            log.info("Using local Edge driver: {}", config.edgeDriverPath());
            System.setProperty("webdriver.edge.driver", config.edgeDriverPath());
        } else {
            log.info("Using WebDriverManager for Edge");
            WebDriverManager.edgedriver().setup();
        }
        return new EdgeDriver(getEdgeOptions(headless));
    }

    private static EdgeOptions getEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        return options;
    }

    private static WebDriver createSafariDriver() {
        return new SafariDriver(new SafariOptions());
    }
}
