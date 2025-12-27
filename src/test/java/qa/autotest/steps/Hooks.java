package qa.autotest.steps;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import qa.autotest.framework.config.ConfigFactory;
import qa.autotest.framework.config.TestConfig;
import qa.autotest.framework.drivers.DriverManager;
import qa.autotest.listeners.AllureSelenideListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cucumber Hooks for test setup and teardown
 * Thread-safe implementation for parallel execution
 */
@Slf4j
public class Hooks {
    
    private static final TestConfig CONFIG = ConfigFactory.getConfig();
    private static final AtomicBoolean isSelenideListenerRegistered = new AtomicBoolean(false);
    
    // ANSI цвета для логов
    private static final String GREEN_TEXT = "\u001B[32m";
    private static final String RED_TEXT = "\u001B[31m";
    private static final String YELLOW_TEXT = "\u001B[33m";
    private static final String CYAN_TEXT = "\u001B[36m";
    private static final String RESET_TEXT = "\u001B[0m";
    
    /**
     * Setup before each scenario
     * Initializes WebDriver and registers Allure listener
     */
    @Before(order = 1)
    public void setUp(Scenario scenario) {
        String threadName = Thread.currentThread().getName();
        
        log.info(GREEN_TEXT + "╔══════════════════════════════════════════════════════════════" + RESET_TEXT);
        log.info(GREEN_TEXT + "║ Thread {}: Scenario Started: {}" + RESET_TEXT, threadName, scenario.getName());
        log.info(GREEN_TEXT + "║ Tags: {}" + RESET_TEXT, scenario.getSourceTagNames());
        log.info(GREEN_TEXT + "║ Browser: {}" + RESET_TEXT, CONFIG.browser());
        log.info(GREEN_TEXT + "║ Headless: {}" + RESET_TEXT, getHeadlessMode());
        log.info(GREEN_TEXT + "╚══════════════════════════════════════════════════════════════" + RESET_TEXT);
        
        // Register Allure Selenide listener once (thread-safe)
        if (isSelenideListenerRegistered.compareAndSet(false, true)) {
            SelenideLogger.addListener("AllureSelenide", new AllureSelenideListener());
            log.info(CYAN_TEXT + "Allure Selenide listener registered" + RESET_TEXT);
        }
        
        // Проверяем, нужен ли WebDriver для этого сценария
        if (needsWebDriver(scenario)) {
            DriverManager.initDriver(CONFIG);
        } else {
            log.info(YELLOW_TEXT + "Thread {}: Scenario doesn't need WebDriver (API/DB test)" + RESET_TEXT, threadName);
        }
    }
    
    /**
     * Take screenshot on failure
     */
    @After(order = 10)
    public void takeScreenshot(Scenario scenario) {
        String threadName = Thread.currentThread().getName();
        
        if (scenario.isFailed() && needsWebDriver(scenario)) {
            try {
                Thread.sleep(1000); // Небольшая задержка перед скриншотом
                
                WebDriver driver = DriverManager.getCurrentThreadDriver();
                if (driver != null && DriverManager.isDriverHealthy()) {
                    log.info(YELLOW_TEXT + "Thread {}: Taking screenshot for failed scenario: {}" + RESET_TEXT,
                            threadName, scenario.getName());
                    
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "Screenshot on failure");
                    
                    log.info(GREEN_TEXT + "Thread {}: Screenshot attached successfully" + RESET_TEXT, threadName);
                } else {
                    log.warn(YELLOW_TEXT + "Thread {}: WebDriver not available for screenshot" + RESET_TEXT, threadName);
                }
            } catch (Exception e) {
                log.warn(RED_TEXT + "Thread {}: Failed to take screenshot: {}" + RESET_TEXT,
                        threadName, e.getMessage());
            }
        }
    }
    
    /**
     * Teardown after each scenario
     * Quits WebDriver and logs scenario status
     */
    @After(order = 100)
    public void tearDown(Scenario scenario) {
        String threadName = Thread.currentThread().getName();
        
        log.info(CYAN_TEXT + "╔══════════════════════════════════════════════════════════════" + RESET_TEXT);
        log.info(CYAN_TEXT + "║ Thread {}: Scenario Finished: {}" + RESET_TEXT, threadName, scenario.getName());
        log.info(CYAN_TEXT + "║ Status: {}" + RESET_TEXT, scenario.getStatus());
        log.info(CYAN_TEXT + "╚══════════════════════════════════════════════════════════════" + RESET_TEXT);
        
        if (needsWebDriver(scenario)) {
            DriverManager.quitDriver();
        }
    }
    
    /**
     * Определяет, нужен ли WebDriver для данного сценария на основе тегов
     */
    private boolean needsWebDriver(Scenario scenario) {
        boolean needsDriver = scenario.getSourceTagNames().stream()
                .noneMatch(tag -> tag.equalsIgnoreCase("@API") ||
                        tag.equalsIgnoreCase("@DATABASE") ||
                        tag.equalsIgnoreCase("@DB") ||
                        tag.equalsIgnoreCase("@API-ONLY") ||
                        tag.equalsIgnoreCase("@DB-ONLY") ||
                        tag.equalsIgnoreCase("@NO-BROWSER"));
        
        log.debug("Thread {}: Scenario '{}' {} WebDriver",
                Thread.currentThread().getName(), scenario.getName(), needsDriver ? "requires" : "doesn't require");
        return needsDriver;
    }
    
    /**
     * Получить режим headless с учетом обоих параметров
     */
    private boolean getHeadlessMode() {
        if (CONFIG.headless() != null) {
            return CONFIG.headless();
        } else if (CONFIG.browserHeadless() != null) {
            return CONFIG.browserHeadless();
        }
        return false;
    }
}
