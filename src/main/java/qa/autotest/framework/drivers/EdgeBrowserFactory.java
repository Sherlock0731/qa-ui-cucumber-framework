package qa.autotest.framework.drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import qa.autotest.framework.config.TestConfig;

@Slf4j
public class EdgeBrowserFactory implements BrowserFactory {

    @Override
    public WebDriver create(boolean headless, TestConfig config) {
        setupDriver(config);
        return new EdgeDriver(buildOptions(headless));
    }

    @Override
    public org.openqa.selenium.Capabilities buildOptionsForRemote(boolean headless) {
        return buildOptions(headless);
    }

    private void setupDriver(TestConfig config) {
        if (Boolean.TRUE.equals(config.useLocalDrivers()) && config.edgeDriverPath() != null) {
            log.info("Using local Edge driver: {}", config.edgeDriverPath());
            System.setProperty("webdriver.edge.driver", config.edgeDriverPath());
        } else {
            log.info("Using WebDriverManager for Edge");
            WebDriverManager.edgedriver().setup();
        }
    }

    private EdgeOptions buildOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        return options;
    }
}
