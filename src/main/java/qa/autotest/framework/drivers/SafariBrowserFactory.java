package qa.autotest.framework.drivers;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import qa.autotest.framework.config.TestConfig;

@Slf4j
public class SafariBrowserFactory implements BrowserFactory {

    @Override
    public WebDriver create(boolean headless, TestConfig config) {
        if (headless) {
            log.warn("Safari does not support headless mode — running in headed mode");
        }
        return new SafariDriver(new SafariOptions());
    }
}
