-- =========================
-- ROLES
-- =========================

INSERT INTO roles (id, name) VALUES
(1, 'BUYER'),
(2, 'MANAGER'),
(3, 'ADMIN');

-- =========================
-- USERS
-- =========================

INSERT INTO users (id, email, password, role_id) VALUES
(1, 'ivan@mail.com', '$2a$10$E6wEd5ZRv4FySOsZGki.WOSobQpWCg6iYPXQCvhhEdNuHH8acTCyK', 1),
(2, 'john@gmail.com', '$2a$10$pnwv81N01nJ.nRFdqr7ulusGSjFCx7reqtMbJqrhPaQyRE1c7GQ7q', 2),
(3, 'romanzinin2005@gmail.com', '$2a$10$pnwv81N01nJ.nRFdqr7ulusGSjFCx7reqtMbJqrhPaQyRE1c7GQ7q', 1),
(6, 'bob@gmail.com', '$2a$10$eWMdhJqB.M9PNKs0b7zfP.HzDwPEd0HTgL4lNYnQKiMgk0hJesiBC', 1),
(8, 'danvor@gmail.com', '$2a$10$pnwv81N01nJ.nRFdqr7ulusGSjFCx7reqtMbJqrhPaQyRE1c7GQ7q', 3),
(9, 'deleted_user@gmail.com', '$2a$10$EJ84Vug8i/mn1iBFuahPRuw/RlrpXCtAvqvI1vSk19QRbSSTlMixy', 1),
(13, 'deleted_employee@gmail.com', '$2a$10$Cz.u6DKnT2BJ8PKk9zXAj.xANiwNIqK1GutgpoM2wU2HZ6owPdy8.', 2),
(15, 'tirion@gmail.com', '$2a$10$aPtn9lK535ZvdNBgfGGbDO2vY3as8pgHGHgA8k5nKt/bXZzignVoG', 2);

-- =========================
-- CLIENTS
-- =========================

INSERT INTO clients (id, birth_date, full_name, phone, user_id) VALUES
(1, '1995-05-20', 'Иван Петров', '+7 (999) 999-99-98', 1),
(2, '2005-01-17', 'Роман', '+7 (910) 876-49-52', 3),
(3, '2016-06-20', 'Bob', '+7-910-876-49-52', 6),
(5, '2026-04-24', 'Удаленный пользователь', '————————————', 9);

-- =========================
-- EMPLOYEES
-- =========================

INSERT INTO employees (id, full_name, phone, position, user_id) VALUES
(1, 'Джон Сноу', '+7-999-888-77-66', 'WORKING', 2),
(2, 'Данвор Кроу', '+7-910-111-22-33', 'WORKING', 8),
(4, 'Удаленный сотрудник', '—————————————', 'DAY_OFF', 13),
(6, 'Тирион', '+7 (999) 888-77-66', 'DAY_OFF', 15);

-- =========================
-- GAMES
-- =========================

INSERT INTO games (id, description, name, price, active) VALUES
(1, 'Игра про покупку улиц', 'Монополия', 1990.00, true),
(2, 'Игра про захват территории', 'Игра престолов', 6950.00, true),
(3, 'Игра про развитие', 'Эволюция', 1250.00, true),
(4, 'Игра про приключения и развитие персонажа', 'Манчкин', 1650.00, true),
(5, 'Игра про приготовление зелий', 'Зельеварение', 1350.00, false);

-- =========================
-- ORDERS
-- =========================

INSERT INTO orders (id, delivery_date, order_date, status, total_price, client_id, employee_id) VALUES
(1, '2026-04-25', '2026-03-18 19:22:16', 'COMPLETED', 3980.00, 1, 1),
(2, NULL, '2026-04-18 19:47:53', 'NOT_CREATED', NULL, 1, NULL),
(3, '2026-04-28', '2026-04-20 16:19:56', 'IN_TRANSIT', 8940.00, 2, 6),
(4, NULL, '2026-04-20 03:53:14', 'NOT_CREATED', NULL, 3, NULL),
(6, '2026-04-24', '2026-04-20 16:22:07', 'CANCELLED', 0.00, 2, 6),
(7, NULL, '2026-04-20 17:50:50', 'CREATED', 3240.00, 2, NULL),
(8, '2026-04-24', '2026-04-20 18:03:00', 'COMPLETED', 6950.00, 2, 1),
(9, '2026-04-25', '2026-04-24 16:54:33', 'COMPLETED', 31040.00, 2, 6),
(10, NULL, '2026-04-23 19:47:51', 'NOT_CREATED', 6950.00, 5, NULL),
(11, NULL, '2026-04-24 16:55:56', 'NOT_CREATED', NULL, 2, NULL);

-- =========================
-- ORDER ITEMS
-- =========================

INSERT INTO order_items (id, price_at_purchase, quantity, game_id, order_id) VALUES
(1, 1990.00, 2, 1, 1),
(2, 6950.00, 3, 2, 2),
(3, 6950.00, 1, 2, 2),
(5, 6950.00, 1, 2, 4),
(9, 1990.00, 1, 1, 3),
(10, 6950.00, 1, 2, 3),
(11, 1990.00, 2, 1, 6),
(12, 6950.00, 1, 2, 6),
(13, 1990.00, 1, 1, 7),
(14, 1250.00, 1, 3, 7),
(15, 6950.00, 1, 2, 8),
(20, 1250.00, 1, 3, 9),
(21, 6950.00, 4, 2, 9),
(22, 6950.00, 1, 2, 10),
(23, 1990.00, 1, 1, 9),
(24, 6950.00, 1, 2, 11),
(27, 1650.00, 1, 4, 11);