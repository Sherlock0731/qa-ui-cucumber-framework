# UI Test Cucumber Framework

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![JUnit](https://img.shields.io/badge/JUnit-5.10.1-red.svg)](https://junit.org/junit5/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.15.0-brightgreen.svg)](https://cucumber.io/)
[![Selenide](https://img.shields.io/badge/Selenide-7.0.4-green.svg)](https://selenide.org/)
[![Allure](https://img.shields.io/badge/Allure-2.25.0-yellow.svg)](http://allure.qatools.ru/)

![Tests](https://github.com/Sherlock0731/qa-ui-cucumber-framework/actions/workflows/test-all.yml/badge.svg)    
[![Allure Report](https://img.shields.io/badge/Allure-Report-orange)](https://sherlock0731.github.io/qa-ui-cucumber-framework/)

Многопоточный BDD-фреймворк для автоматизации UI-тестирования веб-приложений с использованием Cucumber и современного стека технологий.

## Технологический стек

- **Java 17** - язык программирования
- **Maven** - система сборки и управления зависимостями
- **Cucumber 7** - BDD фреймворк с Gherkin синтаксисом
- **JUnit 5** - фреймворк для тестирования с поддержкой параллельности
- **Selenide** - удобная обертка над Selenium WebDriver
- **Selenium WebDriver** - автоматизация браузеров
- **WebDriverManager** - автоматическое управление драйверами браузеров
- **AssertJ** - fluent assertions библиотека
- **Allure Report** - система отчетности
- **Lombok** - уменьшение boilerplate кода
- **SLF4J/Logback** - логирование с поддержкой многопоточности
- **Owner** - управление конфигурациями
- **Docker** - контейнеризация
- **GitHub Actions** - CI/CD pipeline

## Структура проекта

```
qa-ui-cucumber-framework/
├── src/
│   ├── main/
│   │   ├── java/qa/autotest/
│   │   │   ├── app/dto/              # Data Transfer Objects
│   │   │   ├── pages/                # Page Object Model
│   │   │   │   └── flow/             # Page flow abstractions
│   │   │   ├── framework/            # Core framework
│   │   │   │   ├── drivers/          # WebDriver management
│   │   │   │   └── config/           # Configuration
│   │   │   └── core/annotations/     # Custom annotations
│   │   └── resources/
│   │       ├── config/               # Environment configs
│   │       └── logback.xml           # Logging config
│   └── test/
│       ├── java/qa/autotest/
│       │   ├── steps/                # Cucumber Step Definitions
│       │   │   ├── action/           # Action steps (When/И)
│       │   │   ├── validation/       # Validation steps (Then/То)
│       │   │   ├── BaseSteps.java    # Base for all steps
│       │   │   └── Hooks.java        # Before/After hooks
│       │   ├── runners/              # Cucumber runners
│       │   └── listeners/            # Allure listeners
│       └── resources/
│           └── features/             # Gherkin feature files
│               ├── login/            # Login features
│               ├── inventory/        # Inventory features
│               ├── cart/             # Cart features
│               ├── checkout/         # Checkout features
│               └── navigation/       # Navigation features
├── docker/                           # Docker configs
│   ├── Dockerfile
│   └── docker-compose.yml
├── .github/workflows/                # CI/CD pipelines
│   ├── test-all.yml
│   ├── test-login.yml
│   ├── test-inventory.yml
│   ├── test-cart.yml
│   ├── test-checkout.yml
│   └── test-navigation.yml
├── docs/                             # Documentation
└── pom.xml                           # Maven config
```

## Быстрый старт

### Предварительные требования

- Java 17 или выше
- Maven 3.8+
- Docker (опционально)

### Локальный запуск

```bash
# Клонировать репозиторий
git clone <repository-url>
cd qa-ui-cucumber-framework

# ======================================
# ПОСЛЕДОВАТЕЛЬНОЕ ВЫПОЛНЕНИЕ (по умолчанию)
# ======================================

# Запустить все тесты (один тест за раз, один браузер)
mvn clean test

# Явно указать последовательный режим
mvn clean test -Psequential

# ======================================
# ПАРАЛЛЕЛЬНОЕ ВЫПОЛНЕНИЕ
# ======================================

# Запустить тесты с 4 потоками (4 браузера одновременно)
mvn clean test -Pparallel

# Запустить тесты с 8 потоками (8 браузеров одновременно)
mvn clean test -Pparallel-strict -Dthread.count=8

# Запустить с 5 потоками в headless режиме
mvn clean test -Pparallel-strict -Dthread.count=5 -Pheadless

# ======================================
# ЗАПУСК ПО ТЕГАМ CUCUMBER
# ======================================

# Запустить только smoke тесты
mvn clean test -Dcucumber.filter.tags="@smoke"

# Запустить login тесты
mvn clean test -Dcucumber.filter.tags="@login"

# Запустить позитивные тесты cart
mvn clean test -Dcucumber.filter.tags="@cart and @positive"

# Запустить e2e тесты
mvn clean test -Dcucumber.filter.tags="@e2e"

# Smoke тесты параллельно в 4 потока
mvn clean test -Pparallel -Dcucumber.filter.tags="@smoke"

# ======================================
# ALLURE ОТЧЕТЫ
# ======================================

# Сгенерировать и открыть Allure отчет
mvn allure:serve

# Только сгенерировать отчет
mvn allure:report
```

**Важно:**
- По умолчанию тесты запускаются **последовательно** (один браузер за раз)
- Для параллельного запуска обязательно используйте профиль **`-Pparallel`** или **`-Pparallel-strict`**
- Профиль `-Pparallel` использует 4 потока
- Профиль `-Pparallel-strict` позволяет задать количество потоков через `-Dthread.count=N`
- Headless режим активируется профилем `-Pheadless`

### Выбор браузера

```bash
# Chrome (default)
mvn clean test -Dbrowser=chrome

# Firefox
mvn clean test -Dbrowser=firefox

# Edge
mvn clean test -Dbrowser=edge

# Safari (только macOS)
mvn clean test -Dbrowser=safari

# Headless mode (браузеры не видны на экране)
mvn clean test -Pheadless

# Parallel + Headless (рекомендуется для CI/CD)
mvn clean test -Pparallel -Pheadless
```

### Запуск в Docker

```bash
# Запустить все тесты
docker-compose -f docker/docker-compose.yml up

# Запустить с параметрами
THREAD_COUNT=4 BROWSER=chrome docker-compose -f docker/docker-compose.yml up
```

## Переменные окружения

Создайте файл `.env` в корне проекта:

```env
# Application URL
SAUCEDEMO_BASE_URL=[свое значение]

# User Credentials
USER_STANDARD_USERNAME=[свое значение]
USER_STANDARD_PASSWORD=[свое значение]
USER_LOCKED_USERNAME=[свое значение]
USER_LOCKED_PASSWORD=[свое значение]

# Checkout Information
CHECKOUT_FIRSTNAME=[свое значение]
CHECKOUT_LASTNAME=[свое значение]
CHECKOUT_ZIPCODE=1[свое значение]

# Browser Configuration
BROWSER=chrome
HEADLESS=false
THREAD_COUNT=1
```

## Поддерживаемые браузеры

- ✅ Chrome (Windows, Linux, macOS)
- ✅ Firefox (Windows, Linux, macOS)
- ✅ Edge (Windows, macOS)
- ✅ Safari (macOS)

## Многопоточность

Фреймворк полностью поддерживает многопоточное выполнение тестов:

```bash
# Запустить с 8 потоками
mvn clean test -Pparallel-strict -Dthread.count=8

# Запустить с 12 потоками в headless режиме
mvn clean test -Pparallel-strict -Dthread.count=12 -Pheadless
```

Каждый поток создает свой экземпляр WebDriver. Семафор контролирует максимальное количество одновременно открытых браузеров.

## Билингвальные сценарии

Все Feature файлы поддерживают **русский и английский** языки:

```gherkin
@login @smoke @positive
Сценарий: Успешная авторизация со стандартным пользователем
  Когда пользователь открывает страницу логина
  И пользователь вводит имя пользователя "standard_user"
  И пользователь вводит пароль "свое значение"
  И пользователь нажимает кнопку входа
  То пользователь перенаправлен на страницу каталога

@login @smoke @positive  
Scenario: Successful login with standard user
  When user opens login page
  And user enters username "свое значение"
  And user enters password "свое значение"
  And user clicks login button
  Then user is redirected to inventory page
```

## CI/CD

Проект интегрирован с GitHub Actions для автоматического запуска тестов:

- **test-all.yml** - запуск всех тестов
- **test-login.yml** - только login тесты
- **test-inventory.yml** - только inventory тесты
- **test-cart.yml** - только cart тесты
- **test-checkout.yml** - только checkout тесты
- **test-navigation.yml** - только navigation тесты

## Отчеты

После выполнения тестов генерируются:

1. **Allure Report** - подробная визуализация результатов с BDD шагами
2. **Cucumber Reports** - HTML, JSON, XML отчеты
3. **Логи** - детальная информация о выполнении (target/logs/)
4. **Screenshots** - скриншоты при падении тестов (target/screenshots/)

## Доступные теги Cucumber

### По модулям
- `@login` - тесты авторизации (5 сценариев)
- `@inventory` - тесты каталога (6 сценариев)
- `@cart` - тесты корзины (8 сценариев)
- `@checkout` - тесты оформления заказа (8 сценариев)
- `@navigation` - тесты навигации (3 сценария)

### По типу
- `@smoke` - дымовые тесты (быстрая проверка)
- `@positive` - позитивные сценарии
- `@negative` - негативные сценарии
- `@e2e` - end-to-end сценарии
- `@critical` - критичная функциональность
- `@happy-path` - основные пользовательские сценарии

## Документация

Подробная документация доступна в папке `docs/`:

- [Архитектура](docs/ARCHITECTURE.md)
- [Примеры команд](docs/COMMANDS_EXAMPLES.md)
- [Инструкция по запуску](docs/RUN_INSTRUCTIONS.md)
- [Параллельное выполнение тестов](docs/PARALLEL_EXECUTION.md)
- [Настройка WebDriver](docs/WEBDRIVER_CONFIGURATION.md)
- [Матрица тест-кейсов](docs/TEST_CASES_MATRIX.md)

## License

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

## Authors

- **Vitaliy Popravka** - QA Automation Engineer

## Контакты

Для вопросов и предложений создавайте Issue в репозитории.
