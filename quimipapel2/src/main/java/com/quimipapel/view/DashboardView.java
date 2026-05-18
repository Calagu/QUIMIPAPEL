package com.quimipapel.view;

import com.quimipapel.dao.PedidoDAO;
import com.quimipapel.model.Pedido;
import com.quimipapel.util.SessionManager;
import com.quimipapel.util.StyleHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardView {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final Runnable onVerTodos;

    public DashboardView() { this(null); }

    public DashboardView(Runnable onVerTodos) {
        this.onVerTodos = onVerTodos;
    }
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Region build() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + StyleHelper.BG_MAIN + ";");

        // Cabecera
        String nombre = SessionManager.getInstance().getUsuarioActual() != null
                ? SessionManager.getInstance().getUsuarioActual().getNombre().split(" ")[0]
                : "Usuario";
        Label title = new Label("Dashboard");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        Label sub = new Label("Bienvenido, " + nombre + ". Aquí tienes un resumen de la actividad");
        sub.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;");
        VBox header = new VBox(4, title, sub);

        // KPI Cards
        int pendientes  = pedidoDAO.countPendientes();
        int urgentes    = pedidoDAO.countUrgentes();
        int entregados  = pedidoDAO.countEntregadosHoy();
        int incidencias = pedidoDAO.countIncidencias();

        HBox kpis = new HBox(16,
            kpiCard("⏰", "Pedidos pendientes", pendientes, "+3", StyleHelper.COLOR_PENDIENTE),
            kpiCard("❗", "Pedidos urgentes",   urgentes,   "+2", StyleHelper.COLOR_INCIDENCIA),
            kpiCard("✅", "Entregados hoy",     entregados, "+12",StyleHelper.COLOR_ENTREGADO),
            kpiCard("📦", "Incidencias",        incidencias, "-1", StyleHelper.COLOR_INCIDENCIA)
        );
        kpis.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        // Pedidos recientes
        List<Pedido> recientes = pedidoDAO.findRecientes(6);
        VBox pedidosCard = StyleHelper.card(12);
        pedidosCard.setPadding(new Insets(20));
        pedidosCard.setSpacing(12);

        HBox pedidosHeader = new HBox();
        pedidosHeader.setAlignment(Pos.CENTER_LEFT);
        Label pedidosTitle = new Label("Pedidos recientes");
        pedidosTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label verTodos = new Label("Ver todos");
        verTodos.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-cursor:hand;-fx-font-size:13;");
        verTodos.setOnMouseClicked(e -> { if (onVerTodos != null) onVerTodos.run(); });
        pedidosHeader.getChildren().addAll(pedidosTitle, sp, verTodos);

        // Cabecera tabla
        GridPane tabla = buildTablaRecientes(recientes);

        pedidosCard.getChildren().addAll(pedidosHeader, StyleHelper.separator(), tabla);

        root.getChildren().addAll(header, kpis, pedidosCard);
        return root;
    }

    private VBox kpiCard(String icon, String label, int valor, String delta, String color) {
        VBox card = StyleHelper.card(12);
        card.setPadding(new Insets(20));
        card.setSpacing(8);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(String.format(
            "-fx-background-color:%s20;-fx-text-fill:%s;-fx-background-radius:8;-fx-padding:8;-fx-font-size:18;",
            color, color));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label deltaLabel = new Label(delta);
        deltaLabel.setStyle(String.format(
            "-fx-background-color:%s;-fx-text-fill:white;-fx-background-radius:20;-fx-padding:2 8;-fx-font-size:11;-fx-font-weight:bold;",
            StyleHelper.GREEN));
        top.getChildren().addAll(iconLabel, sp, deltaLabel);

        Label num = new Label(String.valueOf(valor));
        num.setFont(Font.font("System", FontWeight.BOLD, 32));
        num.setStyle("-fx-text-fill:" + color + ";");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:12;");

        card.getChildren().addAll(top, num, lbl);
        return card;
    }

    private GridPane buildTablaRecientes(List<Pedido> pedidos) {
        GridPane g = new GridPane();
        g.setHgap(16); g.setVgap(12);

        String[] cols = {"ID", "Cliente", "Fecha", "Estado", "Items", "Total"};
        double[] widths = {70, 200, 150, 130, 60, 100};

        for (int i = 0; i < cols.length; i++) {
            Label h = new Label(cols[i]);
            h.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:12;-fx-font-weight:bold;");
            g.add(h, i, 0);
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPrefWidth(widths[i]);
            g.getColumnConstraints().add(cc);
        }

        int row = 1;
        for (Pedido p : pedidos) {
            Label id = new Label(p.getIdFormateado());
            id.setStyle("-fx-font-weight:bold;-fx-font-size:13;");
            Label cliente = new Label(p.getClienteNombre() != null ? p.getClienteNombre() : "-");
            cliente.setStyle("-fx-font-size:13;");
            Label fecha = new Label(p.getFecha() != null ? p.getFecha().format(FMT) : "-");
            fecha.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:12;");
            Label estado = StyleHelper.badge(p.getEstado());
            Label items = new Label(String.valueOf(p.getNumItems()));
            items.setStyle("-fx-font-size:13;");
            Label total = new Label(String.format("%.2f€", p.getTotal()));
            total.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-weight:bold;-fx-font-size:13;");

            g.addRow(row++, id, cliente, fecha, estado, items, total);
        }

        if (pedidos.isEmpty()) {
            Label vacio = new Label("No hay pedidos recientes.");
            vacio.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";");
            g.add(vacio, 0, 1, 6, 1);
        }
        return g;
    }
}
