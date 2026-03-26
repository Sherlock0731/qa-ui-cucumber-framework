package qa.autotest.framework.drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import qa.autotest.framework.config.TestConfig;

@Slf4j
public class FirefoxBrowserFactory implements BrowserFactory {

    @Override
    public WebDriver create(boolean headless, TestConfig config) {
        setupDriver(config);
        return new FirefoxDriver(buildOptions(headless));
    }

    @Override
    public org.openqa.selenium.Capabilities buildOptionsForRemote(boolean headless) {
        return buildOptions(headless);
    }

    private void setupDriver(TestConfig config) {
        if (Boolean.TRUE.equals(config.useLocalDrivers()) && config.firefoxDriverPath() != null) {
            log.info("Using local Firefox driver: {}", config.firefoxDriverPath());
            System.setProperty("webdriver.gecko.driver", config.firefoxDriverPath());
        } else {
            log.info("Using WebDriverManager for Firefox");
            WebDriverManager.firefoxdriver().setup();
        }
    }

    private FirefoxOptions buildOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }
}
