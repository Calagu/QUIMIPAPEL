package com.quimipapel.view;

import com.quimipapel.dao.PedidoDAO;
import com.quimipapel.dao.UsuarioDAO;
import com.quimipapel.model.Pedido;
import com.quimipapel.model.Usuario;
import com.quimipapel.util.StyleHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RepartoView {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private VBox root;
    private HBox kpis;
    private VBox entregasCard;

    public Region build() {
        root = new VBox(20);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + StyleHelper.BG_MAIN + ";");

        Label titleLbl = new Label("Vista de Reparto");
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 26));
        Label subLbl = new Label("Consulta pedidos de reparto, datos del cliente y marca cargado, entregado o incidencia");
        subLbl.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;");
        VBox header = new VBox(4, titleLbl, subLbl);

        kpis = new HBox(16);
        root.getChildren().addAll(header, kpis, buildPanel());
        refreshData();
        return root;
    }

    private HBox buildPanel() {
        HBox panelRow = new HBox(20);

        VBox conductoresCard = StyleHelper.card(12);
        conductoresCard.setPadding(new Insets(20));
        conductoresCard.setSpacing(12);
        conductoresCard.setPrefWidth(300);
        Label condTitle = new Label("Repartidores");
        condTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        conductoresCard.getChildren().addAll(condTitle, StyleHelper.separator());

        List<Usuario> repartidores = usuarioDAO.findByRol("Repartidor");
        if (repartidores.isEmpty()) {
            conductoresCard.getChildren().add(gray("No hay repartidores activos."));
        }
        for (Usuario u : repartidores) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 0, 8, 0));
            StackPane av = StyleHelper.avatar(u.getIniciales(), StyleHelper.GREEN, 38);
            VBox info = new VBox(2);
            Label nombre = new Label(u.getNombre());
            nombre.setStyle("-fx-font-weight:bold;-fx-font-size:13;");
            Label rol = gray("Repartidor activo");
            info.getChildren().addAll(nombre, rol);
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Label badge = new Label("Disponible");
            badge.setStyle("-fx-background-color:#DCFCE7;-fx-text-fill:" + StyleHelper.GREEN + ";-fx-background-radius:20;-fx-padding:2 10;-fx-font-size:11;-fx-font-weight:bold;");
            row.getChildren().addAll(av, info, sp, badge);
            conductoresCard.getChildren().addAll(row, StyleHelper.separator());
        }

        entregasCard = StyleHelper.card(12);
        entregasCard.setPadding(new Insets(20));
        entregasCard.setSpacing(12);
        HBox.setHgrow(entregasCard, Priority.ALWAYS);

        panelRow.getChildren().addAll(conductoresCard, entregasCard);
        return panelRow;
    }

    private void refreshData() {
        List<Usuario> repartidores = usuarioDAO.findByRol("Repartidor");
        List<Pedido> pendientes = pedidoDAO.findRepartoPendiente();
        int cargados = (int) pendientes.stream().filter(p -> "Cargado".equals(p.getEstado())).count();
        int urgentes = (int) pendientes.stream().filter(p -> "Urgente".equals(p.getUrgencia())).count();

        kpis.getChildren().setAll(
            kpiCard("🚚", "Repartidores activos", repartidores.size(), "#3B82F6"),
            kpiCard("📦", "Pedidos para reparto", pendientes.size(), StyleHelper.COLOR_PENDIENTE),
            kpiCard("✅", "Cargados", cargados, "#6366F1"),
            kpiCard("❗", "Urgentes", urgentes, StyleHelper.COLOR_INCIDENCIA)
        );
        kpis.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        renderEntregas(pendientes);
    }

    private void renderEntregas(List<Pedido> pedidos) {
        entregasCard.getChildren().clear();
        Label entTitle = new Label("Entregas pendientes");
        entTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        entregasCard.getChildren().addAll(entTitle, StyleHelper.separator());

        if (pedidos.isEmpty()) {
            Label empty = new Label("No hay entregas pendientes.");
            empty.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";");
            entregasCard.getChildren().add(empty);
            return;
        }

        for (Pedido p : pedidos) {
            VBox pedCard = new VBox(8);
            pedCard.setPadding(new Insets(14));
            pedCard.setStyle("-fx-background-color:#F9FAFB;-fx-background-radius:10;-fx-border-color:" + StyleHelper.BORDER + ";-fx-border-radius:10;");

            HBox top = new HBox(8);
            top.setAlignment(Pos.CENTER_LEFT);
            Label pedId = new Label("Pedido " + p.getIdFormateado());
            pedId.setStyle("-fx-font-weight:bold;-fx-font-size:13;");
            top.getChildren().addAll(pedId, StyleHelper.badge(p.getEstado()));
            if ("Urgente".equals(p.getUrgencia())) {
                Label urg = new Label("URGENTE");
                urg.setStyle("-fx-background-color:#EF4444;-fx-text-fill:white;-fx-background-radius:20;-fx-padding:2 8;-fx-font-size:10;-fx-font-weight:bold;");
                top.getChildren().add(urg);
            }
            Region spTop = new Region(); HBox.setHgrow(spTop, Priority.ALWAYS);
            top.getChildren().add(spTop);
            if (p.getFecha() != null) {
                Label hora = new Label("⏰ " + p.getFecha().format(FMT_HORA));
                hora.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-size:12;");
                top.getChildren().add(hora);
            }

            Label cliente = bold(p.getClienteNombre() != null ? p.getClienteNombre() : "Cliente sin nombre");
            Label direccion = gray("📍 " + (p.getClienteDireccion() != null ? p.getClienteDireccion() : "Sin dirección"));
            Label telefono = gray("☎ " + (p.getClienteTelefono() != null ? p.getClienteTelefono() : "Sin teléfono"));
            Label notas = gray("Notas: " + (p.getNotas() != null && !p.getNotas().isBlank() ? p.getNotas() : "—"));

            HBox info = new HBox(16);
            Label items = new Label("📦 " + p.getNumItems() + " productos");
            items.setStyle("-fx-font-size:12;");
            Label total = new Label(String.format("%.2f€", p.getTotal()));
            total.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-size:12;-fx-font-weight:bold;");
            info.getChildren().addAll(items, total);

            HBox acciones = new HBox(8);
            Button btnCargado = StyleHelper.btnSecondary("Marcar cargado");
            Button btnRuta = StyleHelper.btnSecondary("En reparto");
            Button btnEntregado = StyleHelper.btnPrimary("Entregado");
            Button btnIncidencia = new Button("Incidencia");
            btnIncidencia.setStyle("-fx-background-color:#FEE2E2;-fx-text-fill:#EF4444;-fx-background-radius:8;-fx-font-size:13;-fx-padding:8 14;-fx-cursor:hand;-fx-font-weight:bold;");

            btnCargado.setOnAction(e -> cambiarEstado(p, "Cargado"));
            btnRuta.setOnAction(e -> cambiarEstado(p, "En reparto"));
            btnEntregado.setOnAction(e -> cambiarEstado(p, "Entregado"));
            btnIncidencia.setOnAction(e -> cambiarEstado(p, "Incidencia"));
            acciones.getChildren().addAll(btnCargado, btnRuta, btnEntregado, btnIncidencia);

            pedCard.getChildren().addAll(top, cliente, direccion, telefono, notas, info, acciones);
            entregasCard.getChildren().add(pedCard);
        }
    }

    private void cambiarEstado(Pedido p, String nuevoEstado) {
        if (pedidoDAO.updateEstado(p.getId(), nuevoEstado)) {
            refreshData();
        } else {
            new Alert(Alert.AlertType.ERROR, "No se pudo actualizar el estado del pedido.", ButtonType.OK).showAndWait();
        }
    }

    private VBox kpiCard(String icon, String label, int valor, String color) {
        VBox card = StyleHelper.card(12);
        card.setPadding(new Insets(20));
        card.setSpacing(8);
        Label ic = new Label(icon);
        ic.setStyle(String.format("-fx-background-color:%s20;-fx-text-fill:%s;-fx-background-radius:8;-fx-padding:8;-fx-font-size:18;", color, color));
        Label num = new Label(String.valueOf(valor));
        num.setFont(Font.font("System", FontWeight.BOLD, 28));
        num.setStyle("-fx-text-fill:" + color + ";");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:12;");
        card.getChildren().addAll(ic, num, lbl);
        return card;
    }

    private Label bold(String t)  { Label l = new Label(t); l.setStyle("-fx-font-weight:bold;-fx-font-size:13;"); return l; }
    private Label gray(String t)  { Label l = new Label(t); l.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:12;"); return l; }
}
