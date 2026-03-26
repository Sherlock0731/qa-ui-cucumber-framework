package qa.autotest.framework.drivers;

/**
 * Supported browser types.
 * <p>
 * Заменяет String-параметр "chrome"/"firefox"/etc. во всех местах передачи браузера.
 * Опечатка в имени браузера теперь выловится компилятором, не в runtime.
 * fromString() используется один раз при чтении конфига — дальше везде типизировано.
 */
public enum BrowserType {

    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge"),
    SAFARI("safari");

    private final String configValue;

    BrowserType(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigValue() {
        return configValue;
    }

    /**
     * Парсит значение из конфига (-Dbrowser=chrome).
     * Неизвестное значение — IllegalArgumentException с понятным сообщением.
     */
    public static BrowserType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Browser value is null. Set -Dbrowser=chrome|firefox|edge|safari");
        }
        String lower = value.trim().toLowerCase();
        for (BrowserType type : values()) {
            if (type.configValue.equals(lower)) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                "Unknown browser: '" + value + "'. Supported: chrome, firefox, edge, safari"
        );
    }
}
