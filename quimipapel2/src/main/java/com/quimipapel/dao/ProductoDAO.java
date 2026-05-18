package com.quimipapel.dao;

import com.quimipapel.model.Producto;
import com.quimipapel.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> findAll() {
        List<Producto> list = new ArrayList<>();
        String sql = """
            SELECT p.*, cat.nombre AS cat_nombre
            FROM productos p
            LEFT JOIN categorias cat ON cat.id = p.categoria_id
            WHERE p.activo = 1
            ORDER BY p.nombre
            """;
        try (Connection c = DatabaseUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Producto findById(int id) {
        String sql = """
            SELECT p.*, cat.nombre AS cat_nombre
            FROM productos p
            LEFT JOIN categorias cat ON cat.id = p.categoria_id
            WHERE p.id=? AND p.activo=1
            """;
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Producto> search(String q, String categoria, String stockFiltro) {
        List<Producto> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT p.*, cat.nombre AS cat_nombre
            FROM productos p
            LEFT JOIN categorias cat ON cat.id = p.categoria_id
            WHERE p.activo = 1
            """);
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            sql.append(" AND (p.nombre LIKE ? OR p.sku LIKE ? OR cat.nombre LIKE ?)");
            params.add("%" + q + "%");
            params.add("%" + q + "%");
            params.add("%" + q + "%");
        }
        if (categoria != null && !categoria.isBlank() && !categoria.equals("Todas las categorías")) {
            sql.append(" AND cat.nombre = ?"); params.add(categoria);
        }
        if ("Bajo".equals(stockFiltro)) {
            sql.append(" AND p.stock <= p.stock_minimo AND p.stock > 0");
        } else if ("Sin stock".equals(stockFiltro)) {
            sql.append(" AND p.stock = 0");
        } else if ("Alto".equals(stockFiltro)) {
            sql.append(" AND p.stock > p.stock_minimo * 3");
        } else if ("Medio".equals(stockFiltro)) {
            sql.append(" AND p.stock > p.stock_minimo AND p.stock <= p.stock_minimo * 3");
        }
        sql.append(" ORDER BY p.nombre");
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i+1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String> getCategorias() {
        List<String> list = new ArrayList<>();
        try (Connection c = DatabaseUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT nombre FROM categorias ORDER BY nombre")) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int getCategoriaIdByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return 0;
        String sql = "SELECT id FROM categorias WHERE nombre=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean save(Producto p) {
        ensureCategoriaId(p);
        String sql = "INSERT INTO productos (nombre,sku,categoria_id,precio,stock,stock_minimo,activo) VALUES (?,?,?,?,?,?,1)";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getSku());
            if (p.getCategoriaId() > 0) ps.setInt(3, p.getCategoriaId()); else ps.setNull(3, Types.INTEGER);
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            ps.setInt(6, p.getStockMinimo());
            ps.executeUpdate();
            ResultSet gk = ps.getGeneratedKeys();
            if (gk.next()) p.setId(gk.getInt(1));
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Producto p) {
        ensureCategoriaId(p);
        String sql = "UPDATE productos SET nombre=?,sku=?,categoria_id=?,precio=?,stock=?,stock_minimo=?,activo=? WHERE id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getSku());
            if (p.getCategoriaId() > 0) ps.setInt(3, p.getCategoriaId()); else ps.setNull(3, Types.INTEGER);
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            ps.setInt(6, p.getStockMinimo());
            ps.setBoolean(7, p.isActivo());
            ps.setInt(8, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "UPDATE productos SET activo=0 WHERE id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private void ensureCategoriaId(Producto p) {
        if (p.getCategoriaId() <= 0 && p.getCategoriaNombre() != null) {
            p.setCategoriaId(getCategoriaIdByNombre(p.getCategoriaNombre()));
        }
    }

    private Producto mapRow(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setSku(rs.getString("sku"));
        p.setCategoriaId(rs.getInt("categoria_id"));
        try { p.setCategoriaNombre(rs.getString("cat_nombre")); } catch (SQLException ignored) {}
        p.setPrecio(rs.getDouble("precio"));
        p.setStock(rs.getInt("stock"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }
}
