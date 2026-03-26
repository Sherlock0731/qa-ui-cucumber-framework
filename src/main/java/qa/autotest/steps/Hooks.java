package qa.autotest.steps;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import qa.autotest.framework.config.ConfigFactory;
import qa.autotest.framework.config.TestConfig;
import qa.autotest.framework.drivers.DriverManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cucumber Hooks for test setup and teardown.
 * Thread-safe implementation for parallel execution.
 * <p>
 * Скриншоты при падении:
 * AllureSelenide с .screenshots(true) перехватывает каждый failed Selenide event
 * через SelenideLogger и прикрепляет скриншот к текущему Allure test case напрямую
 * через AllureLifecycle.
 */
@Slf4j
public class Hooks {

    private final TestConfig CONFIG = ConfigFactory.getConfig();

    private static final AtomicBoolean selenideListenerRegistered = new AtomicBoolean(false);

    @Before(order = 1)
    public void setUp(Scenario scenario) {
        String threadName = Thread.currentThread().getName();

        log.info("Thread {}: scenario started: {}", threadName, scenario.getName());
        log.info("Thread {}: tags: {}", threadName, scenario.getSourceTagNames());
        log.info("Thread {}: browser: {}, headless: {}",
                threadName, CONFIG.browser(), Boolean.TRUE.equals(CONFIG.browserHeadless()));

        // AllureSelenide регистрируется один раз для всех тредов.
        // .screenshots(true)   — скриншот при каждом failed Selenide event
        // .savePageSource(false) — page source отключён: замедляет параллельный прогон,
        //                          при необходимости включается отдельно.
        if (selenideListenerRegistered.compareAndSet(false, true)) {
            SelenideLogger.addListener("allure", new AllureSelenide()
                    .screenshots(true)
                    .savePageSource(false));
            log.info("Thread {}: AllureSelenide listener registered", threadName);
        }

        if (needsWebDriver(scenario)) {
            DriverManager.initDriver(CONFIG);
        } else {
            log.info("Thread {}: scenario doesn't need WebDriver", threadName);
        }
    }

    @After(order = 100)
    public void tearDown(Scenario scenario) {
        log.info("Thread {}: scenario finished: {} — {}",
                Thread.currentThread().getName(), scenario.getName(), scenario.getStatus());

        if (needsWebDriver(scenario)) {
            if (scenario.isFailed() && DriverManager.getCurrentThreadDriver() != null) {
                try {
                    byte[] screenshot = ((TakesScreenshot) DriverManager.getCurrentThreadDriver())
                            .getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "Screenshot on failure");
                    log.info("Thread {}: screenshot attached to scenario",
                            Thread.currentThread().getName());
                } catch (Exception e) {
                    log.warn("Thread {}: failed to take screenshot: {}",
                            Thread.currentThread().getName(), e.getMessage());
                }
            }
            DriverManager.quitDriver();
        }
    }

    private boolean needsWebDriver(Scenario scenario) {
        boolean needsDriver = scenario.getSourceTagNames().stream()
                .noneMatch(tag -> tag.equalsIgnoreCase("@API") ||
                        tag.equalsIgnoreCase("@DATABASE") ||
                        tag.equalsIgnoreCase("@DB") ||
                        tag.equalsIgnoreCase("@API-ONLY") ||
                        tag.equalsIgnoreCase("@DB-ONLY") ||
                        tag.equalsIgnoreCase("@NO-BROWSER"));

        log.debug("Thread {}: scenario '{}' {} WebDriver",
                Thread.currentThread().getName(), scenario.getName(),
                needsDriver ? "requires" : "doesn't require");
        return needsDriver;
    }
}
