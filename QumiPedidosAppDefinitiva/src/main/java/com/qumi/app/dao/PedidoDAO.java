package com.qumi.app.dao;

import com.qumi.app.model.EstadoPedido;
import com.qumi.app.model.Indicadores;
import com.qumi.app.model.Pedido;
import com.qumi.app.model.Producto;
import com.qumi.app.model.Urgencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    public List<Pedido> findAll(String search, EstadoPedido estado, Urgencia urgencia, LocalDate desde, LocalDate hasta) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT p.*, c.empresa AS cliente_empresa, c.nombre AS cliente_nombre, pr.nombre AS producto_nombre
                FROM pedidos p
                JOIN clientes c ON c.id = p.cliente_id
                JOIN productos pr ON pr.id = p.producto_id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append(" AND (c.empresa LIKE ? OR c.nombre LIKE ? OR pr.nombre LIKE ? OR p.direccion_entrega LIKE ? OR p.notas LIKE ?) ");
            String term = "%" + search.trim() + "%";
            for (int i = 0; i < 5; i++) params.add(term);
        }
        if (estado != null) {
            sql.append(" AND p.estado = ? ");
            params.add(estado.name());
        }
        if (urgencia != null) {
            sql.append(" AND p.urgencia = ? ");
            params.add(urgencia.name());
        }
        if (desde != null) {
            sql.append(" AND date(p.fecha_pedido) >= date(?) ");
            params.add(desde.toString());
        }
        if (hasta != null) {
            sql.append(" AND date(p.fecha_pedido) <= date(?) ");
            params.add(hasta.toString());
        }
        sql.append(" ORDER BY CASE p.urgencia WHEN 'URGENTE' THEN 1 WHEN 'ALTA' THEN 2 WHEN 'NORMAL' THEN 3 ELSE 4 END, p.fecha_entrega ASC, p.id DESC");

        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) pedidos.add(map(rs));
            }
        }
        return pedidos;
    }

    public List<Pedido> findForReparto() throws SQLException {
        String sql = """
                SELECT p.*, c.empresa AS cliente_empresa, c.nombre AS cliente_nombre, pr.nombre AS producto_nombre
                FROM pedidos p
                JOIN clientes c ON c.id = p.cliente_id
                JOIN productos pr ON pr.id = p.producto_id
                WHERE p.estado IN ('PENDIENTE', 'EN_PREPARACION', 'EN_REPARTO', 'INCIDENCIA')
                ORDER BY CASE p.urgencia WHEN 'URGENTE' THEN 1 WHEN 'ALTA' THEN 2 WHEN 'NORMAL' THEN 3 ELSE 4 END,
                         p.fecha_entrega ASC, p.id DESC
                """;
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) pedidos.add(map(rs));
            }
        }
        return pedidos;
    }

    public void createPedido(int clienteId, Producto producto, int cantidad, Urgencia urgencia, LocalDate fechaEntrega, String direccionEntrega, String notas) throws SQLException {
        String checkStock = "SELECT stock FROM productos WHERE id = ? AND activo = 1";
        String updateStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";
        String insertPedido = """
                INSERT INTO pedidos(cliente_id, producto_id, cantidad, precio_unitario, total, estado, urgencia, fecha_pedido, fecha_entrega, direccion_entrega, notas)
                VALUES (?, ?, ?, ?, ?, ?, ?, date('now'), ?, ?, ?)
                """;

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int stock;
                try (PreparedStatement ps = conn.prepareStatement(checkStock)) {
                    ps.setInt(1, producto.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Producto no encontrado o inactivo.");
                        stock = rs.getInt("stock");
                    }
                }
                if (stock < cantidad) throw new SQLException("Stock insuficiente. Stock actual: " + stock);

                try (PreparedStatement ps = conn.prepareStatement(updateStock)) {
                    ps.setInt(1, cantidad);
                    ps.setInt(2, producto.getId());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(insertPedido, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, clienteId);
                    ps.setInt(2, producto.getId());
                    ps.setInt(3, cantidad);
                    ps.setDouble(4, producto.getPrecio());
                    ps.setDouble(5, cantidad * producto.getPrecio());
                    ps.setString(6, EstadoPedido.PENDIENTE.name());
                    ps.setString(7, urgencia.name());
                    ps.setString(8, fechaEntrega == null ? null : fechaEntrega.toString());
                    ps.setString(9, direccionEntrega);
                    ps.setString(10, notas);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void updateEstado(int pedidoId, EstadoPedido estado, String notas) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ?, notas = COALESCE(NULLIF(?, ''), notas) WHERE id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setString(2, notas == null ? "" : notas.trim());
            ps.setInt(3, pedidoId);
            ps.executeUpdate();
        }
    }

    public Indicadores getIndicadores() throws SQLException {
        int total = count("SELECT COUNT(*) FROM pedidos");
        int pendientes = count("SELECT COUNT(*) FROM pedidos WHERE estado IN ('PENDIENTE','EN_PREPARACION')");
        int enReparto = count("SELECT COUNT(*) FROM pedidos WHERE estado='EN_REPARTO'");
        int entregados = count("SELECT COUNT(*) FROM pedidos WHERE estado='ENTREGADO'");
        int urgentes = count("SELECT COUNT(*) FROM pedidos WHERE urgencia IN ('URGENTE','ALTA') AND estado NOT IN ('ENTREGADO','CANCELADO')");
        int stockBajo = new ProductoDAO().countStockBajo();
        return new Indicadores(total, pendientes, enReparto, entregados, urgentes, stockBajo);
    }

    private int count(String sql) throws SQLException {
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Pedido map(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setClienteId(rs.getInt("cliente_id"));
        String empresa = rs.getString("cliente_empresa");
        String nombre = rs.getString("cliente_nombre");
        p.setClienteNombre(empresa == null || empresa.isBlank() ? nombre : empresa + " - " + nombre);
        p.setProductoId(rs.getInt("producto_id"));
        p.setProductoNombre(rs.getString("producto_nombre"));
        p.setCantidad(rs.getInt("cantidad"));
        p.setPrecioUnitario(rs.getDouble("precio_unitario"));
        p.setTotal(rs.getDouble("total"));
        p.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
        p.setUrgencia(Urgencia.valueOf(rs.getString("urgencia")));
        p.setFechaPedido(parseDate(rs.getString("fecha_pedido")));
        p.setFechaEntrega(parseDate(rs.getString("fecha_entrega")));
        p.setDireccionEntrega(rs.getString("direccion_entrega"));
        p.setNotas(rs.getString("notas"));
        return p;
    }

    private LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
}
