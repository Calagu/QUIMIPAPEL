package com.quimipapel.dao;

import com.quimipapel.model.Usuario;
import com.quimipapel.util.DatabaseUtil;
import com.quimipapel.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario validarCredenciales(String email, String password) {
        Usuario user = findByEmail(email);
        if (user != null && user.isActivo() && PasswordUtil.verify(password, user.getPasswordHash())) {
            updateUltimoAcceso(user.getId());
            return user;
        }
        return null;
    }

    public Usuario findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND activo = 1";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Usuario> findAll() {
        List<Usuario> list = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY activo DESC, nombre";
        try (Connection c = DatabaseUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Usuario> findByRol(String rol) {
        List<Usuario> list = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol=? AND activo=1 ORDER BY nombre";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, rol);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean save(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre,email,telefono,password_hash,rol,activo) VALUES (?,?,?,?,?,1)";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getPasswordHash());
            ps.setString(5, u.getRol());
            ps.executeUpdate();
            ResultSet gk = ps.getGeneratedKeys();
            if (gk.next()) u.setId(gk.getInt(1));
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Usuario u) {
        String sql = "UPDATE usuarios SET nombre=?,email=?,telefono=?,rol=?,activo=? WHERE id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getRol());
            ps.setBoolean(5, u.isActivo());
            ps.setInt(6, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updatePassword(int id, String newHash) {
        String sql = "UPDATE usuarios SET password_hash=? WHERE id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public void updateUltimoAcceso(int id) {
        String sql = "UPDATE usuarios SET ultimo_acceso=NOW() WHERE id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean delete(int id) {
        String sql = "UPDATE usuarios SET activo=0 WHERE id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Contadores para Dashboard / Usuarios
    public int countTotal() { return countBy("SELECT COUNT(*) FROM usuarios"); }
    public int countActivos() { return countBy("SELECT COUNT(*) FROM usuarios WHERE activo=1"); }
    public int countInactivos() { return countBy("SELECT COUNT(*) FROM usuarios WHERE activo=0"); }
    public int countAdmins() { return countBy("SELECT COUNT(*) FROM usuarios WHERE rol='Administrador' AND activo=1"); }

    private int countBy(String sql) {
        try (Connection c = DatabaseUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setTelefono(rs.getString("telefono"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        Timestamp ts = rs.getTimestamp("ultimo_acceso");
        if (ts != null) u.setUltimoAcceso(ts.toLocalDateTime());
        return u;
    }
}
