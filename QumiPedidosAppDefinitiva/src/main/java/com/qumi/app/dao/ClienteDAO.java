package com.qumi.app.dao;

import com.qumi.app.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    public List<Cliente> findAll(String search) throws SQLException {
        String term = "%" + (search == null ? "" : search.trim()) + "%";
        String sql = """
                SELECT * FROM clientes
                WHERE nombre LIKE ? OR empresa LIKE ? OR telefono LIKE ? OR email LIKE ? OR direccion LIKE ? OR zona LIKE ?
                ORDER BY id DESC
                """;
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 6; i++) ps.setString(i, term);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) clientes.add(map(rs));
            }
        }
        return clientes;
    }

    public List<Cliente> findActive() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM clientes WHERE activo=1 ORDER BY empresa, nombre")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) clientes.add(map(rs));
            }
        }
        return clientes;
    }

    public void save(Cliente c) throws SQLException {
        if (c.getId() == 0) insert(c); else update(c);
    }

    private void insert(Cliente c) throws SQLException {
        String sql = "INSERT INTO clientes(nombre, empresa, telefono, email, direccion, zona, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, c);
            ps.executeUpdate();
        }
    }

    private void update(Cliente c) throws SQLException {
        String sql = "UPDATE clientes SET nombre=?, empresa=?, telefono=?, email=?, direccion=?, zona=?, activo=? WHERE id=?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, c);
            ps.setInt(8, c.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM clientes WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void fill(PreparedStatement ps, Cliente c) throws SQLException {
        ps.setString(1, c.getNombre());
        ps.setString(2, c.getEmpresa());
        ps.setString(3, c.getTelefono());
        ps.setString(4, c.getEmail());
        ps.setString(5, c.getDireccion());
        ps.setString(6, c.getZona());
        ps.setInt(7, c.isActivo() ? 1 : 0);
    }

    private Cliente map(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("empresa"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("direccion"),
                rs.getString("zona"),
                rs.getInt("activo") == 1
        );
    }
}
