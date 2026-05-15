package boardgames_shop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Инициализирует бд на новом пк (Минимальная миграция БД)
 *
 * Алгоритм:
 * 1. Подключается к postgres от имени суперпользователя
 * 2. Создаёт пользователя AdminRoma (если не существует)
 * 3. Создаёт БД boardgame_shop (если не существует)
 * 4. Выдаёт права AdminRoma на БД
 * 5. Подключается к boardgame_shop и выполняет schema.sql (если таблиц ещё нет)
 * 6. Выполняет data.sql (если таблица roles пуста)
 */
@Component
@Order(1)
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${init.datasource.url}")
    private String initUrl;

    @Value("${init.datasource.username}")
    private String initUsername;

    @Value("${init.datasource.password}")
    private String initPassword;

    @Value("${spring.datasource.url}")
    private String appUrl;

    @Value("${spring.datasource.username}")
    private String appUsername;

    @Value("${spring.datasource.password}")
    private String appPassword;

    // Имя БД и пользователя извлекаем из URL/username, чтобы не дублировать в конфиге
    private static final String DB_NAME   = "boardgame_shop";
    private static final String APP_USER  = "AdminRoma";

    // Метод вызывается вручную из BoardgamesShopApplication до старта Spring-контекста
    public void initialize() {

        log.info("=== DatabaseInitializer: старт ===");

        try {
            step1_createUserAndDatabase();
            step2_createSchemaAndData();
            log.info("=== DatabaseInitializer: завершено успешно ===");
        } catch (Exception e) {
            log.error("=== DatabaseInitializer: критическая ошибка ===", e);
            throw new RuntimeException("Не удалось инициализировать базу данных. " +
                    "Проверьте пароль суперпользователя postgres в application.properties " +
                    "(параметр init.datasource.password)", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ШАГ 1: создать пользователя и БД (подключение через postgres)
    // ─────────────────────────────────────────────────────────────
    private void step1_createUserAndDatabase() throws Exception {

        log.info("Подключение к postgres для создания пользователя и БД...");

        try (Connection conn = DriverManager.getConnection(initUrl, initUsername, initPassword);
             Statement stmt = conn.createStatement()) {

            // 1a. Создать пользователя AdminRoma (если не существует)
            boolean userExists = false;
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_roles WHERE rolname = '" + APP_USER + "'")) {
                userExists = rs.next();
            }

            if (!userExists) {
                log.info("Создаю пользователя {}...", APP_USER);
                stmt.execute(
                        "CREATE USER \"" + APP_USER + "\" WITH PASSWORD '" + appPassword + "'"
                );
                log.info("Пользователь {} создан.", APP_USER);
            } else {
                log.info("Пользователь {} уже существует.", APP_USER);
            }

            // 1b. Создать БД boardgame_shop (если не существует)
            boolean dbExists = false;
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + DB_NAME + "'")) {
                dbExists = rs.next();
            }

            if (!dbExists) {
                log.info("Создаю базу данных {}...", DB_NAME);
                // CREATE DATABASE нельзя выполнять в транзакции — autoCommit должен быть true
                conn.setAutoCommit(true);
                stmt.execute(
                        "CREATE DATABASE \"" + DB_NAME + "\" OWNER \"" + APP_USER + "\""
                );
                log.info("База данных {} создана.", DB_NAME);
            } else {
                log.info("База данных {} уже существует.", DB_NAME);
            }

            // 1c. Выдать права на БД
            conn.setAutoCommit(true);
            stmt.execute(
                    "GRANT ALL PRIVILEGES ON DATABASE \"" + DB_NAME + "\" TO \"" + APP_USER + "\""
            );
            log.info("Права на БД {} выданы пользователю {}.", DB_NAME, APP_USER);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ШАГ 2: создать схему и данные (подключение через AdminRoma)
    // ─────────────────────────────────────────────────────────────
    private void step2_createSchemaAndData() throws Exception {

        log.info("Подключение к {} для проверки схемы...", DB_NAME);

        try (Connection conn = DriverManager.getConnection(appUrl, appUsername, appPassword)) {

            // Проверяем: таблица roles существует?
            boolean schemaExists = tableExists(conn, "roles");

            if (!schemaExists) {
                log.info("Схема не найдена. Выполняю schema.sql...");
                executeSqlFile(conn, "schema.sql");
                log.info("schema.sql выполнен успешно.");
            } else {
                log.info("Схема уже существует, schema.sql пропущен.");
            }

            // Проверяем: есть ли данные в таблице roles?
            boolean hasData = false;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM roles")) {
                if (rs.next()) {
                    hasData = rs.getInt(1) > 0;
                }
            }

            if (!hasData) {
                log.info("Данные не найдены. Выполняю data.sql...");
                executeSqlFile(conn, "data.sql");
                log.info("data.sql выполнен успешно.");

                log.info("Сбрасываю sequences после импорта данных...");
                resetSequences(conn);
                log.info("Sequences сброшены.");
            } else {
                log.info("Данные уже существуют, data.sql пропущен.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ШАГ 3: сброс sequences (после вставки данных с явными id)
    // ─────────────────────────────────────────────────────────────

    /**
     * После INSERT с явными id sequence остаётся на 1 и при следующей вставке
     * Hibernate получает уже занятый id → duplicate key.
     *
     * Для каждой таблицы с IDENTITY-колонкой выполняем:
     *   SELECT setval(sequence_name, MAX(id))
     * Это сдвигает sequence на максимальный существующий id,
     * и следующая вставка получит MAX(id) + 1.
     */
    private void resetSequences(Connection conn) throws Exception {

        // Таблицы проекта с IDENTITY primary key (BIGINT GENERATED BY DEFAULT AS IDENTITY)
        String[] tables = {"roles", "users", "clients", "employees", "games", "orders", "order_items"};

        conn.setAutoCommit(true);
        try (Statement stmt = conn.createStatement()) {
            for (String table : tables) {
                // Получаем имя sequence для данной таблицы
                String seqQuery = String.format(
                        "SELECT pg_get_serial_sequence('%s', 'id')", table);

                String sequenceName = null;
                try (ResultSet rs = stmt.executeQuery(seqQuery)) {
                    if (rs.next()) {
                        sequenceName = rs.getString(1);
                    }
                }

                if (sequenceName == null) {
                    log.warn("Sequence для таблицы {} не найден, пропускаю.", table);
                    continue;
                }

                // Получаем максимальный id в таблице
                long maxId = 0;
                try (ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(id), 0) FROM " + table)) {
                    if (rs.next()) {
                        maxId = rs.getLong(1);
                    }
                }

                if (maxId > 0) {
                    // setval(sequence, value) — следующий nextval вернёт value + 1
                    stmt.execute(String.format("SELECT setval('%s', %d)", sequenceName, maxId));
                    log.info("  {} → sequence сброшен на {}", table, maxId);
                } else {
                    log.info("  {} → таблица пуста, sequence не тронут.", table);
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Вспомогательные методы
    // ─────────────────────────────────────────────────────────────

    private boolean tableExists(Connection conn, String tableName) throws Exception {
        try (ResultSet rs = conn.getMetaData().getTables(
                null, "public", tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    /**
     * Читает SQL-файл из classpath и выполняет каждый statement.
     * Разделитель — точка с запятой (;).
     * Пустые строки и комментарии (--) пропускаются.
     */
    private void executeSqlFile(Connection conn, String fileName) throws Exception {

        ClassPathResource resource = new ClassPathResource(fileName);

        String sql;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            sql = reader.lines().collect(Collectors.joining("\n"));
        }

        // Убираем блочные комментарии /* ... */
        sql = sql.replaceAll("/\\*.*?\\*/", "");

        String[] statements = sql.split(";");

        conn.setAutoCommit(false);
        try (Statement stmt = conn.createStatement()) {
            for (String statement : statements) {
                // Убираем строчные комментарии и пробелы
                String cleaned = removeLineComments(statement).trim();
                if (!cleaned.isEmpty()) {
                    stmt.execute(cleaned);
                }
            }
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
    }

    private String removeLineComments(String sql) {
        StringBuilder result = new StringBuilder();
        for (String line : sql.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("--")) {
                // Убираем inline-комментарий в конце строки
                int commentIdx = line.indexOf("--");
                if (commentIdx >= 0) {
                    result.append(line, 0, commentIdx);
                } else {
                    result.append(line);
                }
                result.append("\n");
            }
        }
        return result.toString();
    }
}