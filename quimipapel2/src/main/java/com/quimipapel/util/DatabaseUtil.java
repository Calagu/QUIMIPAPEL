package com.quimipapel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexión a MySQL.
 *
 * Por defecto conecta a:
 *   jdbc:mysql://localhost:3306/quimipapel
 *   usuario: root
 *   contraseña: vacía
 *
 * También permite configurar desde variables de entorno o parámetros VM:
 *   QUIMIPAPEL_DB_URL, QUIMIPAPEL_DB_USER, QUIMIPAPEL_DB_PASS
 *   -Dquimipapel.db.url=... -Dquimipapel.db.user=... -Dquimipapel.db.pass=...
 */
public class DatabaseUtil {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/quimipapel?useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";

    private static Connection connection;

    private DatabaseUtil() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            connection = DriverManager.getConnection(getUrl(), getUser(), getPass());
        }
        return connection;
    }

    public static boolean testConnection() {
        try (Connection c = DriverManager.getConnection(getUrl(), getUser(), getPass())) {
            return c != null && c.isValid(2);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getUrl() {
        return readConfig("quimipapel.db.url", "QUIMIPAPEL_DB_URL", DEFAULT_URL);
    }

    public static String getUser() {
        return readConfig("quimipapel.db.user", "QUIMIPAPEL_DB_USER", "root");
    }

    public static String getPass() {
        return readConfig("quimipapel.db.pass", "QUIMIPAPEL_DB_PASS", "");
    }

    private static String readConfig(String property, String env, String defaultValue) {
        String fromProperty = System.getProperty(property);
        if (fromProperty != null && !fromProperty.isBlank()) return fromProperty;
        String fromEnv = System.getenv(env);
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;
        return defaultValue;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }
}
