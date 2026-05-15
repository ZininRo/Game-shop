# 🎲 Boardgames Shop — Backend

REST API интернет-магазина настольных игр.  
Учебный проект для портфолио: Spring Boot · Spring Security · JWT · JPA · PostgreSQL.

---

## Содержание

- [Стек технологий](#стек-технологий)
- [Архитектура](#архитектура)
- [Роли и права доступа](#роли-и-права-доступа)
- [API — обзор эндпоинтов](#api--обзор-эндпоинтов)
- [Быстрый старт](#быстрый-старт)
- [Переменные окружения](#переменные-окружения)
- [Структура БД](#структура-бд)
- [Тестовые данные](#тестовые-данные)
- [Известные ограничения](#известные-ограничения)

---

## Стек технологий

| Слой | Технология |
|---|---|
| Язык | Java 17 |
| Фреймворк | Spring Boot 4.x (Web MVC, Security, Data JPA) |
| База данных | PostgreSQL 15 |
| Аутентификация | JWT (jjwt 0.11) |
| Сборка | Maven |
| Фронтенд | Angular 17 (отдельный репозиторий) |

---

## Архитектура

```
┌─────────────────────────────────────────────────┐
│                  Angular SPA                    │
│          http://localhost:4200                  │
└──────────────────────┬──────────────────────────┘
                       │ HTTP / JSON
                       │ Authorization: Bearer <JWT>
┌──────────────────────▼──────────────────────────┐
│               Spring Boot API                   │
│           http://localhost:8080                 │
│                                                 │
│  JwtAuthFilter → Controller → Service → Repo    │
└──────────────────────┬──────────────────────────┘
                       │ JPA / Hibernate
┌──────────────────────▼──────────────────────────┐
│              PostgreSQL (boardgame_shop)        │
└─────────────────────────────────────────────────┘
```

**Пакетная структура:**

```
boardgames_shop/
├── config/          # SecurityConfig, DatabaseInitializer
├── controller/      # REST-контроллеры
├── dto/             # Request/Response объекты
│   ├── admin/
│   ├── auth/
│   ├── cart/
│   ├── game/
│   ├── history/
│   ├── manager/
│   └── profile/
├── entity/          # JPA-сущности
├── exception/       # GlobalExceptionHandler, ResourceNotFoundException
├── repository/      # Spring Data репозитории
├── security/        # JwtService, JwtAuthFilter
└── service/         # Бизнес-логика
```

---

## Роли и права доступа

Система реализует три роли с разграничением через Spring Security:

### BUYER (покупатель)

| Действие | Эндпоинт |
|---|---|
| Каталог игр | `GET /api/games` |
| Карточка игры | `GET /api/games/{id}` |
| Корзина (просмотр / добавление / изменение / удаление) | `GET/POST/PUT/DELETE /api/cart` |
| Оформление заказа | `POST /api/cart/checkout` |
| История заказов | `GET /api/orders` |
| Профиль | `GET/PUT/DELETE /api/profile` |

### MANAGER (менеджер)

| Действие | Ограничение |
|---|---|
| Просмотр новых заказов | только без менеджера |
| Взять заказ | только себе |
| Изменить статус / дату доставки | только своих заказов |
| Просмотр всех заказов | полный список |

### ADMIN (администратор)

| Раздел | Возможности |
|---|---|
| Покупатели | просмотр, редактирование, удаление |
| Сотрудники | полный CRUD + смена роли |
| Игры | полный CRUD + активация/деактивация |
| Заказы | просмотр, смена статуса, назначение **любого** менеджера |
| Аналитика | 6 отчётных эндпоинтов |

**Ограничения администратора:**
- не может менять покупателя в заказе
- не может менять дату создания заказа
- не может редактировать состав заказа

---

## API — обзор эндпоинтов

### Аутентификация

```
POST /api/auth/register    — регистрация покупателя
POST /api/auth/login       — вход, возвращает JWT-токен
```

**Пример запроса на логин:**
```json
POST /api/auth/login
{
  "email": "ivan@mail.com",
  "password": "password123"
}
```

**Ответ:**
```json
{
  "userId": 1,
  "role": "BUYER",
  "token": "eyJhbGciOiJ..."
}
```

Токен передаётся в заголовке каждого последующего запроса:
```
Authorization: Bearer eyJhbGciOiJ...
```

---

### Покупатель

```
GET    /api/games                     — каталог (только активные игры)
GET    /api/games/{id}                — карточка игры

GET    /api/cart                      — содержимое корзины
POST   /api/cart/add                  — добавить игру в корзину
PUT    /api/cart/update               — изменить количество
DELETE /api/cart/remove/{gameId}      — удалить позицию
POST   /api/cart/checkout             — оформить заказ

GET    /api/orders                    — история заказов

GET    /api/profile                   — данные профиля
PUT    /api/profile                   — обновить профиль
DELETE /api/profile                   — удалить аккаунт
```

---

### Менеджер

```
GET  /api/manager/new           — новые заказы (без менеджера)
POST /api/manager/assign/{id}   — взять заказ себе
GET  /api/manager/my            — мои заказы
GET  /api/manager/order/{id}    — детали конкретного заказа
PUT  /api/manager/order/{id}    — обновить статус / дату доставки
GET  /api/manager/all           — все заказы (кроме NOT_CREATED)
```

---

### Администратор

```
# Сотрудники
GET    /api/admin/employees
POST   /api/admin/employees        → 201
PUT    /api/admin/employees/{id}
DELETE /api/admin/employees/{id}   → 204

# Покупатели
GET    /api/admin/clients
PUT    /api/admin/clients/{id}
DELETE /api/admin/clients/{id}     → 204

# Игры
GET    /api/admin/games
POST   /api/admin/games            → 201
PUT    /api/admin/games/{id}
DELETE /api/admin/games/{id}       → 204  (soft delete: active = false)

# Заказы
GET    /api/admin/orders
PUT    /api/admin/orders/{id}
GET    /api/admin/orders/last?limit=10
GET    /api/admin/orders/multi-item

# Аналитика (только COMPLETED-заказы)
GET    /api/admin/reports/sales/month?year=2026&month=4
GET    /api/admin/reports/sales/day?date=2026-04-24
GET    /api/admin/reports/sales/period?from=2026-01-01&to=2026-04-30
GET    /api/admin/reports/sales/compare?year=2026&month=4
GET    /api/admin/reports/sales/top-game?year=2026&month=4
GET    /api/admin/reports/sales/average-check?year=2026&month=4
```

---

### Статусы заказа

```
NOT_CREATED  →  CREATED  →  IN_TRANSIT  →  PICKUP_POINT  →  COMPLETED
                                                         ↘  CANCELLED
```

| Код | Отображение |
|---|---|
| `NOT_CREATED` | Корзина (не оформлен) |
| `CREATED` | Оформлен |
| `IN_TRANSIT` | В пути |
| `PICKUP_POINT` | В пункте выдачи |
| `COMPLETED` | Завершён |
| `CANCELLED` | Отменён |

---

## Быстрый старт

### Предварительные требования

- Java 17+
- Maven 3.9+
- PostgreSQL 15 (локально или Docker)

### 1. Клонировать репозиторий

```bash
git clone https://github.com/your-username/boardgames-shop.git
cd boardgames-shop
```

### 2. Создать файл с переменными окружения

Скопируйте пример и заполните значения:

```bash
cp .env.example .env
```

`.env.example`:
```
DB_SUPER_PASSWORD=your_postgres_superuser_password
DB_PASSWORD=your_app_user_password
JWT_SECRET=your_secret_key_minimum_32_characters
```

> ⚠️ Файл `.env` добавлен в `.gitignore` и **никогда не должен попадать в репозиторий**.

### 3. Запустить приложение

```bash
mvn spring-boot:run
```

При первом запуске `DatabaseInitializer` автоматически:
1. Создаст пользователя `AdminRoma` в PostgreSQL
2. Создаст базу данных `boardgame_shop`
3. Выполнит `schema.sql` (создание таблиц)
4. Выполнит `data.sql` (тестовые данные)

API будет доступен на `http://localhost:8080`.

### 4. Проверить работу

```bash
# Получить токен
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"danvor@gmail.com","password":"password123"}'

# Получить список игр (с токеном покупателя)
curl http://localhost:8080/api/games \
  -H "Authorization: Bearer <token>"
```

---

## Переменные окружения

| Переменная | Описание | Пример |
|---|---|---|
| `DB_SUPER_PASSWORD` | Пароль суперпользователя `postgres` | `TestSecret123` |
| `DB_PASSWORD` | Пароль пользователя приложения `AdminRoma` | `Roma21` |
| `JWT_SECRET` | Секрет для подписи JWT (мин. 32 символа) | `VerySecretKey...` |
| `DB_USERNAME` | Имя пользователя БД (опц.) | `AdminRoma` |
| `CORS_ALLOWED_ORIGINS` | Разрешённые CORS-источники (опц.) | `http://localhost:4200` |

---

## Структура БД

```
roles ──────────────── users ──┬── clients ──── orders ──── order_items ──── games
                               └── employees ──┘
```

**Ключевые решения:**

- **Мягкое удаление игр** — поле `active = false` вместо физического удаления; игра остаётся в истории заказов
- **Заглушки при удалении пользователей** — при удалении покупателя или сотрудника их заказы переназначаются на placeholder-записи (client id=5, employee id=4), чтобы не нарушать FK-консистентность
- **Корзина как заказ** — незавершённый заказ хранится со статусом `NOT_CREATED`; `checkout` переводит его в `CREATED`
- **Цена фиксируется в момент покупки** — `order_items.price_at_purchase` хранит цену на момент оформления, независимо от будущих изменений

### Схема таблиц

```sql
roles         (id, name)
users         (id, email, password, role_id → roles)
clients       (id, full_name, phone, birth_date, user_id → users)
employees     (id, full_name, phone, position, user_id → users)
games         (id, name, price, description, active)
orders        (id, status, order_date, delivery_date, total_price,
               client_id → clients, employee_id → employees)
order_items   (id, quantity, price_at_purchase,
               game_id → games, order_id → orders)
```

---

## Тестовые данные

После первого запуска в БД создаются следующие учётные записи:

| Email | Пароль | Роль |
|---|---|---|
| `danvor@gmail.com` | `password123` | ADMIN |
| `john@gmail.com` | `password123` | MANAGER |
| `tirion@gmail.com` | `password123` | MANAGER |
| `ivan@mail.com` | `12345678` | BUYER |
| `romanzinin2005@gmail.com` | `password123` | BUYER |

**Тестовые игры:**

| Название | Цена | Статус |
|---|---|---|
| Монополия | 1 990 ₽ | активна |
| Игра престолов | 6 950 ₽ | активна |
| Эволюция | 1 250 ₽ | активна |
| Манчкин | 1 650 ₽ | активна |
| Зельеварение | 1 350 ₽ | неактивна (soft deleted) |

---

## Известные ограничения

Проект является учебным. Следующие аспекты намеренно упрощены или отложены:

- **Нет миграций** — схема создаётся через `schema.sql`; в продакшне следует использовать Flyway или Liquibase
- **`DatabaseInitializer`** — реализован вручную; лучшая альтернатива — Spring Boot + Flyway с автозапуском
- **Изображения игр** — хранятся локально на сервере в папке `uploads/`; в продакшне — S3 или аналог
- **Нет пагинации** на списках заказов и пользователей — добавить `Pageable` при росте данных
- **Нет rate limiting** на эндпоинтах аутентификации
- **Тесты** — минимальны; покрытие сервисного слоя unit-тестами запланировано

---

## Лицензия

MIT
