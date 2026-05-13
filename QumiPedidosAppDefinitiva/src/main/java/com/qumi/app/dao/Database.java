package com.qumi.app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static final String URL = "jdbc:sqlite:qumi_pedidos.db";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static void init() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");

            st.execute("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL,
                        activo INTEGER NOT NULL DEFAULT 1
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS clientes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        empresa TEXT,
                        telefono TEXT,
                        email TEXT,
                        direccion TEXT NOT NULL,
                        zona TEXT,
                        activo INTEGER NOT NULL DEFAULT 1
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS productos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        categoria TEXT NOT NULL,
                        descripcion TEXT,
                        precio REAL NOT NULL,
                        stock INTEGER NOT NULL,
                        stock_minimo INTEGER NOT NULL DEFAULT 5,
                        activo INTEGER NOT NULL DEFAULT 1
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS pedidos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        cliente_id INTEGER NOT NULL,
                        producto_id INTEGER NOT NULL,
                        cantidad INTEGER NOT NULL,
                        precio_unitario REAL NOT NULL,
                        total REAL NOT NULL,
                        estado TEXT NOT NULL,
                        urgencia TEXT NOT NULL,
                        fecha_pedido TEXT NOT NULL,
                        fecha_entrega TEXT,
                        direccion_entrega TEXT NOT NULL,
                        notas TEXT,
                        FOREIGN KEY(cliente_id) REFERENCES clientes(id),
                        FOREIGN KEY(producto_id) REFERENCES productos(id)
                    )
                    """);

            seedIfEmpty(conn);
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo inicializar la base de datos SQLite", e);
        }
    }

    private static void seedIfEmpty(Connection conn) throws SQLException {
        if (count(conn, "usuarios") == 0) {
            insertUser(conn, "Administrador QUMI", "admin", "admin123", "ADMIN");
            insertUser(conn, "Equipo comercial", "comercial", "comercial123", "COMERCIAL");
            insertUser(conn, "Oficina", "oficina", "oficina123", "OFICINA");
            insertUser(conn, "Repartidor", "reparto", "reparto123", "REPARTIDOR");
        }

        if (count(conn, "clientes") == 0) {
            insertCliente(conn, "Laura Gómez", "Papelería Centro", "600111222", "centro@qumi.es", "C/ Mayor 12", "Centro");
            insertCliente(conn, "Mario Ruiz", "Oficinas Norte", "600333444", "norte@qumi.es", "Av. Norte 8", "Norte");
            insertCliente(conn, "Sara Molina", "Colegio Alba", "600555666", "alba@qumi.es", "C/ Escuela 4", "Sur");
        }

        if (count(conn, "productos") == 0) {
            insertProducto(conn, "Pack folios A4 500 hojas", "Papel", "Papel blanco 80 g/m²", 4.99, 120, 20);
            insertProducto(conn, "Archivador palanca", "Oficina", "Archivador tamaño A4", 3.50, 65, 10);
            insertProducto(conn, "Bolígrafos azules x50", "Escritura", "Caja de 50 bolígrafos", 12.90, 42, 8);
            insertProducto(conn, "Cartuchos impresora negro", "Impresión", "Compatible alta capacidad", 18.75, 18, 5);
        }

        if (count(conn, "pedidos") == 0) {
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO pedidos(cliente_id, producto_id, cantidad, precio_unitario, total, estado, urgencia,
                                        fecha_pedido, fecha_entrega, direccion_entrega, notas)
                    VALUES (?, ?, ?, ?, ?, ?, ?, date('now'), date('now', '+1 day'), ?, ?)
                    """)) {
                ps.setInt(1, 1);
                ps.setInt(2, 1);
                ps.setInt(3, 5);
                ps.setDouble(4, 4.99);
                ps.setDouble(5, 24.95);
                ps.setString(6, "PENDIENTE");
                ps.setString(7, "ALTA");
                ps.setString(8, "C/ Mayor 12");
                ps.setString(9, "Entregar por la mañana");
                ps.executeUpdate();

                ps.setInt(1, 2);
                ps.setInt(2, 3);
                ps.setInt(3, 2);
                ps.setDouble(4, 12.90);
                ps.setDouble(5, 25.80);
                ps.setString(6, "EN_REPARTO");
                ps.setString(7, "NORMAL");
                ps.setString(8, "Av. Norte 8");
                ps.setString(9, "Llamar antes de llegar");
                ps.executeUpdate();
            }
        }
    }

    private static int count(Connection conn, String table) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static void insertUser(Connection conn, String nombre, String username, String password, String role) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO usuarios(nombre, username, password, role, activo) VALUES (?, ?, ?, ?, 1)")) {
            ps.setString(1, nombre);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }

    private static void insertCliente(Connection conn, String nombre, String empresa, String telefono, String email, String direccion, String zona) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO clientes(nombre, empresa, telefono, email, direccion, zona, activo) VALUES (?, ?, ?, ?, ?, ?, 1)")) {
            ps.setString(1, nombre);
            ps.setString(2, empresa);
            ps.setString(3, telefono);
            ps.setString(4, email);
            ps.setString(5, direccion);
            ps.setString(6, zona);
            ps.executeUpdate();
        }
    }

    private static void insertProducto(Connection conn, String nombre, String categoria, String descripcion, double precio, int stock, int stockMinimo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO productos(nombre, categoria, descripcion, precio, stock, stock_minimo, activo) VALUES (?, ?, ?, ?, ?, ?, 1)")) {
            ps.setString(1, nombre);
            ps.setString(2, categoria);
            ps.setString(3, descripcion);
            ps.setDouble(4, precio);
            ps.setInt(5, stock);
            ps.setInt(6, stockMinimo);
            ps.executeUpdate();
        }
    }
}
