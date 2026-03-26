package qa.autotest.steps;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import qa.autotest.framework.config.ConfigFactory;
import qa.autotest.framework.config.TestConfig;
import qa.autotest.framework.drivers.DriverManager;
import qa.autotest.listeners.AllureSelenideListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cucumber Hooks for test setup and teardown.
 * Thread-safe implementation for parallel execution.
 */
@Slf4j
public class Hooks {

    private final TestConfig CONFIG = ConfigFactory.getConfig();

    private static final AtomicBoolean isSelenideListenerRegistered = new AtomicBoolean(false);

    @Before(order = 1)
    public void setUp(Scenario scenario) {
        String threadName = Thread.currentThread().getName();

        log.info("Thread {}: Scenario Started: {}", threadName, scenario.getName());
        log.info("Thread {}: Tags: {}", threadName, scenario.getSourceTagNames());
        log.info("Thread {}: Browser: {}, Headless: {}", threadName, CONFIG.browser(), getHeadlessMode());

        if (isSelenideListenerRegistered.compareAndSet(false, true)) {
            SelenideLogger.addListener("AllureSelenide", new AllureSelenideListener());
            log.info("Thread {}: Allure Selenide listener registered", threadName);
        }

        if (needsWebDriver(scenario)) {
            DriverManager.initDriver(CONFIG);
        } else {
            log.info("Thread {}: Scenario doesn't need WebDriver (API/DB test)", threadName);
        }
    }

    /**
     * Take screenshot on failure.
     */
    @After(order = 10)
    public void takeScreenshot(Scenario scenario) {
        String threadName = Thread.currentThread().getName();

        if (scenario.isFailed() && needsWebDriver(scenario)) {
            if (DriverManager.getCurrentThreadDriver() != null && DriverManager.isDriverHealthy()) {
                try {
                    log.info("Thread {}: Taking screenshot for failed scenario: {}", threadName, scenario.getName());

                    byte[] screenshot = Selenide.screenshot(OutputType.BYTES);
                    if (screenshot != null) {
                        scenario.attach(screenshot, "image/png", "Screenshot on failure");
                        log.info("Thread {}: Screenshot attached successfully", threadName);
                    } else {
                        log.warn("Thread {}: Screenshot returned null", threadName);
                    }
                } catch (Exception e) {
                    log.warn("Thread {}: Failed to take screenshot: {}", threadName, e.getMessage());
                }
            } else {
                log.warn("Thread {}: WebDriver not available for screenshot", threadName);
            }
        }
    }

    @After(order = 100)
    public void tearDown(Scenario scenario) {
        String threadName = Thread.currentThread().getName();

        log.info("Thread {}: Scenario Finished: {} — {}", threadName, scenario.getName(), scenario.getStatus());

        if (needsWebDriver(scenario)) {
            DriverManager.quitDriver();
        }
    }

    private boolean needsWebDriver(Scenario scenario) {
        boolean needsDriver = scenario.getSourceTagNames().stream()
                .noneMatch(tag -> tag.equalsIgnoreCase("@API")      ||
                                  tag.equalsIgnoreCase("@DATABASE") ||
                                  tag.equalsIgnoreCase("@DB")       ||
                                  tag.equalsIgnoreCase("@API-ONLY") ||
                                  tag.equalsIgnoreCase("@DB-ONLY")  ||
                                  tag.equalsIgnoreCase("@NO-BROWSER"));

        log.debug("Thread {}: Scenario '{}' {} WebDriver",
                Thread.currentThread().getName(), scenario.getName(),
                needsDriver ? "requires" : "doesn't require");
        return needsDriver;
    }

    private boolean getHeadlessMode() {
        return Boolean.TRUE.equals(CONFIG.browserHeadless());
    }
}
