package qa.autotest.framework.drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import qa.autotest.framework.config.TestConfig;

import java.util.Map;

@Slf4j
public class ChromeBrowserFactory implements BrowserFactory {

    @Override
    public WebDriver create(boolean headless, TestConfig config) {
        setupDriver(config);
        return new ChromeDriver(buildOptions(headless));
    }

    @Override
    public org.openqa.selenium.Capabilities buildOptionsForRemote(boolean headless) {
        return buildOptions(headless);
    }

    private void setupDriver(TestConfig config) {
        if (Boolean.TRUE.equals(config.useLocalDrivers()) && config.chromeDriverPath() != null) {
            log.info("Using local Chrome driver: {}", config.chromeDriverPath());
            System.setProperty("webdriver.chrome.driver", config.chromeDriverPath());
        } else {
            log.info("Using WebDriverManager for Chrome");
            WebDriverManager.chromedriver().setup();
        }
    }

    private ChromeOptions buildOptions(boolean headless) {
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
}
