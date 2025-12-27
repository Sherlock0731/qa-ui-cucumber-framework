# Матрица тест-кейсов

## Общая статистика

| Модуль | Сценарии | Smoke | Positive | Negative | E2E | Critical |
|--------|----------|-------|----------|----------|-----|----------|
| Login | 5 | 1 | 2 | 3 | 0 | 2 |
| Inventory | 6 | 3 | 4 | 2 | 0 | 2 |
| Cart | 8 | 4 | 5 | 3 | 0 | 2 |
| Checkout | 8 | 4 | 5 | 1 | 2 | 1 |
| Navigation | 3 | 1 | 2 | 0 | 1 | 1 |
| **ИТОГО** | **30** | **13** | **18** | **9** | **3** | **8** |

## Login (5 сценариев)

| ID | Название | Приоритет | Теги | Описание |
|----|----------|-----------|------|----------|
| TC-001 | Успешная авторизация со стандартным пользователем | High | @login, @smoke, @positive | Авторизация с валидными креденшелами |
| TC-002 | Успешная авторизация с различными типами пользователей | High | @login, @positive | Авторизация standard_user, problem_user, performance_glitch_user |
| TC-003 | Валидация поля Username | Medium | @login, @negative | Пустое поле username |
| TC-004 | Валидация поля Password | Medium | @login, @negative | Пустое поле password |
| TC-005 | Авторизация с неверными учетными данными | High | @login, @negative, @critical | Неверный username/password |

## Inventory (6 сценариев)

| ID | Название | Приоритет | Теги | Описание |
|----|----------|-----------|------|----------|
| TC-006 | Отображение списка товаров | High | @inventory, @smoke, @positive | Все товары видны на странице |
| TC-007 | Сортировка товаров по имени (A to Z) | Medium | @inventory, @positive | Сортировка по возрастанию |
| TC-008 | Сортировка товаров по имени (Z to A) | Medium | @inventory, @positive | Сортировка по убыванию |
| TC-009 | Сортировка товаров по цене (low to high) | Medium | @inventory, @smoke, @positive, @critical | Сортировка по цене вверх |
| TC-010 | Сортировка товаров по цене (high to low) | Medium | @inventory, @smoke, @positive, @critical | Сортировка по цене вниз |
| TC-011 | Переход на страницу детальной информации о товаре | Medium | @inventory, @positive | Клик на товар → детали |

## Cart (8 сценариев)

| ID | Название | Приоритет | Теги | Описание |
|----|----------|-----------|------|----------|
| TC-012 | Добавление товара в корзину из каталога | High | @cart, @smoke, @positive | Add to cart → badge = 1 |
| TC-013 | Добавление нескольких товаров в корзину | High | @cart, @smoke, @positive, @critical | Add 3 товара → badge = 3 |
| TC-014 | Удаление товара из корзины | Medium | @cart, @smoke, @positive | Remove → badge уменьшается |
| TC-015 | Счетчик товаров в корзине | Medium | @cart, @positive | Badge корректно отображает количество |
| TC-016 | Отображение добавленных товаров в корзине | High | @cart, @smoke, @positive, @critical | Товары видны в cart page |
| TC-017 | Удаление товара из корзины на странице корзины | Medium | @cart, @positive | Remove button работает |
| TC-018 | Кнопка 'Checkout' в корзине | Medium | @cart, @positive | Переход на checkout |
| TC-019 | Кнопка 'Continue Shopping' в корзине | Medium | @cart, @positive | Возврат в inventory |

## Checkout (8 сценариев)

| ID | Название | Приоритет | Теги | Описание |
|----|----------|-----------|------|----------|
| TC-020 | Успешное оформление заказа | Critical | @checkout, @smoke, @e2e, @positive, @happy-path | End-to-end flow |
| TC-021 | Валидация поля First Name | Medium | @checkout, @negative | Пустое поле First Name |
| TC-022 | Валидация поля Last Name | Medium | @checkout, @negative | Пустое поле Last Name |
| TC-023 | Валидация поля Postal Code | Medium | @checkout, @negative | Пустое поле Postal Code |
| TC-024 | Отмена оформления на первом шаге | Low | @checkout, @positive | Cancel → возврат в cart |
| TC-025 | Проверка корректности расчета общей суммы заказа | High | @checkout, @smoke, @e2e, @positive, @critical | Subtotal + Tax = Total |
| TC-026 | Отмена оформления на втором шаге | Low | @checkout, @positive | Cancel → возврат в inventory |
| TC-027 | Возврат к покупкам после успешного оформления | Low | @checkout, @smoke, @positive | Back Home → inventory |

## Navigation (3 сценария)

| ID | Название | Приоритет | Теги | Описание |
|----|----------|-----------|------|----------|
| TC-028 | Logout через меню | High | @navigation, @smoke, @positive | Logout → redirect to login |
| TC-029 | Пункт меню 'All Items' | Medium | @navigation, @positive | All Items → inventory page |
| TC-030 | Сброс состояния приложения через Reset App State | Medium | @navigation, @e2e, @positive, @critical | Reset → cart badge = 0 |

## Приоритизация

### Critical (8 тестов)
Критически важная функциональность, без которой приложение не функционирует.

- TC-005: Авторизация с неверными данными
- TC-009: Сортировка по цене (low to high)
- TC-010: Сортировка по цене (high to low)
- TC-013: Добавление нескольких товаров
- TC-016: Отображение товаров в корзине
- TC-025: Расчёт общей суммы
- TC-030: Reset App State

### High (12 тестов)
Важная функциональность, влияющая на пользовательский опыт.

- TC-001, TC-002: Успешная авторизация
- TC-006: Отображение списка товаров
- TC-012: Добавление в корзину
- TC-020: Успешное оформление заказа
- TC-028: Logout

### Medium (9 тестов)
Второстепенная функциональность.

- TC-003, TC-004: Валидация полей авторизации
- TC-007, TC-008, TC-011: Сортировка и навигация
- TC-014, TC-015, TC-017, TC-018: Функции корзины
- TC-021, TC-022, TC-023: Валидация полей checkout
- TC-029: All Items menu

### Low (1 тест)
Редко используемая функциональность.

- TC-024, TC-026, TC-027: Cancel и Back buttons

## Покрытие функциональности

### Авторизация (5 тестов)
- ✅ Успешная авторизация
- ✅ Различные типы пользователей
- ✅ Валидация обязательных полей
- ✅ Обработка неверных креденшелов

### Каталог товаров (6 тестов)
- ✅ Отображение списка
- ✅ Сортировка по имени (A-Z, Z-A)
- ✅ Сортировка по цене (low-high, high-low)
- ✅ Переход к деталям товара

### Корзина (8 тестов)
- ✅ Добавление товаров
- ✅ Удаление товаров
- ✅ Счётчик товаров (badge)
- ✅ Отображение в корзине
- ✅ Переход к оформлению
- ✅ Продолжение покупок

### Оформление заказа (8 тестов)
- ✅ Успешное оформление (E2E)
- ✅ Валидация полей формы
- ✅ Расчёт итоговой суммы
- ✅ Отмена на разных шагах
- ✅ Возврат к покупкам

### Навигация (3 теста)
- ✅ Logout
- ✅ Меню навигации
- ✅ Сброс состояния

## Теги для фильтрации

### По модулям
- `@login` - 5 тестов
- `@inventory` - 6 тестов
- `@cart` - 8 тестов
- `@checkout` - 8 тестов
- `@navigation` - 3 теста

### По типу
- `@smoke` - 13 быстрых проверок
- `@positive` - 18 позитивных сценариев
- `@negative` - 9 негативных сценариев
- `@e2e` - 3 end-to-end сценария
- `@critical` - 8 критичных тестов
- `@happy-path` - 1 основной пользовательский сценарий

## Рекомендуемые комбинации

### Быстрая проверка (Smoke)
```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
# 13 тестов, ~4-5 минут
```

### Критичная функциональность
```bash
mvn clean test -Dcucumber.filter.tags="@critical"
# 8 тестов, ~3-4 минуты
```

### Полный End-to-End
```bash
mvn clean test -Dcucumber.filter.tags="@e2e"
# 3 теста, ~1-2 минуты
```

### Один модуль
```bash
mvn clean test -Dcucumber.filter.tags="@cart"
# 8 тестов, ~3-4 минуты
```

### Pull Request проверка
```bash
mvn clean test -Dcucumber.filter.tags="@smoke or @critical"
# 21 тест (с пересечениями), ~6-7 минут
```

Полное покрытие всех 30 тест-кейсов обеспечивает проверку критичной функциональности SauceDemo приложения.
