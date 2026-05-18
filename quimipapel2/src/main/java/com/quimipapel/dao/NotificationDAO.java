package com.quimipapel.dao;

import com.quimipapel.util.DatabaseUtil;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class NotificationDAO {

    public Map<String, Boolean> findByUsuarioId(int usuarioId) {
        ensureRow(usuarioId);
        Map<String, Boolean> prefs = defaults();
        String sql = "SELECT nuevos_pedidos,pedidos_urgentes,incidencias,entregas_completadas,actualizaciones_sistema FROM notificaciones_config WHERE usuario_id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                prefs.put("nuevos_pedidos", rs.getBoolean("nuevos_pedidos"));
                prefs.put("pedidos_urgentes", rs.getBoolean("pedidos_urgentes"));
                prefs.put("incidencias", rs.getBoolean("incidencias"));
                prefs.put("entregas_completadas", rs.getBoolean("entregas_completadas"));
                prefs.put("actualizaciones_sistema", rs.getBoolean("actualizaciones_sistema"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return prefs;
    }

    public boolean updatePreference(int usuarioId, String key, boolean value) {
        if (!defaults().containsKey(key)) return false;
        ensureRow(usuarioId);
        String sql = "UPDATE notificaciones_config SET " + key + "=? WHERE usuario_id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, value);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public void ensureRow(int usuarioId) {
        String sql = "INSERT INTO notificaciones_config (usuario_id) VALUES (?) ON DUPLICATE KEY UPDATE usuario_id=VALUES(usuario_id)";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Map<String, Boolean> defaults() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("nuevos_pedidos", true);
        map.put("pedidos_urgentes", true);
        map.put("incidencias", true);
        map.put("entregas_completadas", false);
        map.put("actualizaciones_sistema", false);
        return map;
    }
}
