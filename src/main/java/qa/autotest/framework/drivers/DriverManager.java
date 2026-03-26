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
import qa.autotest.framework.config.ConfigFactory;
import qa.autotest.framework.config.TestConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages WebDriver instances for different browsers
 * Thread-safe implementation with Semaphore for parallel execution control
 */
@Slf4j
public class DriverManager {

    // ThreadLocal для хранения WebDriver для каждого потока
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Semaphore для ограничения количества одновременных браузеров
    private static final int MAX_BROWSERS = Integer.parseInt(
            System.getProperty("max.parallel.browsers", "3")
    );
    private static final Semaphore browserSemaphore = new Semaphore(MAX_BROWSERS, true);
    private static final AtomicInteger activeBrowsers = new AtomicInteger(0);

    // ANSI цвета для логов
    private static final String GREEN_TEXT = "\u001B[32m";
    private static final String RED_TEXT = "\u001B[31m";
    private static final String YELLOW_TEXT = "\u001B[33m";
    private static final String CYAN_TEXT = "\u001B[36m";
    private static final String RESET_TEXT = "\u001B[0m";

    private DriverManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Получить WebDriver для текущего потока
     *
     * @return WebDriver instance or null
     */
    public static WebDriver getCurrentThreadDriver() {
        return driver.get();
    }

    /**
     * Проверить здоровье драйвера
     *
     * @return true если драйвер активен
     */
    public static boolean isDriverHealthy() {
        try {
            WebDriver currentDriver = driver.get();
            if (currentDriver != null) {
                currentDriver.getTitle(); // Проверка что драйвер работает
                return true;
            }
        } catch (Exception e) {
            log.debug("Thread {}: Driver is not healthy: {}",
                    Thread.currentThread().getName(), e.getMessage());
        }
        return false;
    }

    /**
     * Initializes WebDriver based on configuration
     *
     * @param config Test configuration
     */
    public static void initDriver(TestConfig config) {
        String threadName = Thread.currentThread().getName();

        // Если драйвер уже существует для этого потока, закрываем его
        if (driver.get() != null) {
            log.debug("Thread {}: Driver already exists, closing it", threadName);
            quitDriver();
        }

        // Ожидаем разрешения на создание браузера
        try {
            log.info(YELLOW_TEXT + "Thread {}: Waiting for browser permit... (active: {})" + RESET_TEXT,
                    threadName, activeBrowsers.get());
            browserSemaphore.acquire();
            int currentActive = activeBrowsers.incrementAndGet();
            log.info(GREEN_TEXT + "Thread {}: Permit acquired. Active browsers: {}" + RESET_TEXT,
                    threadName, currentActive);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for browser permit", e);
        }

        try {
            String browser = config.browser().toLowerCase();

            // Support both -Dheadless=true and -Dbrowser.headless=true
            boolean headless = false;
            if (config.headless() != null) {
                headless = config.headless();
            } else if (config.browserHeadless() != null) {
                headless = config.browserHeadless();
            }

            String remoteUrl = config.browserRemoteUrl();

            log.info(GREEN_TEXT + "Thread {}: Initializing {} driver (headless: {})" + RESET_TEXT,
                    threadName, browser, headless);

            WebDriver webDriver;

            if (remoteUrl != null && !remoteUrl.isEmpty()) {
                webDriver = createRemoteDriver(browser, headless, remoteUrl);
            } else {
                webDriver = createLocalDriver(browser, headless);
            }

            // Configure timeouts.
            webDriver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofMillis(config.pageLoadTimeout()));

            // Set window size
            webDriver.manage().window().setSize(
                    new org.openqa.selenium.Dimension(config.browserWidth(), config.browserHeight())
            );

            driver.set(webDriver);
            WebDriverRunner.setWebDriver(webDriver);

            // Configure Selenide
            Configuration.timeout = config.explicitTimeout();
            Configuration.screenshots = config.screenshotOnFailure();
            Configuration.reportsFolder = config.screenshotFolder();

            log.info(GREEN_TEXT + "Thread {}: Driver initialized successfully" + RESET_TEXT, threadName);

        } catch (Exception e) {
            // В случае ошибки освобождаем семафор
            int remaining = activeBrowsers.decrementAndGet();
            browserSemaphore.release();
            log.error(RED_TEXT + "Thread {}: Failed to initialize driver: {}" + RESET_TEXT,
                    threadName, e.getMessage());
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    /**
     * Creates local WebDriver instance
     */
    private static WebDriver createLocalDriver(String browser, boolean headless) {
        return switch (browser) {
            case "chrome" -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            case "edge" -> createEdgeDriver(headless);
            case "safari" -> createSafariDriver();
            default -> {
                log.warn("Unknown browser: {}. Using Chrome as default", browser);
                yield createChromeDriver(headless);
            }
        };
    }

    /**
     * Creates remote WebDriver instance for Selenium Grid
     */
    private static WebDriver createRemoteDriver(String browser, boolean headless, String remoteUrl)
            throws MalformedURLException {

        log.info("Creating remote driver for: {} at {}", browser, remoteUrl);

        return switch (browser) {
            case "chrome" -> new RemoteWebDriver(new URL(remoteUrl), getChromeOptions(headless));
            case "firefox" -> new RemoteWebDriver(new URL(remoteUrl), getFirefoxOptions(headless));
            case "edge" -> new RemoteWebDriver(new URL(remoteUrl), getEdgeOptions(headless));
            case "safari" -> new RemoteWebDriver(new URL(remoteUrl), new SafariOptions());
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    private static WebDriver createChromeDriver(boolean headless) {
        TestConfig config = ConfigFactory.getConfig();

        if (config.useLocalDrivers() && config.chromeDriverPath() != null) {
            log.info("Using local Chrome driver from: {}", config.chromeDriverPath());
            System.setProperty("webdriver.chrome.driver", config.chromeDriverPath());
        } else {
            log.info("Using WebDriverManager for Chrome");
            WebDriverManager.chromedriver().setup();
        }

        return new ChromeDriver(getChromeOptions(headless));
    }

    private static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        // Performance and stability
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--incognito");

        // Additional password manager safeguards (redundant with incognito but ensures compatibility)
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-password-generation");
        options.addArguments("--disable-password-manager-reauthentication");

        // Comprehensive preferences to disable password manager and autofill
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.default_content_setting_values.notifications", 2,
                "profile.default_content_settings.popups", 0,
                "autofill.profile_enabled", false
        ));

        // Exclude automation switches that might trigger credential prompts
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        if (headless) {
            options.addArguments("--headless=new");
        }

        return options;
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        TestConfig config = ConfigFactory.getConfig();

        if (config.useLocalDrivers() && config.firefoxDriverPath() != null) {
            log.info("Using local Firefox driver from: {}", config.firefoxDriverPath());
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

    private static WebDriver createEdgeDriver(boolean headless) {
        TestConfig config = ConfigFactory.getConfig();

        if (config.useLocalDrivers() && config.edgeDriverPath() != null) {
            log.info("Using local Edge driver from: {}", config.edgeDriverPath());
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
        // Safari doesn't support headless mode natively
        // WebDriverManager not needed for Safari on macOS
        return new SafariDriver(new SafariOptions());
    }

    /**
     * Gets current WebDriver instance
     *
     * @return WebDriver instance for current thread
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Quits and removes WebDriver instance for current thread
     */
    /**
     * Quits the WebDriver for the current thread and releases browser permit
     */
    public static void quitDriver() {
        String threadName = Thread.currentThread().getName();
        WebDriver currentDriver = driver.get();

        if (currentDriver != null) {
            try {
                log.info(CYAN_TEXT + "Thread {}: Quitting driver..." + RESET_TEXT, threadName);
                currentDriver.quit();
                driver.remove();
                WebDriverRunner.closeWebDriver();
                log.info(GREEN_TEXT + "Thread {}: Driver quit successfully" + RESET_TEXT, threadName);
            } catch (Exception e) {
                log.error(RED_TEXT + "Thread {}: Error while quitting driver: {}" + RESET_TEXT,
                        threadName, e.getMessage());
            } finally {
                // Освобождаем семафор и уменьшаем счетчик активных браузеров
                int remaining = activeBrowsers.decrementAndGet();
                browserSemaphore.release();
                log.info(YELLOW_TEXT + "Thread {}: Browser permit released. Active browsers: {}" + RESET_TEXT,
                        threadName, remaining);
            }
        }
    }

    /**
     * Логирование статуса драйверов (для отладки)
     */
    public static void logDriversStatus() {
        log.info(CYAN_TEXT + "Active browsers: {}, Available permits: {}" + RESET_TEXT,
                activeBrowsers.get(), browserSemaphore.availablePermits());
    }
}
