# Примеры команд запуска

## Основные команды

### Последовательное выполнение

```bash
# Запустить все тесты (один за раз)
mvn clean test

# Явно указать последовательный режим
mvn clean test -Psequential
```

### Параллельное выполнение

```bash
# 4 потока (по умолчанию для профиля parallel)
mvn clean test -Pparallel

# 8 потоков
mvn clean test -Pparallel-strict -Dthread.count=8

# 12 потоков
mvn clean test -Pparallel-strict -Dthread.count=12
```

### Headless режим

```bash
# Последовательно в headless
mvn clean test -Pheadless

# Параллельно (4 потока) в headless
mvn clean test -Pparallel -Pheadless

# 8 потоков в headless
mvn clean test -Pparallel-strict -Dthread.count=8 -Pheadless
```

## Фильтрация по тегам Cucumber

### По модулям

```bash
# Только login тесты
mvn clean test -Dcucumber.filter.tags="@login"

# Только inventory тесты
mvn clean test -Dcucumber.filter.tags="@inventory"

# Только cart тесты
mvn clean test -Dcucumber.filter.tags="@cart"

# Только checkout тесты
mvn clean test -Dcucumber.filter.tags="@checkout"

# Только navigation тесты
mvn clean test -Dcucumber.filter.tags="@navigation"
```

### По типу тестов

```bash
# Только smoke тесты
mvn clean test -Dcucumber.filter.tags="@smoke"

# Только позитивные тесты
mvn clean test -Dcucumber.filter.tags="@positive"

# Только негативные тесты
mvn clean test -Dcucumber.filter.tags="@negative"

# Только e2e тесты
mvn clean test -Dcucumber.filter.tags="@e2e"

# Только critical тесты
mvn clean test -Dcucumber.filter.tags="@critical"
```

### Комбинации тегов

```bash
# Login И позитивные
mvn clean test -Dcucumber.filter.tags="@login and @positive"

# Cart И smoke
mvn clean test -Dcucumber.filter.tags="@cart and @smoke"

# Checkout ИЛИ cart
mvn clean test -Dcucumber.filter.tags="@checkout or @cart"

# Smoke, но НЕ негативные
mvn clean test -Dcucumber.filter.tags="@smoke and not @negative"

# Все кроме skip
mvn clean test -Dcucumber.filter.tags="not @skip"
```

## Выбор браузера

```bash
# Chrome (по умолчанию)
mvn clean test -Dbrowser=chrome

# Firefox
mvn clean test -Dbrowser=firefox

# Edge
mvn clean test -Dbrowser=edge

# Safari (только macOS)
mvn clean test -Dbrowser=safari
```

## Комбинированные команды

```bash
# Smoke тесты, параллельно, в headless
mvn clean test -Pparallel -Pheadless -Dcucumber.filter.tags="@smoke"

# Login тесты, 8 потоков, Firefox
mvn clean test -Pparallel-strict -Dthread.count=8 -Dbrowser=firefox -Dcucumber.filter.tags="@login"

# E2E тесты, последовательно, Chrome headless
mvn clean test -Psequential -Pheadless -Dcucumber.filter.tags="@e2e"

# Все позитивные тесты, 4 потока
mvn clean test -Pparallel -Dcucumber.filter.tags="@positive"

# Cart и checkout, 6 потоков, headless
mvn clean test -Pparallel-strict -Dthread.count=6 -Pheadless -Dcucumber.filter.tags="@cart or @checkout"
```

## Allure отчёты

```bash
# Запустить тесты и открыть Allure отчёт
mvn clean test allure:serve

# Только сгенерировать отчёт (без запуска тестов)
mvn allure:report

# Запустить тесты с параллелизмом и открыть отчёт
mvn clean test -Pparallel allure:serve

# Посмотреть историю выполнения
mvn allure:serve
```

## Окружения

```bash
# Local окружение (по умолчанию)
mvn clean test -Denv=local

# CI окружение
mvn clean test -Denv=ci

# С переопределением URL
mvn clean test -Dsaucedemo.base.url=https://custom-url.com
```

## Логирование

```bash
# С подробным логированием
mvn clean test -Dlogging.detailed=true

# Без логов в консоль (только в файл)
mvn clean test -Dlogback.configurationFile=src/main/resources/logback-silent.xml

# Debug уровень логирования
mvn clean test -Dlog.level=DEBUG
```

## Docker

```bash
# Запустить в Docker с дефолтными настройками
docker-compose -f docker/docker-compose.yml up

# С параметрами
THREAD_COUNT=4 BROWSER=chrome docker-compose -f docker/docker-compose.yml up

# Headless режим в Docker
HEADLESS=true THREAD_COUNT=8 docker-compose -f docker/docker-compose.yml up

# Только smoke тесты в Docker
TEST_TAGS="@smoke" docker-compose -f docker/docker-compose.yml up
```

## CI/CD примеры

```bash
# GitHub Actions локально (через act)
act -j test-all

# Jenkins
jenkins build qa-ui-cucumber-tests -p THREAD_COUNT=8 -p BROWSER=chrome

# GitLab CI
gitlab-runner exec docker test-job
```

## Troubleshooting команды

```bash
# Пропустить тесты, только собрать проект
mvn clean install -DskipTests

# Запустить с Maven debug
mvn clean test -X -Pparallel

# Проверить зависимости
mvn dependency:tree

# Очистить всё
mvn clean

# Проверить конфигурацию Cucumber
mvn test -Dcucumber.options="--dry-run --glue qa.autotest.steps"
```

## Полезные комбинации для разработки

```bash
# Быстрая проверка - только smoke в headless
mvn clean test -Pheadless -Dcucumber.filter.tags="@smoke"

# Отладка одного теста
mvn clean test -Dcucumber.filter.tags="@login and @positive"

# Проверка параллелизма - 2 потока на smoke
mvn clean test -Pparallel-strict -Dthread.count=2 -Dcucumber.filter.tags="@smoke"

# Full regression в headless
mvn clean test -Pparallel-strict -Dthread.count=8 -Pheadless

# CI/CD simulation
mvn clean test -Pparallel -Pheadless -Denv=ci -Dcucumber.filter.tags="not @skip"
```

## Переменные окружения

```bash
# Через export (Linux/macOS)
export BROWSER=firefox
export THREAD_COUNT=4
export HEADLESS=true
mvn clean test -Pparallel

# Через set (Windows)
set BROWSER=firefox
set THREAD_COUNT=4
mvn clean test -Pparallel

# Inline (Linux/macOS)
BROWSER=firefox THREAD_COUNT=4 mvn clean test -Pparallel
```

## Примеры для разных сценариев

```bash
# Локальная разработка - быстрая проверка
mvn clean test -Dcucumber.filter.tags="@smoke"

# Pull Request проверка
mvn clean test -Pparallel -Pheadless -Dcucumber.filter.tags="@critical"

# Nightly build - полный прогон
mvn clean test -Pparallel-strict -Dthread.count=8 -Pheadless

# Release testing - все тесты последовательно
mvn clean test -Psequential

# Debugging конкретного модуля
mvn clean test -Dcucumber.filter.tags="@cart" -Dlogback.configurationFile=src/main/resources/logback-debug.xml
```
