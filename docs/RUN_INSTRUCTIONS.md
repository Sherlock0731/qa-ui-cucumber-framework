# Инструкция по запуску тестов

## Предварительные требования

### Системные требования
- **Java 17** или выше
- **Maven 3.8+**
- **Браузер:** Chrome/Firefox/Edge/Safari
- **RAM:** минимум 4GB (рекомендуется 8GB для параллельного запуска)
- **Дисковое пространство:** минимум 2GB свободного места

### Проверка установки

```bash
# Проверить версию Java
java -version
# Должно показать: java version "17.x.x"

# Проверить версию Maven
mvn -version
# Должно показать: Apache Maven 3.8.x или выше
```

## Установка и настройка

### 1. Клонирование репозитория

```bash
git clone <repository-url>
cd qa-ui-cucumber-framework
```

### 2. Создание файла .env (опционально)

Создайте файл `.env` в корне проекта:

```env
SAUCEDEMO_BASE_URL=[свое значение]
USER_STANDARD_USERNAME=[свое значение]
USER_STANDARD_PASSWORD=[свое значение]
BROWSER=chrome
HEADLESS=false
THREAD_COUNT=1
```

### 3. Первый запуск

```bash
# Убедитесь, что всё собирается
mvn clean compile

# Запустите один smoke тест для проверки
mvn clean test -Dcucumber.filter.tags="@smoke" -Dcucumber.filter.tags="@login and @positive"
```

## Способы запуска

### Последовательное выполнение

```bash
# Все тесты (30 сценариев) - займёт ~12-15 минут
mvn clean test

# Только smoke тесты (~13 сценариев) - займёт ~4-5 минут
mvn clean test -Dcucumber.filter.tags="@smoke"

# Один конкретный модуль
mvn clean test -Dcucumber.filter.tags="@login"
```

### Параллельное выполнение

```bash
# 4 потока (рекомендуется для локальной разработки)
mvn clean test -Pparallel

# 8 потоков (для мощных машин или CI/CD)
mvn clean test -Pparallel-strict -Dthread.count=8

# Smoke тесты параллельно
mvn clean test -Pparallel -Dcucumber.filter.tags="@smoke"
```

### Headless режим

```bash
# Последовательно без GUI
mvn clean test -Pheadless

# Параллельно без GUI (лучше для CI/CD)
mvn clean test -Pparallel -Pheadless

# 8 потоков headless
mvn clean test -Pparallel-strict -Dthread.count=8 -Pheadless
```

## Фильтрация тестов

### По тегам Cucumber

```bash
# Все login тесты
mvn clean test -Dcucumber.filter.tags="@login"

# Позитивные cart тесты
mvn clean test -Dcucumber.filter.tags="@cart and @positive"

# Smoke или critical
mvn clean test -Dcucumber.filter.tags="@smoke or @critical"

# Все кроме негативных
mvn clean test -Dcucumber.filter.tags="not @negative"
```

### По названию сценария

```bash
# Конкретный тест-кейс
mvn clean test -Dcucumber.filter.name="TC-012"

# По ключевому слову
mvn clean test -Dcucumber.filter.name="Успешная авторизация"
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

## Отчёты Allure

### Генерация и просмотр

```bash
# Запустить тесты и открыть Allure отчёт
mvn clean test allure:serve

# Только сгенерировать отчёт (без запуска тестов)
mvn allure:report

# Открыть уже сгенерированный отчёт
mvn allure:serve
```

### Просмотр истории

Allure сохраняет историю выполнения тестов между запусками.

```bash
# После нескольких запусков откройте отчёт
mvn allure:serve
# В разделе "Trends" увидите графики и тренды
```

## Docker

### Запуск в контейнере

```bash
# Дефолтные настройки
docker-compose -f docker/docker-compose.yml up

# С параметрами
THREAD_COUNT=4 BROWSER=firefox docker-compose -f docker/docker-compose.yml up

# Headless в Docker
HEADLESS=true THREAD_COUNT=8 docker-compose -f docker/docker-compose.yml up

# Только smoke в Docker
TEST_TAGS="@smoke" docker-compose -f docker/docker-compose.yml up

# С cleanup после выполнения
docker-compose -f docker/docker-compose.yml up --abort-on-container-exit
docker-compose -f docker/docker-compose.yml down
```

## Окружения

### Local (по умолчанию)

```bash
mvn clean test
# Использует config/local.properties
```

### CI окружение

```bash
mvn clean test -Denv=ci
# Использует config/ci.properties
```

### Кастомное окружение

Создайте файл `src/main/resources/config/staging.properties`:
```properties
saucedemo.base.url=[свое значение]
```

Запустите:
```bash
mvn clean test -Denv=staging
```

## Логи

### Просмотр логов

```bash
# Во время выполнения - в консоли
mvn clean test

# После выполнения - в файлах
cat target/logs/test-execution.log

# Логи конкретного потока
cat target/logs/thread-ForkJoinPool-3-worker-1.log
```

### Debug режим

```bash
# Подробные логи
mvn clean test -X

# С DEBUG уровнем логирования
mvn clean test -Dlog.level=DEBUG
```

## Скриншоты

Скриншоты создаются автоматически при падении теста:

```bash
# После запуска проверьте папку
ls target/screenshots/

# Скриншоты также прикрепляются к Allure отчёту
mvn allure:serve
# → Откройте упавший тест → вкладка "Screenshot"
```

## CI/CD Integration

### GitHub Actions (локально)

Установите [act](https://github.com/nektos/act):

```bash
# Запустить workflow локально
act -j test-all

# С конкретным браузером
act -j test-all -s BROWSER=firefox
```

### Jenkins

```bash
# Через Jenkins CLI
java -jar jenkins-cli.jar build "qa-ui-cucumber-tests" \
  -p THREAD_COUNT=8 \
  -p BROWSER=chrome \
  -p TEST_TAGS="@smoke"
```

## Troubleshooting

### Проблема: Maven не может скачать зависимости

**Решение:**
```bash
# Очистить локальный репозиторий
rm -rf ~/.m2/repository

# Пересобрать
mvn clean install -U
```

### Проблема: Браузер не запускается

**Решение:**
```bash
# Проверьте, что браузер установлен
which chrome  # или firefox, edge

# Запустите с явным указанием браузера
mvn clean test -Dbrowser=chrome

# Попробуйте headless
mvn clean test -Pheadless
```

### Проблема: Тесты падают с timeout

**Решение:**
```bash
# Увеличьте timeout через properties
mvn clean test -Dtimeout.explicit=20000

# Или в local.properties
echo "timeout.explicit=20000" >> src/main/resources/config/local.properties
```

### Проблема: OutOfMemoryError при параллельном запуске

**Решение:**
```bash
# Уменьшите количество потоков
mvn clean test -Pparallel-strict -Dthread.count=2

# Или увеличьте память для Maven
export MAVEN_OPTS="-Xms2g -Xmx4g"
mvn clean test -Pparallel
```

## Рекомендуемые сценарии использования

### Локальная разработка

```bash
# Быстрая проверка после изменений
mvn clean test -Dcucumber.filter.tags="@smoke"

# Проверка конкретного модуля
mvn clean test -Dcucumber.filter.tags="@cart"

# Debug одного теста
mvn clean test -Dcucumber.filter.name="TC-012"
```

### Pull Request проверка

```bash
# Критичные тесты быстро
mvn clean test -Pparallel -Pheadless -Dcucumber.filter.tags="@critical"

# Или все smoke
mvn clean test -Pparallel -Pheadless -Dcucumber.filter.tags="@smoke"
```

### Nightly Build

```bash
# Полный прогон параллельно
mvn clean test -Pparallel-strict -Dthread.count=8 -Pheadless

# С отчётом
mvn clean test -Pparallel-strict -Dthread.count=8 -Pheadless allure:report
```

### Release Testing

```bash
# Все тесты последовательно для стабильности
mvn clean test -Psequential

# С детальными отчётами
mvn clean test -Psequential allure:serve
```

## Переменные окружения

### Linux/macOS

```bash
export BROWSER=firefox
export THREAD_COUNT=4
export HEADLESS=true
mvn clean test -Pparallel
```

### Windows

```cmd
set BROWSER=firefox
set THREAD_COUNT=4
set HEADLESS=true
mvn clean test -Pparallel
```

### Inline

```bash
BROWSER=firefox THREAD_COUNT=4 mvn clean test -Pparallel
```

## Полезные команды

```bash
# Проверить версии зависимостей
mvn dependency:tree

# Обновить зависимости
mvn clean install -U

# Пропустить тесты, только собрать
mvn clean install -DskipTests

# Очистить всё
mvn clean

# Проверить Cucumber конфигурацию
mvn test -Dcucumber.options="--dry-run"
```

Фреймворк готов к использованию. Следуйте этой инструкции для успешного запуска тестов.
