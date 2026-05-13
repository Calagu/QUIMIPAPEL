package com.qumi.app.dao;

import com.qumi.app.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public List<Producto> findAll(String search) throws SQLException {
        String term = "%" + (search == null ? "" : search.trim()) + "%";
        String sql = """
                SELECT * FROM productos
                WHERE nombre LIKE ? OR categoria LIKE ? OR descripcion LIKE ?
                ORDER BY id DESC
                """;
        List<Producto> productos = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) productos.add(map(rs));
            }
        }
        return productos;
    }

    public List<Producto> findActive() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM productos WHERE activo=1 ORDER BY nombre")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) productos.add(map(rs));
            }
        }
        return productos;
    }

    public void save(Producto p) throws SQLException {
        if (p.getId() == 0) insert(p); else update(p);
    }

    private void insert(Producto p) throws SQLException {
        String sql = "INSERT INTO productos(nombre, categoria, descripcion, precio, stock, stock_minimo, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, p);
            ps.executeUpdate();
        }
    }

    private void update(Producto p) throws SQLException {
        String sql = "UPDATE productos SET nombre=?, categoria=?, descripcion=?, precio=?, stock=?, stock_minimo=?, activo=? WHERE id=?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, p);
            ps.setInt(8, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM productos WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int countStockBajo() throws SQLException {
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM productos WHERE stock <= stock_minimo AND activo = 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private void fill(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getCategoria());
        ps.setString(3, p.getDescripcion());
        ps.setDouble(4, p.getPrecio());
        ps.setInt(5, p.getStock());
        ps.setInt(6, p.getStockMinimo());
        ps.setInt(7, p.isActivo() ? 1 : 0);
    }

    private Producto map(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                rs.getInt("stock_minimo"),
                rs.getInt("activo") == 1
        );
    }
}
