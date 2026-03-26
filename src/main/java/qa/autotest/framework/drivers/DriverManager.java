package qa.autotest.framework.drivers;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import qa.autotest.framework.config.TestConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Управляет жизненным циклом WebDriver: создание, хранение (ThreadLocal),
 * ограничение параллельных браузеров (Semaphore), завершение.
 * <p>
 * SRP: только lifecycle. Создание конкретного браузера делегировано BrowserFactory.
 * OCP: добавление нового браузера = новый BrowserFactory + строка в FACTORIES.
 * DriverManager не меняется.
 * <p>
 * Selenide Configuration инициализируется один раз (configureSelenideOnce):
 * глобальные static поля не thread-safe для записи из параллельных тредов.
 * <p>
 * implicitlyWait намеренно не устанавливается: конфликтует с Selenide FluentWait.
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

    /**
     * Реестр фабрик: BrowserType → BrowserFactory. OCP-точка расширения.
     */
    private static final Map<BrowserType, BrowserFactory> FACTORIES;

    static {
        FACTORIES = new EnumMap<>(BrowserType.class);
        FACTORIES.put(BrowserType.CHROME, new ChromeBrowserFactory());
        FACTORIES.put(BrowserType.FIREFOX, new FirefoxBrowserFactory());
        FACTORIES.put(BrowserType.EDGE, new EdgeBrowserFactory());
        FACTORIES.put(BrowserType.SAFARI, new SafariBrowserFactory());
    }

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
            BrowserType browserType = BrowserType.fromString(config.browser());
            boolean headless = Boolean.TRUE.equals(config.browserHeadless());
            String remoteUrl = config.browserRemoteUrl();

            log.info("Thread {}: initializing {} (headless: {})", threadName, browserType, headless);

            WebDriver webDriver = (remoteUrl != null && !remoteUrl.isEmpty())
                    ? createRemoteDriver(browserType, headless, remoteUrl)
                    : createLocalDriver(browserType, headless, config);

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
                log.info("Thread {}: permit released, active: {}",
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

    private static WebDriver createLocalDriver(BrowserType type, boolean headless, TestConfig config) {
        BrowserFactory factory = FACTORIES.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("No BrowserFactory registered for: " + type);
        }
        return factory.create(headless, config);
    }

    private static WebDriver createRemoteDriver(BrowserType type, boolean headless, String remoteUrl)
            throws MalformedURLException {
        log.info("Creating remote driver: {} at {}", type, remoteUrl);
        BrowserFactory factory = FACTORIES.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("No BrowserFactory registered for remote: " + type);
        }
        return new RemoteWebDriver(new URL(remoteUrl), factory.buildOptionsForRemote(headless));
    }
}
