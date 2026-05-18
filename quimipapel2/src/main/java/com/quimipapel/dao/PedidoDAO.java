package com.quimipapel.dao;

import com.quimipapel.model.Pedido;
import com.quimipapel.model.PedidoItem;
import com.quimipapel.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private static final String SELECT_BASE = """
        SELECT p.*,
               cl.empresa AS cliente_nombre,
               CONCAT_WS(', ', cl.direccion, CONCAT_WS(' ', cl.codigo_postal, cl.ciudad)) AS cliente_direccion,
               cl.telefono AS cliente_telefono,
               u.nombre AS conductor_nombre,
               COUNT(pi.id) AS num_items
        FROM pedidos p
        LEFT JOIN clientes cl ON cl.id = p.cliente_id
        LEFT JOIN conductores co ON co.id = p.conductor_id
        LEFT JOIN usuarios u ON u.id = co.usuario_id
        LEFT JOIN pedido_items pi ON pi.pedido_id = p.id
        """;

    public List<Pedido> findAll() {
        return filter(null, null, null, null, null);
    }

    public List<Pedido> findRecientes(int limit) {
        return query(SELECT_BASE + " GROUP BY p.id ORDER BY p.fecha DESC LIMIT " + limit, null);
    }

    public Pedido findById(int id) {
        List<Object> params = new ArrayList<>();
        params.add(id);
        List<Pedido> pedidos = query(SELECT_BASE + " WHERE p.id=? GROUP BY p.id LIMIT 1", params);
        return pedidos.isEmpty() ? null : pedidos.get(0);
    }

    public boolean updateCabecera(Pedido p) {
        String sql = "UPDATE pedidos SET cliente_id=?, estado=?, urgencia=?, reparto=?, notas=? WHERE id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, p.getClienteId());
            ps.setString(2, p.getEstado());
            ps.setString(3, p.getUrgencia());
            ps.setBoolean(4, p.isReparto());
            ps.setString(5, p.getNotas());
            ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Pedido> filter(String estado, String urgencia, LocalDate desde, LocalDate hasta) {
        return filter(estado, urgencia, desde, hasta, null);
    }

    public List<Pedido> filter(String estado, String urgencia, LocalDate desde, LocalDate hasta, String busqueda) {
        StringBuilder sql = new StringBuilder(SELECT_BASE + " WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (estado != null && !estado.isBlank() && !estado.equals("Todos")) {
            sql.append("AND p.estado=? "); params.add(estado);
        }
        if (urgencia != null && !urgencia.isBlank() && !urgencia.equals("Todos")) {
            sql.append("AND p.urgencia=? "); params.add(urgencia);
        }
        if (desde != null) { sql.append("AND DATE(p.fecha)>=? "); params.add(desde.toString()); }
        if (hasta != null) { sql.append("AND DATE(p.fecha)<=? "); params.add(hasta.toString()); }
        if (busqueda != null && !busqueda.isBlank()) {
            sql.append("AND (CAST(p.id AS CHAR) LIKE ? OR cl.empresa LIKE ? OR p.notas LIKE ? OR cl.telefono LIKE ?) ");
            String like = "%" + busqueda + "%";
            params.add(like); params.add(like); params.add(like); params.add(like);
        }
        sql.append("GROUP BY p.id ORDER BY FIELD(p.urgencia,'Urgente','Normal'), p.fecha DESC");
        return query(sql.toString(), params);
    }

    public List<Pedido> findRepartoPendiente() {
        String sql = SELECT_BASE + """
            WHERE p.reparto = 1
              AND p.estado <> 'Entregado'
            GROUP BY p.id
            ORDER BY FIELD(p.urgencia,'Urgente','Normal'), p.fecha ASC
            """;
        return query(sql, null);
    }

    private List<Pedido> query(String sql, List<Object> params) {
        List<Pedido> list = new ArrayList<>();
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (params != null) for (int i = 0; i < params.size(); i++) ps.setObject(i+1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean save(Pedido p) {
        String sql = "INSERT INTO pedidos (cliente_id,usuario_id,conductor_id,estado,urgencia,reparto,notas,total) VALUES (?,?,?,?,?,?,?,?)";
        Connection c = null;
        try {
            c = DatabaseUtil.getConnection();
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, p.getClienteId());
                ps.setInt(2, p.getUsuarioId());
                if (p.getConductorId() != null) ps.setInt(3, p.getConductorId()); else ps.setNull(3, Types.INTEGER);
                ps.setString(4, p.getEstado());
                ps.setString(5, p.getUrgencia());
                ps.setBoolean(6, p.isReparto());
                ps.setString(7, p.getNotas());
                ps.setDouble(8, p.getTotal());
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) p.setId(gk.getInt(1));
            }
            saveItems(c, p);
            c.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    public boolean updateEstado(int id, String estado) {
        String sql = "UPDATE pedidos SET estado=? WHERE id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado); ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM pedidos WHERE id=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<PedidoItem> findItemsByPedidoId(int pedidoId) {
        List<PedidoItem> list = new ArrayList<>();
        String sql = """
            SELECT pi.*, pr.nombre AS producto_nombre
            FROM pedido_items pi
            INNER JOIN productos pr ON pr.id = pi.producto_id
            WHERE pi.pedido_id=?
            ORDER BY pi.id
            """;
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PedidoItem item = new PedidoItem();
                item.setId(rs.getInt("id"));
                item.setPedidoId(rs.getInt("pedido_id"));
                item.setProductoId(rs.getInt("producto_id"));
                item.setProductoNombre(rs.getString("producto_nombre"));
                item.setCantidad(rs.getInt("cantidad"));
                item.setPrecioUnit(rs.getDouble("precio_unit"));
                list.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- Dashboard stats ---
    public int countPendientes()  { return countByEstado("Pendiente"); }
    public int countUrgentes()    { return countBy("SELECT COUNT(*) FROM pedidos WHERE urgencia='Urgente' AND estado NOT IN ('Entregado')"); }
    public int countEntregadosHoy(){ return countBy("SELECT COUNT(*) FROM pedidos WHERE estado='Entregado' AND DATE(fecha)=CURDATE()"); }
    public int countIncidencias() { return countByEstado("Incidencia"); }

    private int countByEstado(String estado) {
        return countBy("SELECT COUNT(*) FROM pedidos WHERE estado='" + estado + "'");
    }
    private int countBy(String sql) {
        try (Connection c = DatabaseUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private void saveItems(Connection c, Pedido p) throws SQLException {
        if (p.getItems() == null || p.getItems().isEmpty()) return;
        String sqlItems = "INSERT INTO pedido_items (pedido_id,producto_id,cantidad,precio_unit) VALUES (?,?,?,?)";
        String sqlStock = "UPDATE productos SET stock = GREATEST(stock - ?, 0) WHERE id=?";
        try (PreparedStatement psItems = c.prepareStatement(sqlItems);
             PreparedStatement psStock = c.prepareStatement(sqlStock)) {
            for (PedidoItem item : p.getItems()) {
                psItems.setInt(1, p.getId());
                psItems.setInt(2, item.getProductoId());
                psItems.setInt(3, item.getCantidad());
                psItems.setDouble(4, item.getPrecioUnit());
                psItems.addBatch();

                psStock.setInt(1, item.getCantidad());
                psStock.setInt(2, item.getProductoId());
                psStock.addBatch();
            }
            psItems.executeBatch();
            psStock.executeBatch();
        }
    }

    private Pedido mapRow(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setClienteId(rs.getInt("cliente_id"));
        try { p.setClienteNombre(rs.getString("cliente_nombre")); } catch (SQLException ignored) {}
        try { p.setClienteDireccion(rs.getString("cliente_direccion")); } catch (SQLException ignored) {}
        try { p.setClienteTelefono(rs.getString("cliente_telefono")); } catch (SQLException ignored) {}
        p.setUsuarioId(rs.getInt("usuario_id"));
        int cid = rs.getInt("conductor_id"); if (!rs.wasNull()) p.setConductorId(cid);
        try { p.setConductorNombre(rs.getString("conductor_nombre")); } catch (SQLException ignored) {}
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) p.setFecha(ts.toLocalDateTime());
        p.setEstado(rs.getString("estado"));
        p.setUrgencia(rs.getString("urgencia"));
        p.setReparto(rs.getBoolean("reparto"));
        p.setNotas(rs.getString("notas"));
        p.setTotal(rs.getDouble("total"));
        try { p.setNumItems(rs.getInt("num_items")); } catch (SQLException ignored) {}
        return p;
    }
}
