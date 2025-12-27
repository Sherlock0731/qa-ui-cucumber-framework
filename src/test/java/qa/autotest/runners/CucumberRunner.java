package qa.autotest.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Cucumber Test Runner
 * Executes all feature files with specified configuration
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters({
    @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "qa.autotest.steps"),
    @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = 
        "pretty, " +
        "html:target/cucumber-reports/cucumber.html, " +
        "json:target/cucumber-reports/cucumber.json, " +
        "junit:target/cucumber-reports/cucumber.xml, " +
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"),
    @ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @skip"),
    @ConfigurationParameter(key = SNIPPET_TYPE_PROPERTY_NAME, value = "camelcase"),
    @ConfigurationParameter(key = EXECUTION_DRY_RUN_PROPERTY_NAME, value = "false")
})
public class CucumberRunner {
}
