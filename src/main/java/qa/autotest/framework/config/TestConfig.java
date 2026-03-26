package qa.autotest.framework.config;

import org.aeonbits.owner.Config;

/**
 * Configuration interface for test environment properties.
 * Uses Owner library for configuration management.
 * <p>
 * Configuration priority (highest to lowest):
 * 1. System properties  (-Dkey=value)
 * 2. Environment variables
 * 3. Environment-specific properties (local.properties, ci.properties, etc.)
 * 4. Default properties (default.properties)
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config/${env}.properties",
        "classpath:config/default.properties"
})
public interface TestConfig extends Config {

    @Key("env")
    String environment();

    @Key("saucedemo.base.url")
    String sauceDemoBaseUrl();

    @Key("user.standard.username")
    String standardUsername();

    @Key("user.standard.password")
    String standardPassword();

    @Key("user.locked.username")
    String lockedUsername();

    @Key("user.locked.password")
    String lockedPassword();

    @Key("user.problem.username")
    String problemUsername();

    @Key("user.problem.password")
    String problemPassword();

    @Key("user.performance.username")
    String performanceUsername();

    @Key("user.performance.password")
    String performancePassword();

    @Key("checkout.firstname")
    String checkoutFirstName();

    @Key("checkout.lastname")
    String checkoutLastName();

    @Key("checkout.zipcode")
    String checkoutZipCode();

    @Key("browser")
    String browser();

    /**
     * Единственный ключ для headless-режима.
     * CLI: -Dbrowser.headless=true
     * В ci.properties: browser.headless=true
     */
    @Key("browser.headless")
    @DefaultValue("false")
    Boolean browserHeadless();

    @Key("browser.width")
    Integer browserWidth();

    @Key("browser.height")
    Integer browserHeight();

    @Key("browser.remote.url")
    String browserRemoteUrl();

    @Key("webdriver.use.local")
    @DefaultValue("false")
    Boolean useLocalDrivers();

    @Key("webdriver.chrome.driver")
    String chromeDriverPath();

    @Key("webdriver.firefox.driver")
    String firefoxDriverPath();

    @Key("webdriver.edge.driver")
    String edgeDriverPath();

    @Key("timeout.page.load")
    Long pageLoadTimeout();

    @Key("timeout.explicit")
    Long explicitTimeout();

    @Key("thread.count")
    Integer threadCount();

    @Key("screenshot.on.failure")
    Boolean screenshotOnFailure();

    @Key("screenshot.folder")
    String screenshotFolder();

    @Key("logging.detailed")
    Boolean detailedLogging();

    @Key("retry.attempts")
    Integer retryAttempts();
}
