package com.quimipapel.view;

import com.quimipapel.dao.ClienteDAO;
import com.quimipapel.dao.PedidoDAO;
import com.quimipapel.dao.ProductoDAO;
import com.quimipapel.model.Cliente;
import com.quimipapel.model.Pedido;
import com.quimipapel.model.PedidoItem;
import com.quimipapel.model.Producto;
import com.quimipapel.util.SessionManager;
import com.quimipapel.util.StyleHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PedidosView {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private VBox tablaContainer;
    private List<Pedido> ultimaLista = new ArrayList<>();

    public Region build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + StyleHelper.BG_MAIN + ";");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(4,
            createTitle("Gestión de Pedidos"),
            createSub("Crea pedidos con productos, cantidades, urgencia, observaciones y reparto"));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnExport = StyleHelper.btnSecondary("Exportar CSV");
        btnExport.setOnAction(e -> exportarCsv());
        Button btnNuevo = StyleHelper.btnPrimary("+ Nuevo pedido");
        btnNuevo.setOnAction(e -> showFormDialog());
        header.getChildren().addAll(titleBox, sp, btnExport);
        if (SessionManager.getInstance().canCreateOrders()) header.getChildren().add(btnNuevo);

        VBox filtrosCard = StyleHelper.card(12);
        filtrosCard.setPadding(new Insets(16));
        filtrosCard.setSpacing(12);

        HBox filtros = new HBox(12);
        filtros.setAlignment(Pos.CENTER_LEFT);

        TextField tfBuscar = StyleHelper.textField("Buscar por ID, cliente, teléfono o notas...");
        tfBuscar.setPrefWidth(260);
        tfBuscar.setPrefHeight(38);

        ComboBox<String> cbEstado = StyleHelper.<String>comboBox();
        cbEstado.setItems(FXCollections.observableArrayList(estadosConTodos()));
        cbEstado.setValue("Todos"); cbEstado.setPromptText("Estado");

        ComboBox<String> cbUrgencia = StyleHelper.<String>comboBox();
        cbUrgencia.setItems(FXCollections.observableArrayList("Todos","Normal","Urgente"));
        cbUrgencia.setValue("Todos"); cbUrgencia.setPromptText("Urgencia");

        DatePicker dpDesde = new DatePicker(); dpDesde.setPromptText("Fecha desde");
        DatePicker dpHasta = new DatePicker(); dpHasta.setPromptText("Fecha hasta");
        dpDesde.setStyle("-fx-font-size:13;"); dpHasta.setStyle("-fx-font-size:13;");

        Button btnFiltrar = StyleHelper.btnPrimary("Filtrar");
        Button btnLimpiar = StyleHelper.btnSecondary("Limpiar");

        filtros.getChildren().addAll(
            vbox("Búsqueda", tfBuscar),
            vbox("Estado", cbEstado),
            vbox("Urgencia", cbUrgencia),
            vbox("Desde", dpDesde),
            vbox("Hasta", dpHasta),
            btnFiltrar,
            btnLimpiar
        );
        filtrosCard.getChildren().add(filtros);

        tablaContainer = new VBox(0);
        VBox tablaCard = StyleHelper.card(12);
        tablaCard.setPadding(new Insets(20));
        tablaCard.getChildren().add(tablaContainer);

        renderTabla(pedidoDAO.findAll());

        btnFiltrar.setOnAction(e -> renderTabla(pedidoDAO.filter(
            cbEstado.getValue(), cbUrgencia.getValue(), dpDesde.getValue(), dpHasta.getValue(), tfBuscar.getText())));
        tfBuscar.setOnAction(e -> btnFiltrar.fire());
        btnLimpiar.setOnAction(e -> {
            tfBuscar.clear(); cbEstado.setValue("Todos"); cbUrgencia.setValue("Todos");
            dpDesde.setValue(null); dpHasta.setValue(null);
            renderTabla(pedidoDAO.findAll());
        });

        root.getChildren().addAll(header, filtrosCard, tablaCard);
        return root;
    }

    private void renderTabla(List<Pedido> pedidos) {
        ultimaLista = new ArrayList<>(pedidos);
        tablaContainer.getChildren().clear();

        GridPane cabecera = new GridPane();
        cabecera.setHgap(12); cabecera.setVgap(8);
        String[] colNames = {"ID","Cliente","Fecha","Estado","Urgencia","Reparto","Items","Total","Acciones"};
        double[] colWidths = {60, 170, 140, 110, 90, 70, 50, 90, 170};
        for (int i = 0; i < colNames.length; i++) {
            Label h = new Label(colNames[i]);
            h.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:11;-fx-font-weight:bold;");
            cabecera.add(h, i, 0);
            ColumnConstraints cc = new ColumnConstraints(); cc.setPrefWidth(colWidths[i]);
            cabecera.getColumnConstraints().add(cc);
        }
        tablaContainer.getChildren().add(cabecera);
        tablaContainer.getChildren().add(StyleHelper.separator());

        if (pedidos.isEmpty()) {
            Label empty = new Label("No se encontraron pedidos.");
            empty.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-padding:16;");
            tablaContainer.getChildren().add(empty);
            return;
        }

        for (Pedido p : pedidos) {
            GridPane fila = new GridPane();
            fila.setHgap(12); fila.setVgap(4);
            fila.setPadding(new Insets(10, 0, 10, 0));
            for (ColumnConstraints cc : cabecera.getColumnConstraints()) {
                ColumnConstraints c2 = new ColumnConstraints(); c2.setPrefWidth(cc.getPrefWidth());
                fila.getColumnConstraints().add(c2);
            }

            Label id      = bold(p.getIdFormateado());
            Label cliente = text(p.getClienteNombre() != null ? p.getClienteNombre() : "-");
            Label fecha   = gray(p.getFecha() != null ? p.getFecha().format(FMT) : "-");
            Label estado  = StyleHelper.badge(p.getEstado());

            Label urgencia;
            if ("Urgente".equals(p.getUrgencia())) {
                urgencia = new Label("URGENTE");
                urgencia.setStyle("-fx-background-color:#EF4444;-fx-text-fill:white;-fx-background-radius:20;-fx-padding:2 8;-fx-font-size:10;-fx-font-weight:bold;");
            } else {
                urgencia = gray("Normal");
            }

            Label reparto = text(p.isReparto() ? "Sí" : "No");
            Label items   = text(String.valueOf(p.getNumItems()));
            Label total   = new Label(String.format("%.2f€", p.getTotal()));
            total.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-weight:bold;-fx-font-size:13;");

            Button btnVer = new Button("Ver");
            btnVer.setStyle("-fx-background-color:" + StyleHelper.GREEN + "20;-fx-text-fill:" + StyleHelper.GREEN + ";-fx-background-radius:6;-fx-font-size:11;-fx-cursor:hand;-fx-padding:3 10;");
            btnVer.setOnAction(e -> showDetallePedido(p));
            HBox acciones = new HBox(6, btnVer);

            if (SessionManager.getInstance().canEditOrders()) {
                Button btnEdit = new Button("Editar");
                btnEdit.setStyle("-fx-background-color:#EEF2FF;-fx-text-fill:#4F46E5;-fx-background-radius:6;-fx-font-size:11;-fx-cursor:hand;-fx-padding:3 10;");
                btnEdit.setOnAction(e -> showEditPedido(p));
                acciones.getChildren().add(btnEdit);
            }

            if (SessionManager.getInstance().canDeleteOrders()) {
                Button btnDel = new Button("✕");
                btnDel.setStyle("-fx-background-color:#FEE2E2;-fx-text-fill:#EF4444;-fx-background-radius:6;-fx-font-size:11;-fx-cursor:hand;-fx-padding:3 8;");
                btnDel.setOnAction(e -> {
                    if (confirmar("¿Eliminar pedido " + p.getIdFormateado() + "?")) {
                        pedidoDAO.delete(p.getId());
                        renderTabla(pedidoDAO.findAll());
                    }
                });
                acciones.getChildren().add(btnDel);
            }

            fila.addRow(0, id, cliente, fecha, estado, urgencia, reparto, items, total, acciones);
            tablaContainer.getChildren().addAll(fila, StyleHelper.separator());
        }
    }

    private void showFormDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nuevo pedido");

        VBox form = new VBox(14);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color:white;");
        form.setPrefWidth(560);

        Label title = new Label("Nuevo pedido");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        List<Cliente> clientes = clienteDAO.findAll();
        ComboBox<String> cbCliente = StyleHelper.<String>comboBox();
        cbCliente.setItems(FXCollections.observableArrayList(clientes.stream().map(Cliente::getEmpresa).toList()));
        cbCliente.setPromptText("Selecciona cliente");
        cbCliente.setPrefWidth(500);

        ComboBox<String> cbUrgencia = StyleHelper.<String>comboBox();
        cbUrgencia.setItems(FXCollections.observableArrayList("Normal","Urgente"));
        cbUrgencia.setValue("Normal"); cbUrgencia.setPrefWidth(190);

        CheckBox chkReparto = new CheckBox("Requiere reparto");
        chkReparto.setSelected(true);
        chkReparto.setStyle("-fx-font-size:13;");

        List<Producto> productos = productoDAO.findAll();
        ComboBox<String> cbProducto = StyleHelper.<String>comboBox();
        cbProducto.setItems(FXCollections.observableArrayList(productos.stream()
            .map(p -> p.getSku() + " - " + p.getNombre() + " (" + String.format("%.2f€", p.getPrecio()) + " | stock " + p.getStock() + ")")
            .toList()));
        cbProducto.setPromptText("Selecciona producto");
        cbProducto.setPrefWidth(330);

        Spinner<Integer> spCantidad = new Spinner<>(1, 999, 1);
        spCantidad.setEditable(true);
        spCantidad.setPrefWidth(90);

        Button btnAdd = StyleHelper.btnPrimary("Añadir");
        HBox addRow = new HBox(10, cbProducto, spCantidad, btnAdd);
        addRow.setAlignment(Pos.CENTER_LEFT);

        VBox itemsBox = new VBox(6);
        itemsBox.setPadding(new Insets(10));
        itemsBox.setStyle("-fx-background-color:#F9FAFB;-fx-background-radius:8;-fx-border-color:" + StyleHelper.BORDER + ";-fx-border-radius:8;");

        TextField tfTotal = StyleHelper.textField("Total automático");
        tfTotal.setPrefWidth(190);
        tfTotal.setEditable(false);
        tfTotal.setText("0.00");

        TextArea taNotas = new TextArea();
        taNotas.setPromptText("Observaciones del pedido...");
        taNotas.setPrefRowCount(3);
        taNotas.setStyle("-fx-background-radius:8;-fx-border-radius:8;-fx-border-color:" + StyleHelper.BORDER + ";-fx-font-size:13;");

        List<PedidoItem> items = new ArrayList<>();
        renderLineas(itemsBox, items, tfTotal);

        btnAdd.setOnAction(e -> {
            int idx = cbProducto.getSelectionModel().getSelectedIndex();
            if (idx < 0) { alert("Selecciona un producto."); return; }
            Producto prod = productos.get(idx);
            int cantidad = spCantidad.getValue();
            if (cantidad <= 0) { alert("La cantidad debe ser mayor que cero."); return; }
            PedidoItem item = new PedidoItem();
            item.setProductoId(prod.getId());
            item.setProductoNombre(prod.getNombre());
            item.setCantidad(cantidad);
            item.setPrecioUnit(prod.getPrecio());
            items.add(item);
            renderLineas(itemsBox, items, tfTotal);
            cbProducto.getSelectionModel().clearSelection();
            spCantidad.getValueFactory().setValue(1);
        });

        HBox opcionesRow = new HBox(14, vbox("Urgencia", cbUrgencia), vbox("Total (€)", tfTotal), chkReparto);
        opcionesRow.setAlignment(Pos.CENTER_LEFT);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        Button btnGuardar = StyleHelper.btnPrimary("Guardar pedido");
        Button btnCancelar = StyleHelper.btnSecondary("Cancelar");
        botones.getChildren().addAll(btnCancelar, btnGuardar);

        btnCancelar.setOnAction(e -> dialog.close());
        btnGuardar.setOnAction(e -> {
            int idxCliente = cbCliente.getSelectionModel().getSelectedIndex();
            if (idxCliente < 0) { alert("Selecciona un cliente."); return; }
            if (items.isEmpty()) { alert("Añade al menos un producto al pedido."); return; }

            Pedido np = new Pedido();
            Cliente cliente = clientes.get(idxCliente);
            np.setClienteId(cliente.getId());
            np.setClienteNombre(cliente.getEmpresa());
            np.setEstado("Pendiente");
            np.setUrgencia(cbUrgencia.getValue());
            np.setReparto(chkReparto.isSelected());
            np.setNotas(taNotas.getText());
            np.setItems(new ArrayList<>(items));
            np.setTotal(calcularTotal(items));
            var user = SessionManager.getInstance().getUsuarioActual();
            np.setUsuarioId(user != null ? user.getId() : 1);

            if (pedidoDAO.save(np)) {
                renderTabla(pedidoDAO.findAll());
                dialog.close();
            } else {
                alert("No se pudo guardar el pedido. Revisa la conexión y los datos.");
            }
        });

        form.getChildren().addAll(title,
            vbox("Cliente", cbCliente),
            opcionesRow,
            vbox("Productos y cantidades", addRow),
            itemsBox,
            vbox("Observaciones", taNotas),
            botones);
        dialog.setScene(new Scene(form));
        dialog.showAndWait();
    }

    private void renderLineas(VBox itemsBox, List<PedidoItem> items, TextField tfTotal) {
        itemsBox.getChildren().clear();
        if (items.isEmpty()) {
            Label empty = gray("Todavía no hay productos añadidos.");
            itemsBox.getChildren().add(empty);
        } else {
            for (PedidoItem item : new ArrayList<>(items)) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                Label nombre = bold(item.getProductoNombre());
                Label qty = gray("x" + item.getCantidad());
                Label precio = text(String.format("%.2f€", item.getPrecioUnit()));
                Label subtotal = new Label(String.format("%.2f€", item.getSubtotal()));
                subtotal.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-weight:bold;");
                Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                Button del = new Button("✕");
                del.setStyle("-fx-background-color:#FEE2E2;-fx-text-fill:#EF4444;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:2 7;");
                del.setOnAction(e -> {
                    items.remove(item);
                    renderLineas(itemsBox, items, tfTotal);
                });
                row.getChildren().addAll(nombre, qty, precio, sp, subtotal, del);
                itemsBox.getChildren().add(row);
            }
        }
        tfTotal.setText(String.format("%.2f", calcularTotal(items)));
    }

    private double calcularTotal(List<PedidoItem> items) {
        return items.stream().mapToDouble(PedidoItem::getSubtotal).sum();
    }


    private void showEditPedido(Pedido pedido) {
        Pedido actual = pedidoDAO.findById(pedido.getId());
        if (actual == null) {
            alert("No se pudo cargar el pedido.");
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar pedido " + actual.getIdFormateado());

        VBox form = new VBox(14);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color:white;");
        form.setPrefWidth(520);

        Label title = new Label("Editar pedido " + actual.getIdFormateado());
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        List<Cliente> clientes = clienteDAO.findAll();
        ComboBox<String> cbCliente = StyleHelper.<String>comboBox();
        cbCliente.setItems(FXCollections.observableArrayList(clientes.stream().map(Cliente::getEmpresa).toList()));
        cbCliente.setPrefWidth(470);
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == actual.getClienteId()) {
                cbCliente.getSelectionModel().select(i);
                break;
            }
        }

        ComboBox<String> cbEstado = StyleHelper.<String>comboBox();
        cbEstado.setItems(FXCollections.observableArrayList(estadosPedido()));
        cbEstado.setValue(actual.getEstado());
        cbEstado.setPrefWidth(180);

        ComboBox<String> cbUrgencia = StyleHelper.<String>comboBox();
        cbUrgencia.setItems(FXCollections.observableArrayList("Normal", "Urgente"));
        cbUrgencia.setValue(actual.getUrgencia());
        cbUrgencia.setPrefWidth(180);

        CheckBox chkReparto = new CheckBox("Requiere reparto");
        chkReparto.setSelected(actual.isReparto());
        chkReparto.setStyle("-fx-font-size:13;");

        TextArea taNotas = new TextArea(actual.getNotas() != null ? actual.getNotas() : "");
        taNotas.setPromptText("Observaciones del pedido...");
        taNotas.setPrefRowCount(4);
        taNotas.setStyle("-fx-background-radius:8;-fx-border-radius:8;-fx-border-color:" + StyleHelper.BORDER + ";-fx-font-size:13;");

        HBox row1 = new HBox(12, vbox("Estado", cbEstado), vbox("Urgencia", cbUrgencia), chkReparto);
        row1.setAlignment(Pos.CENTER_LEFT);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        Button btnGuardar = StyleHelper.btnPrimary("Guardar cambios");
        Button btnCancelar = StyleHelper.btnSecondary("Cancelar");
        botones.getChildren().addAll(btnCancelar, btnGuardar);

        btnCancelar.setOnAction(e -> dialog.close());
        btnGuardar.setOnAction(e -> {
            int idxCliente = cbCliente.getSelectionModel().getSelectedIndex();
            if (idxCliente < 0) { alert("Selecciona un cliente."); return; }
            actual.setClienteId(clientes.get(idxCliente).getId());
            actual.setEstado(cbEstado.getValue());
            actual.setUrgencia(cbUrgencia.getValue());
            actual.setReparto(chkReparto.isSelected());
            actual.setNotas(taNotas.getText());

            if (pedidoDAO.updateCabecera(actual)) {
                renderTabla(pedidoDAO.findAll());
                dialog.close();
            } else {
                alert("No se pudieron guardar los cambios del pedido.");
            }
        });

        form.getChildren().addAll(title,
            vbox("Cliente", cbCliente),
            row1,
            vbox("Observaciones", taNotas),
            botones);
        dialog.setScene(new Scene(form));
        dialog.showAndWait();
    }

    private void showDetallePedido(Pedido p) {
        Stage d = new Stage();
        d.initModality(Modality.APPLICATION_MODAL);
        d.setTitle("Pedido " + p.getIdFormateado());
        VBox v = new VBox(12);
        v.setPadding(new Insets(24));
        v.setStyle("-fx-background-color:white;");
        v.setPrefWidth(460);
        v.getChildren().addAll(
            createTitle("Pedido " + p.getIdFormateado()),
            gray("Cliente: " + (p.getClienteNombre() != null ? p.getClienteNombre() : "-")),
            gray("Dirección: " + (p.getClienteDireccion() != null ? p.getClienteDireccion() : "-")),
            gray("Teléfono: " + (p.getClienteTelefono() != null ? p.getClienteTelefono() : "-")),
            gray("Estado: " + p.getEstado()),
            gray("Urgencia: " + p.getUrgencia()),
            gray("Reparto: " + (p.isReparto() ? "Sí" : "No")),
            gray("Notas: " + (p.getNotas() != null && !p.getNotas().isBlank() ? p.getNotas() : "-"))
        );

        VBox lineas = new VBox(6);
        lineas.setPadding(new Insets(8));
        lineas.setStyle("-fx-background-color:#F9FAFB;-fx-background-radius:8;");
        List<PedidoItem> items = pedidoDAO.findItemsByPedidoId(p.getId());
        if (items.isEmpty()) lineas.getChildren().add(gray("Sin líneas de pedido."));
        for (PedidoItem item : items) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label nombre = bold(item.getProductoNombre());
            Label cant = gray("x" + item.getCantidad());
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Label subtotal = new Label(String.format("%.2f€", item.getSubtotal()));
            subtotal.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-weight:bold;");
            row.getChildren().addAll(nombre, cant, sp, subtotal);
            lineas.getChildren().add(row);
        }
        v.getChildren().addAll(StyleHelper.separator(), bold("Productos"), lineas, bold("Total: " + String.format("%.2f€", p.getTotal())));

        SessionManager sm = SessionManager.getInstance();
        if (sm.isAdmin() || sm.isOficina() || sm.isRepartidor()) {
            ComboBox<String> cbEstado = StyleHelper.<String>comboBox();
            cbEstado.setItems(FXCollections.observableArrayList(sm.isRepartidor() ? estadosRepartidor() : estadosPedido()));
            cbEstado.setValue(p.getEstado()); cbEstado.setPrefWidth(280);
            Button btnCambiar = StyleHelper.btnPrimary("Cambiar estado");
            btnCambiar.setOnAction(e -> {
                pedidoDAO.updateEstado(p.getId(), cbEstado.getValue());
                d.close();
                renderTabla(pedidoDAO.findAll());
            });
            v.getChildren().addAll(StyleHelper.separator(), vbox("Cambiar estado", cbEstado), btnCambiar);
        }
        d.setScene(new Scene(v));
        d.showAndWait();
    }

    private void exportarCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Exportar pedidos");
        fc.setInitialFileName("pedidos_quimipapel.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fc.showSaveDialog(tablaContainer.getScene().getWindow());
        if (file == null) return;

        List<String> lines = new ArrayList<>();
        lines.add("id;cliente;fecha;estado;urgencia;reparto;items;total;notas");
        for (Pedido p : ultimaLista) {
            lines.add(String.join(";",
                String.valueOf(p.getId()),
                csv(p.getClienteNombre()),
                csv(p.getFecha() != null ? p.getFecha().format(FMT) : ""),
                csv(p.getEstado()),
                csv(p.getUrgencia()),
                p.isReparto() ? "Sí" : "No",
                String.valueOf(p.getNumItems()),
                String.format("%.2f", p.getTotal()).replace('.', ','),
                csv(p.getNotas())
            ));
        }
        try {
            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
            alert("Exportación realizada correctamente.");
        } catch (IOException ex) {
            alert("No se pudo exportar el CSV: " + ex.getMessage());
        }
    }

    private String csv(String v) {
        if (v == null) return "";
        return '"' + v.replace("\"", "\"\"") + '"';
    }

    private List<String> estadosPedido() {
        return List.of("Pendiente","Preparado","Cargado","En reparto","Entregado","Incidencia");
    }
    private List<String> estadosRepartidor() {
        return List.of("Cargado","En reparto","Entregado","Incidencia");
    }
    private List<String> estadosConTodos() {
        List<String> estados = new ArrayList<>();
        estados.add("Todos");
        estados.addAll(estadosPedido());
        return estados;
    }

    // helpers
    private VBox vbox(String label, javafx.scene.Node field) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size:12;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.TEXT_DARK + ";");
        return new VBox(4, l, field);
    }
    private Label createTitle(String t) { Label l = new Label(t); l.setFont(Font.font("System", FontWeight.BOLD, 26)); return l; }
    private Label createSub(String t)   { Label l = new Label(t); l.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;"); return l; }
    private Label bold(String t)  { Label l = new Label(t); l.setStyle("-fx-font-weight:bold;-fx-font-size:13;"); return l; }
    private Label text(String t)  { Label l = new Label(t); l.setStyle("-fx-font-size:13;"); return l; }
    private Label gray(String t)  { Label l = new Label(t); l.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:12;"); return l; }
    private boolean confirmar(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
