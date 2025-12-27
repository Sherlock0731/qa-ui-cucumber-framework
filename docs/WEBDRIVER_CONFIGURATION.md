# Настройка WebDriver

## Обзор

Фреймворк использует WebDriverManager для автоматического управления драйверами браузеров и поддерживает thread-safe выполнение через ThreadLocal и Semaphore.

## Архитектура Driver Manager

### Основные компоненты

```java
public class DriverManager {
    // ThreadLocal для изоляции между потоками
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
    // Semaphore для контроля количества браузеров
    private static Semaphore browserSemaphore;
    
    // Инициализация
    public static void initDriver() { ... }
    
    // Получение драйвера
    public static WebDriver getDriver() { ... }
    
    // Закрытие драйвера
    public static void quitDriver() { ... }
}
```

## Поддерживаемые браузеры

### Chrome

**По умолчанию**

```bash
mvn clean test
# или явно
mvn clean test -Dbrowser=chrome
```

**Возможности:**
- Headless режим
- Кастомные опции
- Chrome DevTools Protocol
- Расширения (extensions)

**Опции:**
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--start-maximized");
options.addArguments("--disable-blink-features=AutomationControlled");
options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
```

### Firefox

```bash
mvn clean test -Dbrowser=firefox
```

**Возможности:**
- Headless режим
- Профили Firefox
- Расширения (add-ons)
- Geckodriver

**Опции:**
```java
FirefoxOptions options = new FirefoxOptions();
options.addArguments("--width=1920");
options.addArguments("--height=1080");
```

### Edge

```bash
mvn clean test -Dbrowser=edge
```

**Возможности:**
- Headless режим
- Chromium-based функции
- IE Mode (опционально)

**Поддержка:**
- Windows
- macOS

### Safari

```bash
mvn clean test -Dbrowser=safari
```

**Ограничения:**
- Только macOS
- Headless НЕ поддерживается
- Требуется включение Remote Automation в Safari

**Включение:**
1. Открыть Safari → Preferences → Advanced
2. Включить "Show Develop menu in menu bar"
3. Develop → Allow Remote Automation

## Headless режим

### Включение headless

```bash
# Через профиль
mvn clean test -Pheadless

# Через системное свойство
mvn clean test -Dheadless=true

# Через environment variable
export HEADLESS=true
mvn clean test
```

### Реализация

```java
private static WebDriver createChromeDriver(boolean headless) {
    ChromeOptions options = new ChromeOptions();
    
    if (headless) {
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        log.info("Chrome headless mode enabled");
    }
    
    WebDriver driver = new ChromeDriver(options);
    return driver;
}
```

### Преимущества headless

- Быстрее на 10-15%
- Меньше RAM (~30-40%)
- Меньше CPU/GPU
- Идеально для CI/CD
- Стабильнее при параллельном запуске

## WebDriverManager

### Автоматическое управление

Фреймворк использует WebDriverManager для автоматической загрузки и управления драйверами:

```java
import io.github.bonigarcia.wdm.WebDriverManager;

// Chrome
WebDriverManager.chromedriver().setup();
WebDriver driver = new ChromeDriver();

// Firefox
WebDriverManager.firefoxdriver().setup();
WebDriver driver = new FirefoxDriver();

// Edge
WebDriverManager.edgedriver().setup();
WebDriver driver = new EdgeDriver();
```

### Преимущества
- ✅ Автоматическая загрузка драйверов
- ✅ Совместимость с версией браузера
- ✅ Кэширование драйверов
- ✅ Поддержка прокси
- ✅ Поддержка offline режима

### Конфигурация

```java
WebDriverManager.chromedriver()
    .browserVersion("119.0")  // Конкретная версия
    .cachePath("/custom/path") // Кастомный путь кэша
    .proxy("http://proxy:8080") // Прокси
    .setup();
```

## ThreadLocal изоляция

### Назначение

ThreadLocal обеспечивает изоляцию WebDriver между параллельными потоками.

### Реализация

```java
private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

public static void initDriver() {
    WebDriver webDriver = createDriver();
    driver.set(webDriver); // Каждый поток получает свой экземпляр
    WebDriverRunner.setWebDriver(webDriver);
}

public static WebDriver getDriver() {
    return driver.get(); // Получение драйвера текущего потока
}

public static void quitDriver() {
    WebDriver webDriver = driver.get();
    if (webDriver != null) {
        webDriver.quit();
        driver.remove(); // ВАЖНО: Очистка ThreadLocal
    }
}
```

### Важно
- ✅ Всегда используйте `driver.remove()` в finally блоке
- ✅ Не храните WebElement в статических полях
- ✅ Получайте драйвер через `getDriver()` или `WebDriverRunner.getWebDriver()`

## Semaphore контроль

### Назначение

Semaphore ограничивает максимальное количество одновременно открытых браузеров.

### Реализация

```java
private static Semaphore browserSemaphore;

public static void initializeSemaphore(int maxBrowsers) {
    if (browserSemaphore == null) {
        browserSemaphore = new Semaphore(maxBrowsers);
        log.info("Browser Semaphore initialized with {} permits", maxBrowsers);
    }
}

public static void initDriver() {
    try {
        browserSemaphore.acquire(); // Ожидание свободного слота
        log.info("Browser permit acquired. Active browsers: {}", 
                maxBrowsers - browserSemaphore.availablePermits());
        
        WebDriver webDriver = createDriver();
        driver.set(webDriver);
        
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Failed to acquire browser permit", e);
    }
}

public static void quitDriver() {
    WebDriver webDriver = driver.get();
    if (webDriver != null) {
        try {
            webDriver.quit();
        } finally {
            driver.remove();
            browserSemaphore.release(); // Освобождение слота
            log.info("Browser permit released");
        }
    }
}
```

### Настройка

```bash
# 4 браузера максимум
mvn clean test -Pparallel

# 8 браузеров максимум
mvn clean test -Pparallel-strict -Dthread.count=8
```

## Timeouts

### Настройка timeouts

```java
private static void configureTimeouts(WebDriver driver, TestConfig config) {
    driver.manage().timeouts()
        .pageLoadTimeout(Duration.ofSeconds(30))
        .implicitlyWait(Duration.ofSeconds(10))
        .scriptTimeout(Duration.ofSeconds(30));
}
```

### Selenide timeouts

Selenide имеет свои таймауты:

```java
Configuration.timeout = 10000;        // 10 секунд для элементов
Configuration.pageLoadTimeout = 30000; // 30 секунд для страниц
Configuration.pollingInterval = 100;   // 100 мс между проверками
```

### Переопределение

```bash
# Через системные свойства
mvn clean test -Dselenide.timeout=15000

# Через config/local.properties
timeout.explicit=15000
```

## Размер окна браузера

### Настройка размера

```java
private static void configureWindowSize(WebDriver driver) {
    if (config.browserHeadless()) {
        // Headless - фиксированный размер
        driver.manage().window().setSize(new Dimension(1920, 1080));
    } else {
        // GUI - максимизация
        driver.manage().window().maximize();
    }
}
```

### Кастомный размер

```bash
mvn clean test -Dbrowser.width=1440 -Dbrowser.height=900
```

## Chrome опции

### Основные опции

```java
ChromeOptions options = new ChromeOptions();

// Headless
options.addArguments("--headless=new");
options.addArguments("--window-size=1920,1080");

// Производительность
options.addArguments("--disable-gpu");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");

// Безопасность
options.addArguments("--disable-blink-features=AutomationControlled");
options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
options.setExperimentalOption("useAutomationExtension", false);

// Логирование
options.setCapability("goog:loggingPrefs", Map.of("browser", "ALL"));

// User agent
options.addArguments("--user-agent=Custom User Agent String");
```

## Firefox опции

### Основные опции

```java
FirefoxOptions options = new FirefoxOptions();

// Headless
options.addArguments("-headless");
options.addArguments("--width=1920");
options.addArguments("--height=1080");

// Профиль
FirefoxProfile profile = new FirefoxProfile();
profile.setPreference("browser.download.folderList", 2);
profile.setPreference("browser.download.dir", "/path/to/downloads");
options.setProfile(profile);

// Логирование
System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
```

## Selenium Grid

### Конфигурация

```java
private static WebDriver createRemoteDriver(String browser, boolean headless, String remoteUrl) {
    try {
        URL gridUrl = new URL(remoteUrl);
        
        Capabilities capabilities = switch (browser) {
            case "chrome" -> getChromeOptions(headless);
            case "firefox" -> getFirefoxOptions(headless);
            case "edge" -> getEdgeOptions(headless);
            default -> getChromeOptions(headless);
        };
        
        return new RemoteWebDriver(gridUrl, capabilities);
        
    } catch (MalformedURLException e) {
        throw new RuntimeException("Invalid remote URL: " + remoteUrl, e);
    }
}
```

### Использование

```bash
# Через properties
mvn clean test -Dbrowser.remote.url=http://selenium-grid:4444

# Через environment variable
export BROWSER_REMOTE_URL=http://selenium-grid:4444
mvn clean test
```

## Локальные драйверы

### Использование без WebDriverManager

```bash
# Включить использование локальных драйверов
mvn clean test -Dwebdriver.use.local=true

# Указать путь к chromedriver
mvn clean test -Dwebdriver.use.local=true -Dwebdriver.chrome.driver=/path/to/chromedriver
```

### Конфигурация

```properties
# local.properties
webdriver.use.local=true
webdriver.chrome.driver=/usr/local/bin/chromedriver
webdriver.firefox.driver=/usr/local/bin/geckodriver
webdriver.edge.driver=/usr/local/bin/msedgedriver
```

## Troubleshooting

### Проблема: ChromeDriver version mismatch

**Причина:** Версия Chrome и ChromeDriver не совпадают

**Решение:**
```bash
# Очистить кэш WebDriverManager
rm -rf ~/.cache/selenium/

# Пересобрать
mvn clean test
```

### Проблема: Chrome не запускается в headless

**Решение:**
```bash
# Используйте новый headless режим
--headless=new

# Вместо старого
--headless
```

### Проблема: Firefox не находит geckodriver

**Решение:**
```bash
# Установить через WebDriverManager (автоматически)
# Или вручную
sudo apt install firefox-geckodriver  # Linux
brew install geckodriver               # macOS
```

### Проблема: Safari не подключается

**Решение:**
1. Включить Remote Automation в Safari
2. Запустить `safaridriver --enable`
3. Перезапустить Safari

### Проблема: Браузеры не закрываются

**Решение:**
```bash
# Убить все зависшие процессы
pkill -f chrome
pkill -f firefox
pkill -f msedge

# Проверить @After hook
```

## Best Practices

1. **Всегда используйте WebDriverManager** - автоматическое управление драйверами
2. **ThreadLocal для параллельности** - изоляция между потоками
3. **Semaphore для контроля** - ограничение количества браузеров
4. **Headless для CI/CD** - экономия ресурсов
5. **Правильная очистка** - driver.remove() в finally блоке
6. **Таймауты** - разумные значения (не слишком большие)
7. **Логирование** - подробные логи для debugging

Фреймворк предоставляет гибкую и надёжную систему управления WebDriver для стабильного выполнения тестов.
