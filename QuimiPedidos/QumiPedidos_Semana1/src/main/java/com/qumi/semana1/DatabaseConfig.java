package com.qumi.semana1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    // Semana 1: solo configuración inicial de conexión.
    // La lógica real de la app se implementará en la semana 2.
    private static final String URL = "jdbc:mysql://localhost:3306/qumi_pedidos_semana1?useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
