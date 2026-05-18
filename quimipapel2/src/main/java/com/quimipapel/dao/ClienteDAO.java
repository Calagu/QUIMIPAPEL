package com.quimipapel.dao;

import com.quimipapel.model.Cliente;
import com.quimipapel.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> findAll() {
        List<Cliente> list = new ArrayList<>();
        String sql = """
            SELECT c.*,
                   COUNT(p.id)      AS total_pedidos,
                   COALESCE(SUM(p.total),0) AS total_facturado
            FROM clientes c
            LEFT JOIN pedidos p ON p.cliente_id = c.id
            GROUP BY c.id
            ORDER BY c.empresa
            """;
        try (Connection con = DatabaseUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Cliente> search(String q) {
        List<Cliente> list = new ArrayList<>();
        String sql = """
            SELECT c.*,
                   COUNT(p.id) AS total_pedidos,
                   COALESCE(SUM(p.total),0) AS total_facturado
            FROM clientes c
            LEFT JOIN pedidos p ON p.cliente_id = c.id
            WHERE c.empresa LIKE ? OR c.contacto LIKE ? OR c.email LIKE ? OR c.telefono LIKE ?
            GROUP BY c.id ORDER BY c.empresa
            """;
        try (Connection con = DatabaseUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + q + "%";
            for (int i = 1; i <= 4; i++) ps.setString(i, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean save(Cliente c) {
        String sql = "INSERT INTO clientes (empresa,contacto,telefono,email,direccion,ciudad,codigo_postal) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DatabaseUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getEmpresa());
            ps.setString(2, c.getContacto());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getCiudad());
            ps.setString(7, c.getCodigoPostal());
            ps.executeUpdate();
            ResultSet gk = ps.getGeneratedKeys();
            if (gk.next()) c.setId(gk.getInt(1));
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Cliente c) {
        String sql = "UPDATE clientes SET empresa=?,contacto=?,telefono=?,email=?,direccion=?,ciudad=?,codigo_postal=?,activo=? WHERE id=?";
        try (Connection con = DatabaseUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getEmpresa());
            ps.setString(2, c.getContacto());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getCiudad());
            ps.setString(7, c.getCodigoPostal());
            ps.setBoolean(8, c.isActivo());
            ps.setInt(9, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "UPDATE clientes SET activo=0 WHERE id=?";
        try (Connection con = DatabaseUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setEmpresa(rs.getString("empresa"));
        c.setContacto(rs.getString("contacto"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setCiudad(rs.getString("ciudad"));
        c.setCodigoPostal(rs.getString("codigo_postal"));
        c.setActivo(rs.getBoolean("activo"));
        try { c.setTotalPedidos(rs.getInt("total_pedidos")); } catch (SQLException ignored) {}
        try { c.setTotalFacturado(rs.getDouble("total_facturado")); } catch (SQLException ignored) {}
        return c;
    }
}
