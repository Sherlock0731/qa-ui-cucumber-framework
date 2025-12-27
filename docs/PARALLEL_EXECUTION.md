# Параллельное выполнение тестов

## Обзор

Фреймворк поддерживает многопоточное выполнение Cucumber сценариев с автоматическим контролем количества одновременно открытых браузеров через механизм Semaphore.

## Архитектура многопоточности

### Компоненты

1. **JUnit Platform** - управление параллельным запуском тестов
2. **Cucumber Parallel Execution** - распределение сценариев по потокам
3. **ThreadLocal WebDriver** - изоляция драйвера между потоками
4. **Semaphore** - контроль максимального количества браузеров
5. **Thread-safe логирование** - отдельные логи для каждого потока

### Схема работы

```
User запускает: mvn clean test -Pparallel-strict -Dthread.count=8
                                    ↓
              JUnit Platform создаёт 8 потоков
                                    ↓
        Cucumber распределяет сценарии по потокам
                                    ↓
   ForkJoinPool-worker-1  ForkJoinPool-worker-2  ...  ForkJoinPool-worker-8
            ↓                      ↓                            ↓
    Hooks.@Before()         Hooks.@Before()            Hooks.@Before()
            ↓                      ↓                            ↓
   DriverManager.initDriver() → Semaphore.acquire() → Ждёт свободный слот
            ↓                      ↓                            ↓
    WebDriver создан        WebDriver создан          WebDriver создан
            ↓                      ↓                            ↓
   Выполнение шагов        Выполнение шагов          Выполнение шагов
            ↓                      ↓                            ↓
    Hooks.@After()          Hooks.@After()            Hooks.@After()
            ↓                      ↓                            ↓
   WebDriver.quit()         WebDriver.quit()          WebDriver.quit()
            ↓                      ↓                            ↓
   Semaphore.release()     Semaphore.release()      Semaphore.release()
```

## Режимы запуска

### 1. Последовательный (по умолчанию)

```bash
mvn clean test
# или явно
mvn clean test -Psequential
```

**Характеристики:**
- 1 поток
- 1 браузер
- Время: ~12-15 минут для всех тестов
- Использование: отладка, разработка

### 2. Параллельный (4 потока)

```bash
mvn clean test -Pparallel
```

**Характеристики:**
- 4 потока
- 4 браузера одновременно
- Время: ~4-5 минут для всех тестов
- Использование: локальные прогоны, PR проверки

### 3. Параллельный с динамическим количеством потоков

```bash
mvn clean test -Pparallel-strict -Dthread.count=8
```

**Характеристики:**
- N потоков (параметр thread.count)
- N браузеров одновременно
- Время: ~2-3 минуты для 8 потоков
- Использование: CI/CD, мощные машины

## Конфигурация

### pom.xml

#### Базовая конфигурация (параллельность отключена)
```xml
<systemPropertyVariables>
    <cucumber.execution.parallel.enabled>false</cucumber.execution.parallel.enabled>
    <thread.count>1</thread.count>
</systemPropertyVariables>
```

#### Профиль parallel (4 потока)
```xml
<profile>
    <id>parallel</id>
    <properties>
        <parallel.threads>4</parallel.threads>
        <max.parallel.browsers>4</max.parallel.browsers>
    </properties>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <systemPropertyVariables>
                        <cucumber.execution.parallel.enabled>true</cucumber.execution.parallel.enabled>
                        <cucumber.execution.parallel.config.fixed.parallelism>4</cucumber.execution.parallel.config.fixed.parallelism>
                    </systemPropertyVariables>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

#### Профиль parallel-strict (динамическое количество)
```xml
<profile>
    <id>parallel-strict</id>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <systemPropertyVariables>
                        <cucumber.execution.parallel.enabled>true</cucumber.execution.parallel.enabled>
                        <cucumber.execution.parallel.config.fixed.parallelism>${thread.count}</cucumber.execution.parallel.config.fixed.parallelism>
                        <max.parallel.browsers>${thread.count}</max.parallel.browsers>
                    </systemPropertyVariables>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

### junit-platform.properties

```properties
# По умолчанию выключено
cucumber.execution.parallel.enabled=false

# Стратегия
cucumber.execution.parallel.config.strategy=fixed

# Количество (переопределяется через -D)
cucumber.execution.parallel.config.fixed.parallelism=4

# Режим
cucumber.execution.parallel.mode.default=concurrent
cucumber.execution.parallel.mode.classes.default=concurrent
```

## Semaphore - контроль браузеров

### Назначение

Semaphore ограничивает максимальное количество одновременно открытых браузеров, предотвращая перегрузку системы.

### Реализация

```java
public class DriverManager {
    private static Semaphore browserSemaphore;
    
    public static void initializeSemaphore(int maxBrowsers) {
        if (browserSemaphore == null) {
            browserSemaphore = new Semaphore(maxBrowsers);
            log.info("Semaphore initialized with {} permits", maxBrowsers);
        }
    }
    
    public static void initDriver() throws InterruptedException {
        // Ожидание доступного слота
        browserSemaphore.acquire();
        log.info("Permit acquired. Active browsers: {}", 
                maxBrowsers - browserSemaphore.availablePermits());
        
        try {
            WebDriver driver = createDriver();
            // ... инициализация
        } catch (Exception e) {
            browserSemaphore.release(); // Освобождение при ошибке
            throw e;
        }
    }
    
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
            browserSemaphore.release(); // Освобождение слота
            log.info("Browser permit released");
        }
    }
}
```

### Поведение

- **Инициализация:** Semaphore создаётся в Hooks с количеством permits = thread.count
- **acquire():** Поток ждёт, пока не освободится слот
- **release():** После закрытия браузера слот освобождается
- **Блокировка:** Если все слоты заняты, поток ждёт

**Пример логов:**
```
Thread pool-1: Waiting for browser permit... (active: 0)
Thread pool-1: Permit acquired. Active browsers: 1

Thread pool-2: Waiting for browser permit... (active: 1)
Thread pool-2: Permit acquired. Active browsers: 2

Thread pool-3: Waiting for browser permit... (active: 2)
Thread pool-3: Permit acquired. Active browsers: 3

Thread pool-4: Waiting for browser permit... (active: 3)
Thread pool-4: Permit acquired. Active browsers: 4

Thread pool-5: Waiting for browser permit... (active: 4)
# ↑ Thread pool-5 ждёт, пока один из браузеров закроется

Thread pool-1: Driver quit successfully
Thread pool-1: Browser permit released. Active browsers: 3

Thread pool-5: Permit acquired. Active browsers: 4
```

## ThreadLocal изоляция

### Назначение

ThreadLocal обеспечивает изоляцию WebDriver между потоками.

### Реализация

```java
private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

public static WebDriver getDriver() {
    return driver.get();
}

public static void initDriver() {
    WebDriver webDriver = createDriver();
    driver.set(webDriver); // Каждый поток получает свой экземпляр
    WebDriverRunner.setWebDriver(webDriver);
}

public static void quitDriver() {
    WebDriver webDriver = driver.get();
    if (webDriver != null) {
        webDriver.quit();
        driver.remove(); // Очистка ThreadLocal
    }
}
```

## Headless режим

Headless режим экономит ресурсы при параллельном запуске:

```bash
# 4 потока headless
mvn clean test -Pparallel -Pheadless

# 8 потоков headless
mvn clean test -Pparallel-strict -Dthread.count=8 -Pheadless
```

**Преимущества:**
- Экономия CPU/GPU (браузеры не рендерят UI)
- Экономия RAM (~30-40% меньше)
- Быстрее запускаются (+10-15% скорость)
- Идеально для CI/CD

## Рекомендации

### Количество потоков

| Машина | CPU Cores | RAM | Рекомендуемый thread.count |
|--------|-----------|-----|----------------------------|
| CI/CD  | 2-4       | 8GB | 2-3                        |
| Dev    | 4-8       | 16GB| 4-6                        |
| Server | 8-16      | 32GB| 8-12                       |

### Формула расчёта

```
thread.count = min(CPU_CORES, RAM_GB / 2, 12)
```

**Примеры:**
- 4 cores, 8GB RAM: min(4, 4, 12) = 4 потока
- 8 cores, 16GB RAM: min(8, 8, 12) = 8 потоков
- 16 cores, 32GB RAM: min(16, 16, 12) = 12 потоков

### Лимиты

- **Минимум:** 1 поток (последовательный режим)
- **Оптимум:** 4-8 потоков
- **Максимум:** 12 потоков (больше - overhead на переключение контекста)

## Troubleshooting

### Проблема: Все тесты запускаются в потоке main

**Причина:** Параллельность не включена

**Решение:**
1. Используйте профиль `-Pparallel` или `-Pparallel-strict`
2. Проверьте `cucumber.execution.parallel.enabled=true` в логах
3. Убедитесь, что в `junit-platform.properties` нет жёсткого `enabled=false`

### Проблема: Открывается меньше браузеров, чем thread.count

**Причина:** Semaphore ограничивает количество

**Решение:**
1. Проверьте `max.parallel.browsers` в профиле
2. В логах должно быть: `Semaphore initialized with N permits`
3. Убедитесь, что `max.parallel.browsers >= thread.count`

### Проблема: Thread-safety ошибки

**Причина:** Неправильная изоляция WebDriver

**Решение:**
1. Используйте только `ThreadLocal<WebDriver>`
2. Не используйте статические WebElement
3. Получайте драйвер через `WebDriverRunner.getWebDriver()`

### Проблема: Браузеры не закрываются

**Причина:** Exception до `quitDriver()`

**Решение:**
1. Используйте `@After` hook с try-finally
2. Проверьте логи на ошибки в `@After`

## Производительность

### Замеры времени (30 тестов)

| Конфигурация | Время | Ускорение |
|--------------|-------|-----------|
| 1 поток      | 12-15 мин | 1x |
| 2 потока     | 7-9 мин   | ~1.7x |
| 4 потока     | 4-5 мин   | ~3x |
| 8 потоков    | 2-3 мин   | ~5x |
| 12 потоков   | 2-2.5 мин | ~6x |

### CPU/RAM использование

| Потоки | CPU % | RAM (GB) | Браузеры |
|--------|-------|----------|----------|
| 1      | 15%   | 1.5      | 1        |
| 4      | 50%   | 4        | 4        |
| 8      | 80%   | 7        | 8        |
| 12     | 95%   | 10       | 12       |

## CI/CD Examples

### GitHub Actions

```yaml
- name: Run tests in parallel
  run: mvn clean test -Pparallel-strict -Dthread.count=4 -Pheadless
```

### Jenkins

```groovy
stage('Test') {
    steps {
        sh 'mvn clean test -Pparallel -Pheadless'
    }
}
```

### GitLab CI

```yaml
test:
  script:
    - mvn clean test -Pparallel-strict -Dthread.count=8 -Pheadless
```

## Логи многопоточного запуска

```
00:02:08 [ForkJoinPool-3-worker-1] INFO Hooks - Scenario Started: TC-030
00:02:08 [ForkJoinPool-3-worker-2] INFO Hooks - Scenario Started: TC-019  
00:02:08 [ForkJoinPool-3-worker-3] INFO Hooks - Scenario Started: TC-025
00:02:08 [ForkJoinPool-3-worker-4] INFO Hooks - Scenario Started: TC-011

00:02:08 [ForkJoinPool-3-worker-1] INFO DriverManager - Waiting for browser permit...
00:02:08 [ForkJoinPool-3-worker-1] INFO DriverManager - Permit acquired. Active browsers: 1
00:02:08 [ForkJoinPool-3-worker-2] INFO DriverManager - Permit acquired. Active browsers: 2
00:02:08 [ForkJoinPool-3-worker-3] INFO DriverManager - Permit acquired. Active browsers: 3
00:02:08 [ForkJoinPool-3-worker-4] INFO DriverManager - Permit acquired. Active browsers: 4

00:02:10 [ForkJoinPool-3-worker-1] INFO LoginActionSteps - Opening login page
00:02:10 [ForkJoinPool-3-worker-2] INFO LoginActionSteps - Opening login page
```

Фреймворк полностью поддерживает безопасное многопоточное выполнение с автоматическим контролем ресурсов.
