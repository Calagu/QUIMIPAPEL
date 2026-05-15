package com.qumi.app.dao;

import com.qumi.app.model.Role;
import com.qumi.app.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {
    public Optional<Usuario> authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ? AND activo = 1";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    public List<Usuario> findAll(String search) throws SQLException {
        String term = "%" + (search == null ? "" : search.trim()) + "%";
        String sql = """
                SELECT * FROM usuarios
                WHERE nombre LIKE ? OR username LIKE ? OR role LIKE ?
                ORDER BY id DESC
                """;
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) usuarios.add(map(rs));
            }
        }
        return usuarios;
    }

    public void save(Usuario usuario) throws SQLException {
        if (usuario.getId() == 0) {
            insert(usuario);
        } else {
            update(usuario);
        }
    }

    private void insert(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios(nombre, username, password, role, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, u);
            ps.executeUpdate();
        }
    }

    private void update(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET nombre=?, username=?, password=?, role=?, activo=? WHERE id=?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, u);
            ps.setInt(6, u.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM usuarios WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void fill(PreparedStatement ps, Usuario u) throws SQLException {
        ps.setString(1, u.getNombre());
        ps.setString(2, u.getUsername());
        ps.setString(3, u.getPassword());
        ps.setString(4, u.getRole().name());
        ps.setInt(5, u.isActivo() ? 1 : 0);
    }

    private Usuario map(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("username"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role")),
                rs.getInt("activo") == 1
        );
    }
}
