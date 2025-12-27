# Архитектура фреймворка

## Обзор

UI Test Cucumber Framework - это современный BDD-фреймворк для автоматизации тестирования веб-приложений, построенный на базе Cucumber 7, Selenide, JUnit 5 и поддерживающий многопоточное выполнение тестов с билингвальными сценариями.

## Структура проекта

```
qa-ui-cucumber-framework/
├── src/main/java/qa/autotest/
│   ├── app/dto/                 # Data Transfer Objects
│   │   ├── UserDto.java
│   │   ├── ProductDto.java
│   │   ├── CartDto.java
│   │   └── CheckoutDto.java
│   ├── pages/                   # Page Object Model
│   │   ├── flow/
│   │   │   └── PageObject.java # Base page with navigation
│   │   ├── LoginPage.java
│   │   ├── InventoryPage.java
│   │   ├── ProductDetailsPage.java
│   │   ├── CartPage.java
│   │   ├── CheckoutStepOnePage.java
│   │   ├── CheckoutStepTwoPage.java
│   │   └── CheckoutCompletePage.java
│   ├── framework/
│   │   ├── drivers/
│   │   │   └── DriverManager.java  # WebDriver management + Semaphore
│   │   └── config/
│   │       ├── TestConfig.java    # Configuration interface
│   │       └── ConfigFactory.java  # Config factory
│   └── core/annotations/
│       ├── Name.java              # Element name annotation
│       ├── Optional.java          # Optional element annotation
│       └── DefaultUrl.java        # Page default URL annotation
└── src/test/java/qa/autotest/
    ├── steps/                      # Cucumber Step Definitions
    │   ├── action/                 # Action steps (When/Когда, And/И)
    │   │   ├── LoginActionSteps.java
    │   │   ├── InventoryActionSteps.java
    │   │   └── CartAndCheckoutActionSteps.java
    │   ├── validation/             # Validation steps (Then/То)
    │   │   ├── LoginValidationSteps.java
    │   │   ├── InventoryValidationSteps.java
    │   │   └── CartAndCheckoutValidationSteps.java
    │   ├── BaseSteps.java          # Base class for all steps
    │   └── Hooks.java              # Before/After hooks
    ├── runners/
    │   └── CucumberRunner.java     # JUnit Platform Suite runner
    └── listeners/
        └── AllureSelenideListener.java  # Allure integration
```

## Ключевые компоненты

### 1. Feature Files (Gherkin)

**Назначение:** Описание бизнес-сценариев на языке Gherkin

**Особенности:**
- Билингвальная поддержка (русский/английский)
- Структурированная организация по модулям
- Использование тегов для категоризации
- Background для общих предусловий

**Пример:**
```gherkin
# language: ru

@login @smoke @positive
Функция: Авторизация пользователя
  Как пользователь SauceDemo
  Я хочу иметь возможность войти в систему
  Чтобы получить доступ к каталогу товаров

  Сценарий: Успешная авторизация со стандартным пользователем
    Когда пользователь открывает страницу логина
    И пользователь вводит имя пользователя "свое значение"
    И пользователь вводит пароль "свое значение"
    И пользователь нажимает кнопку входа
    То пользователь перенаправлен на страницу каталога
```

### 2. Step Definitions

**Назначение:** Реализация шагов Gherkin на Java

**Архитектура:**
```
BaseSteps (общие методы доступа к Page Objects)
  ├── Action Steps (When/Когда, And/И)
  │   ├── LoginActionSteps
  │   ├── InventoryActionSteps
  │   └── CartAndCheckoutActionSteps
  └── Validation Steps (Then/То, And/И)
      ├── LoginValidationSteps
      ├── InventoryValidationSteps
      └── CartAndCheckoutValidationSteps
```

**Принципы:**
- **Разделение ответственности:** Action steps выполняют действия, Validation steps проверяют результаты
- **Билингвальность:** Каждый метод имеет аннотации `@When`/`@Когда`, `@Then`/`@То`
- **Allure Steps:** Каждый метод аннотирован `@Step` для отчётности
- **Логирование:** Все шаги логируются с уровнем INFO

**Пример Action Step:**
```java
@When("user enters username {string}")
@Когда("пользователь вводит имя пользователя {string}")
@Step("Ввести имя пользователя: {username}")
public void userEntersUsername(String username) {
    log.info("Entering username: {}", username);
    loginPage().getUsernameInput().setValue(username);
}
```

**Пример Validation Step:**
```java
@Then("user is redirected to inventory page")
@То("пользователь перенаправлен на страницу каталога")
@Step("Проверить переход на страницу каталога")
public void userIsRedirectedToInventoryPage() {
    log.info("Verifying redirect to inventory page");
    inventoryPage().getPageTitle()
        .shouldBe(Condition.visible)
        .shouldHave(Condition.text("Products"));
}
```

### 3. Page Object Model

**Назначение:** Инкапсуляция логики взаимодействия с UI

**Иерархия:**
```
PageObject (flow/PageObject.java - базовый класс с navigation)
  ├── LoginPage
  ├── InventoryPage
  ├── ProductDetailsPage
  ├── CartPage
  ├── CheckoutStepOnePage
  ├── CheckoutStepTwoPage
  └── CheckoutCompletePage
```

**Особенности:**
- Использование аннотаций `@Name`, `@Optional`, `@DefaultUrl`
- Selenide API для упрощения работы с элементами
- Fluent interface методы
- Метод `open()` для открытия страницы

**Пример:**
```java
@DefaultUrl("/")
public class LoginPage extends PageObject {
    
    @Name("Username input")
    private SelenideElement usernameInput = $("#user-name");
    
    @Name("Password input")
    private SelenideElement passwordInput = $("#password");
    
    @Name("Login button")
    private SelenideElement loginButton = $("#login-button");
    
    @Optional
    @Name("Error message")
    private SelenideElement errorMessage = $("[data-test='error']");
    
    // Getters for step definitions
    public SelenideElement getUsernameInput() { return usernameInput; }
    public SelenideElement getPasswordInput() { return passwordInput; }
    public SelenideElement getLoginButton() { return loginButton; }
    public SelenideElement getErrorMessage() { return errorMessage; }
}
```

### 4. WebDriver Management с Semaphore

**DriverManager** управляет жизненным циклом WebDriver с контролем количества браузеров:

**Возможности:**
- Автоматическое управление драйверами (WebDriverManager)
- Поддержка 4 браузеров: Chrome, Firefox, Edge, Safari
- Thread-safe реализация через ThreadLocal
- **Semaphore для ограничения количества браузеров**
- Поддержка headless режима
- Интеграция с Selenium Grid
- Настройка timeouts и размера окна

**Пример:**
```java
public class DriverManager {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static Semaphore browserSemaphore;
    
    public static void initializeSemaphore(int maxBrowsers) {
        if (browserSemaphore == null) {
            browserSemaphore = new Semaphore(maxBrowsers);
            log.info("Semaphore initialized with {} permits", maxBrowsers);
        }
    }
    
    public static void initDriver() {
        try {
            browserSemaphore.acquire(); // Ожидание доступного слота
            log.info("Permit acquired. Active browsers: {}", 
                    maxBrowsers - browserSemaphore.availablePermits());
            
            WebDriver webDriver = createDriver();
            driver.set(webDriver);
            WebDriverRunner.setWebDriver(webDriver);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static void quitDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            webDriver.quit();
            driver.remove();
            browserSemaphore.release(); // Освобождение слота
        }
    }
}
```

### 5. Configuration Management

**Использует Owner library для управления конфигурациями:**

**Приоритет загрузки:**
1. System properties (-Dkey=value)
2. Environment variables
3. Environment-specific properties (local.properties, ci.properties)
4. Default properties (default.properties)

**Пример:**
```java
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
    "system:properties",
    "system:env",
    "classpath:config/${env}.properties",
    "classpath:config/default.properties"
})
public interface TestConfig extends Config {
    @Key("browser")
    String browser();
    
    @Key("headless")
    Boolean headless();
    
    @Key("thread.count")
    Integer threadCount();
}
```

### 6. Hooks для управления WebDriver

**Hooks** обеспечивают управление жизненным циклом WebDriver:

**Возможности:**
- Инициализация WebDriver перед сценарием
- Закрытие WebDriver после сценария
- Логирование начала и конца сценария
- Регистрация Allure Selenide listener

**Пример:**
```java
public class Hooks {
    @Before
    public void setUp(Scenario scenario) {
        log.info("Starting scenario: {}", scenario.getName());
        
        // Регистрация Allure listener
        SelenideLogger.addListener("AllureSelenide", 
            new AllureSelenideListener());
        
        // Инициализация WebDriver если нужен для сценария
        if (scenarioRequiresWebDriver(scenario)) {
            DriverManager.initDriver();
        }
    }
    
    @After
    public void tearDown(Scenario scenario) {
        if (scenarioRequiresWebDriver(scenario)) {
            DriverManager.quitDriver();
        }
        
        log.info("Finished scenario: {} - Status: {}", 
            scenario.getName(), scenario.getStatus());
    }
}
```

### 7. Cucumber Runner

**CucumberRunner** настраивает выполнение Cucumber с JUnit Platform:

**Конфигурация:**
```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters({
    @ConfigurationParameter(key = GLUE_PROPERTY_NAME, 
        value = "qa.autotest.steps"),
    @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
        value = "pretty, " +
                "html:target/cucumber-reports/cucumber.html, " +
                "json:target/cucumber-reports/cucumber.json, " +
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"),
    @ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, 
        value = "not @skip")
    // Параллельность управляется через системные свойства
})
public class CucumberRunner {
}
```

## Паттерны проектирования

### 1. Behavior-Driven Development (BDD)
Использование Gherkin для описания поведения системы

### 2. Page Object Pattern
Инкапсуляция логики страниц в отдельные классы

### 3. Step Definitions Pattern
Разделение на Action и Validation steps

### 4. Builder Pattern
Гибкое создание DTO объектов

### 5. Factory Pattern
Создание конфигураций и драйверов

### 6. Singleton Pattern
Управление конфигурацией (thread-safe)

### 7. Fluent Interface
Цепочка вызовов методов для читаемости

### 8. Semaphore Pattern
Контроль количества одновременных браузеров

## Многопоточность

### Thread-Local WebDriver
Каждый поток имеет свой экземпляр WebDriver:
```java
private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
```

### Semaphore для контроля браузеров
Ограничение максимального количества одновременно открытых браузеров:
```java
private static Semaphore browserSemaphore;
browserSemaphore = new Semaphore(maxBrowsers);
```

### Параллельное выполнение Cucumber
Конфигурация через `junit-platform.properties`:
```properties
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.strategy=fixed
cucumber.execution.parallel.config.fixed.parallelism=4
```

И через `pom.xml`:
```xml
<systemPropertyVariables>
    <cucumber.execution.parallel.enabled>true</cucumber.execution.parallel.enabled>
    <cucumber.execution.parallel.config.fixed.parallelism>4</cucumber.execution.parallel.config.fixed.parallelism>
</systemPropertyVariables>
```

### Логирование
Каждый поток пишет в отдельный лог-файл:
```xml
<appender name="SIFT" class="ch.qos.logback.classic.sift.SiftingAppender">
    <discriminator>
        <key>threadName</key>
    </discriminator>
</appender>
```

## Интеграция с Allure

### Аннотации
- `@Epic` - группировка по эпикам
- `@Feature` - группировка по функциональности  
- `@Story` - группировка по user stories
- `@Step` - описание шагов Cucumber
- `@Severity` - приоритет теста

### Cucumber + Allure
- Автоматическое создание шагов из Gherkin
- Группировка по Feature файлам
- Отображение тегов
- Вложенные шаги из step definitions

### Вложения
- Скриншоты при ошибках (автоматически через AllureSelenideListener)
- Логи выполнения
- HTML исходный код страницы
- Page source при падении

## CI/CD Integration

### GitHub Actions
- Автоматический запуск тестов по расписанию и при push
- Параллельное выполнение (4-8 потоков)
- Сохранение артефактов (Allure отчеты, логи, скриншоты)
- Поддержка разных браузеров
- Headless режим для экономии ресурсов

### Docker
- Изолированное окружение
- Предустановленные браузеры
- Headless режим по умолчанию
- Параметризованный запуск

## Лучшие практики

1. **Использование BDD** для описания поведения системы на языке бизнеса
2. **Билингвальные сценарии** для международных команд
3. **Разделение Action и Validation steps** для чистоты кода
4. **Page Objects** для инкапсуляции логики UI
5. **DTO для моделирования данных** вместо Map/JSON
6. **Fluent interface** для читаемости тестов
7. **Thread-safe реализация** для параллельности
8. **Semaphore контроль** для стабильности при параллельном запуске
9. **Централизованная конфигурация** через Owner
10. **Детальное логирование** с поддержкой многопоточности
11. **Allure steps** для прозрачных отчетов
12. **Автоматическое управление драйверами** через WebDriverManager
13. **Использование тегов Cucumber** для гибкой фильтрации тестов

## Расширяемость

Фреймворк легко расширяется:

1. **Добавление новых feature файлов** - просто создайте новый .feature файл
2. **Добавление новых step definitions** - создайте класс в action/ или validation/
3. **Добавление новых Page Objects** - расширьте PageObject
4. **Добавление новых браузеров** - добавьте case в DriverManager
5. **Добавление новых языков** - добавьте аннотации на новом языке

Фреймворк готов к промышленному использованию и может быть легко адаптирован под специфические требования проекта.
