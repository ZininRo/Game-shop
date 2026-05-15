# 🎲 Boardgames Shop
 
Fullstack-приложение интернет-магазина настольных игр.  
Учебный проект для портфолио: Spring Boot · Angular · Spring Security · JWT · PostgreSQL.
 
---
 
## Содержание
 
- [Стек технологий](#стек-технологий)
- [Архитектура](#архитектура)
- [Экраны приложения](#экраны-приложения)
- [Роли и права доступа](#роли-и-права-доступа)
- [API — обзор эндпоинтов](#api--обзор-эндпоинтов)
- [Быстрый старт](#быстрый-старт)
- [Переменные окружения](#переменные-окружения)
- [Структура БД](#структура-бд)
- [Тестовые данные](#тестовые-данные)
- [Известные ограничения](#известные-ограничения)
---
 
## Стек технологий
 
### Backend
 
| Слой | Технология |
|---|---|
| Язык | Java 17 |
| Фреймворк | Spring Boot 4.x (Web MVC, Security, Data JPA) |
| База данных | PostgreSQL 15 |
| Аутентификация | JWT (jjwt 0.11) |
| Сборка | Maven |
 
### Frontend
 
| Слой | Технология |
|---|---|
| Фреймворк | Angular 19 (standalone components) |
| Язык | TypeScript 5.7 |
| Графики | Chart.js 4.x |
| Стили | CSS (custom design system, без UI-библиотек) |
| SSR | Angular SSR / Express |
| Сборка | Angular CLI 19 |
 
---
 
## Архитектура
 
```
┌──────────────────────────────────────────────────────────────┐
│                     Angular SPA                              │
│                  http://localhost:4200                       │
│                                                              │
│  /login  /register                                           │
│  /buyer  → catalog, game/:id, cart, orders (профиль)         │
│  /manager → orders                                           │
│  /admin  → users, employees, games, orders, reports/sales    │
└───────────────────────────┬──────────────────────────────────┘
                            │ HTTP / JSON
                            │ Authorization: Bearer <JWT>
┌───────────────────────────▼──────────────────────────────────┐
│                    Spring Boot API                           │
│                  http://localhost:8080                       │
│                                                              │
│  JwtAuthFilter → Controller → Service → Repository           │
└───────────────────────────┬──────────────────────────────────┘
                            │ JPA / Hibernate
┌───────────────────────────▼──────────────────────────────────┐
│                 PostgreSQL (boardgame_shop)                  │
└──────────────────────────────────────────────────────────────┘
```
 
### Структура фронтенда
 
```
src/app/
├── auth/
│   ├── login/           # Страница входа (модалка «О проекте» с тестовыми данными)
│   ├── register/        # Регистрация с маскированным вводом телефона
│   └── Not found/       # Анимированная 404-страница
├── buyer/
│   ├── catalog/         # Каталог игр с живым поиском
│   ├── game/            # Карточка игры
│   ├── cart/            # Корзина (изменение количества, удаление, оформление)
│   └── orders/          # История заказов + профиль покупателя
├── manager/
│   └── orders/          # Управление заказами (взять в работу, статусы, даты)
├── admin/
│   ├── users/           # CRUD покупателей
│   ├── employees/       # CRUD сотрудников с ролями и бейджами
│   ├── games/           # CRUD игр с мягким удалением
│   ├── orders/          # Все заказы, назначение менеджеров, фильтры
│   └── reports/         # Аналитика с Chart.js-графиком
└── core/
    ├── guards/          # authGuard, roleGuard
    ├── interceptors/    # JWT-интерцептор (автоматически добавляет токен)
    └── services/        # admin/, manager/, auth, cart, game, order, profile
```
 
### Структура бэкенда
 
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
 
## Экраны приложения
 
### Покупатель
 
| Маршрут | Описание |
|---|---|
| `/buyer/catalog` | Сетка карточек с живым поиском по названию |
| `/buyer/game/:id` | Карточка игры с описанием и кнопкой «Добавить в заказ» |
| `/buyer/cart` | Корзина: изменение количества, удаление, подтверждение заказа |
| `/buyer/orders` | Профиль покупателя + история заказов со статусными бейджами, редактирование профиля |
 
### Менеджер
 
| Маршрут | Описание |
|---|---|
| `/manager/orders` | Три вкладки: «Все» / «Без менеджера» / «Мои заказы» |
 
В режиме «Все» доступны фильтры по статусу, клиенту и дате. В карточке заказа менеджер видит состав, может взять заказ себе, сменить статус и дату доставки. Редактирование доступно только для своих заказов.
 
### Администратор
 
| Маршрут | Описание |
|---|---|
| `/admin/users` | Таблица покупателей с редактированием и удалением |
| `/admin/employees` | Таблица сотрудников с бейджами роли и позиции, счётчик активных заказов, полный CRUD |
| `/admin/games` | Таблица игр с флагом активности, CRUD и деактивация через soft delete |
| `/admin/orders` | Полная таблица заказов, модалка редактирования (статус, менеджер, дата, сумма), быстрые бэкенд-фильтры |
| `/admin/reports/sales` | Страница аналитики продаж |
 
**Страница аналитики** (`/admin/reports/sales`) включает:
- навигацию по месяцам (← →) с пересчётом всех показателей
- линейный Chart.js-график выручки по дням; клик на точку открывает модалку с детализацией заказов за этот день
- три KPI-карточки: топ игра месяца, средний чек, сравнение с предыдущим месяцем (+/−%)
- произвольный период с агрегированными показателями (выручка, кол-во заказов, средний чек)
### Общие UI-решения
 
- **Тёмная тема** — единый design system на CSS-переменных (`#0f1117` фон, `#a5b4fc` акцент), реализованный вручную без UI-библиотек
- **Трёхсостояние загрузки** — Skeleton → Error → Content в каждом компоненте
- **Модалки** — закрытие по кнопке и клику вне области, анимации fadeIn/slideUp
- **Lazy loading** — модули `buyer`, `manager`, `admin` загружаются по требованию
- **JWT-интерцептор** — автоматически добавляет `Authorization: Bearer` ко всем исходящим HTTP-запросам
- **Маскированный ввод телефона** — форматирование `+7 (999) 000-00-00` при регистрации в реальном времени
---
 
## Роли и права доступа
 
Роль сохраняется в `localStorage` после логина; `roleGuard` проверяет её при переходе по роутам. На бэкенде все эндпоинты защищены через Spring Security (`hasRole`).
 
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
| Сотрудники | полный CRUD + смена роли (MANAGER / ADMIN) |
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
- Node.js 20+ и npm
- PostgreSQL 15 (локально или Docker)
### 1. Клонировать репозиторий
 
```bash
git clone https://github.com/ZininRo/boardgames-shop.git
cd boardgames-shop
```
 
### 2. Запустить бэкенд
 
```bash
mvn spring-boot:run
```
 
При первом запуске `DatabaseInitializer` автоматически:
1. Создаст пользователя `AdminRoma` в PostgreSQL
2. Создаст базу данных `boardgame_shop`
3. Выполнит `schema.sql` (создание таблиц)
4. Выполнит `data.sql` (тестовые данные)
API будет доступен на `http://localhost:8080`.
 
### 3. Запустить фронтенд
 
```bash
cd boardgames-frontend   # папка с Angular-проектом
npm install
ng serve
```
 
Приложение откроется на `http://localhost:4200`.
 
> Если порт бэкенда отличается от `8080` — обновите константу `api` в файлах сервисов внутри `src/app/core/services/`.
 
### 4. Войти в приложение
 
Откройте `http://localhost:4200/login`. На странице входа есть кнопка **«О проекте»** с готовыми тестовыми учётными данными для каждой роли.
 
---
 
## Переменные окружения
 
| Переменная | Описание | Пример |
|---|---|---|
| `DB_SUPER_PASSWORD` | Пароль суперпользователя `postgres` | `TestSecret123` |
| `DB_PASSWORD` | Пароль пользователя приложения `AdminRoma` | `Roma21` |
| `JWT_SECRET` | Секрет для подписи JWT (мин. 32 символа) | `VerySecretKey...` |
| `DB_USERNAME` | Имя пользователя БД (опц., по умолч. `AdminRoma`) | `AdminRoma` |
| `CORS_ALLOWED_ORIGINS` | Разрешённые CORS-источники (опц.) | `http://localhost:4200` |
 
---
 
## Структура БД
 
```
roles ──────────────── users ──┬── clients ──── orders ──── order_items ──── games
                               └── employees ──┘
```
 
**Ключевые проектные решения:**
 
- **Мягкое удаление игр** — поле `active = false` вместо физического удаления; игра остаётся в истории заказов и не ломает агрегаты аналитики. На фронтенде неактивная игра отображается с бейджем «Нет» в колонке «В наличии»
- **Заглушки при удалении пользователей** — при удалении покупателя или сотрудника их заказы переназначаются на placeholder-записи (client `id=5`, employee `id=4`), чтобы не нарушать FK-консистентность
- **Корзина как заказ** — незавершённый заказ хранится со статусом `NOT_CREATED`; `checkout` переводит его в `CREATED`. Фронтенд автоматически исключает `NOT_CREATED`-заказы из истории
- **Цена фиксируется в момент покупки** — `order_items.price_at_purchase` хранит цену на момент оформления, независимо от будущих изменений в каталоге
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
 
После первого запуска бэкенда в БД создаются следующие учётные записи.  
Их же можно найти в модальном окне **«О проекте»** на странице логина.
 
| Email | Пароль | Роль |
|---|---|---|
| `danvor@gmail.com` | `12345678` | ADMIN |
| `john@gmail.com` | `12345678` | MANAGER |
| `romanzinin2005@gmail.com` | `12345678` | BUYER |

