package qa.autotest.framework.config;

import lombok.extern.slf4j.Slf4j;

/**
 * Factory class for creating and managing TestConfig instances.
 * Implements thread-safe singleton via double-checked locking.
 */
@Slf4j
public class ConfigFactory {

    private static volatile TestConfig config;

    private ConfigFactory() {
    }

    public static TestConfig getConfig() {
        if (config == null) {
            synchronized (ConfigFactory.class) {
                if (config == null) {
                    String env = System.getProperty("env", System.getenv("ENV"));
                    if (env == null) {
                        env = "local";
                    }

                    System.setProperty("env", env);
                    log.info("Initializing configuration for environment: {}", env);

                    TestConfig created = org.aeonbits.owner.ConfigFactory.create(TestConfig.class);
                    validateConfig(created, env);
                    config = created;

                    log.info("Configuration initialized: browser={}, headless={}, threads={}",
                            config.browser(), config.browserHeadless(), config.threadCount());
                }
            }
        }
        return config;
    }

    /**
     * Fail-fast валидация конфига при инициализации.
     * Падает с понятным сообщением до первого обращения к WebDriver,
     * а не с NPE внутри драйвера через 30 секунд.
     * <p>
     * local environment: url и credentials могут отсутствовать в default.properties —
     * разработчик обязан создать local.properties (см. .env.example).
     * ci environment: все переменные обязательны, иначе тесты не имеют смысла.
     */
    private static void validateConfig(TestConfig cfg, String env) {
        if (isBlank(cfg.sauceDemoBaseUrl())) {
            throw new IllegalStateException(
                    "saucedemo.base.url is not set for environment '" + env + "'. " +
                            "For local: create src/main/resources/config/local.properties (see .env.example). " +
                            "For CI: set SAUCEDEMO_BASE_URL environment variable."
            );
        }
        if (isBlank(cfg.standardUsername())) {
            throw new IllegalStateException(
                    "user.standard.username is not set for environment '" + env + "'. " +
                            "For local: add to local.properties. For CI: set USER_STANDARD_USERNAME."
            );
        }
        if (isBlank(cfg.standardPassword())) {
            throw new IllegalStateException(
                    "user.standard.password is not set for environment '" + env + "'. " +
                            "For local: add to local.properties. For CI: set USER_STANDARD_PASSWORD."
            );
        }
        if (isBlank(cfg.browser())) {
            throw new IllegalStateException(
                    "browser is not set. Add 'browser=chrome' to default.properties or pass -Dbrowser=chrome."
            );
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
