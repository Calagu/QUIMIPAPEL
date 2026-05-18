package com.quimipapel.view;

import com.quimipapel.dao.ClienteDAO;
import com.quimipapel.model.Cliente;
import com.quimipapel.util.SessionManager;
import com.quimipapel.util.StyleHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ClientesView {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private VBox tablaContainer;
    private TextField searchField;

    public Region build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + StyleHelper.BG_MAIN + ";");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(4,
            title("Gestión de Clientes"),
            sub("Administra la información de tus clientes"));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnNuevo = new Button("+ Nuevo cliente");
        btnNuevo.setStyle("-fx-background-color:#3B82F6;-fx-text-fill:white;-fx-background-radius:8;-fx-font-weight:bold;-fx-font-size:13;-fx-padding:8 18;-fx-cursor:hand;");
        btnNuevo.setOnAction(e -> showForm(null));
        header.getChildren().addAll(titleBox, sp);
        if (SessionManager.getInstance().canManageClients()) header.getChildren().add(btnNuevo);

        // Buscador
        VBox searchCard = StyleHelper.card(12);
        searchCard.setPadding(new Insets(16));
        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchField = StyleHelper.textField("Buscar por nombre, empresa, correo o teléfono...");
        searchField.setPrefWidth(500);
        searchField.setPrefHeight(38);
        Button btnBuscar = StyleHelper.btnPrimary("Filtros");
        searchField.setOnKeyReleased(e -> renderTabla(clienteDAO.search(searchField.getText())));
        searchRow.getChildren().addAll(searchField, btnBuscar);
        searchCard.getChildren().add(searchRow);

        // Tabla
        tablaContainer = new VBox(0);
        VBox tablaCard = StyleHelper.card(12);
        tablaCard.setPadding(new Insets(20));
        tablaCard.getChildren().add(tablaContainer);

        renderTabla(clienteDAO.findAll());

        root.getChildren().addAll(header, searchCard, tablaCard);
        return root;
    }

    private void renderTabla(List<Cliente> clientes) {
        tablaContainer.getChildren().clear();

        // Cabecera
        GridPane cab = cabecera();
        tablaContainer.getChildren().addAll(cab, StyleHelper.separator());

        if (clientes.isEmpty()) {
            Label e = new Label("No se encontraron clientes.");
            e.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-padding:16;");
            tablaContainer.getChildren().add(e);
            return;
        }

        for (Cliente c : clientes) {
            GridPane fila = new GridPane();
            fila.setHgap(12); fila.setVgap(4);
            fila.setPadding(new Insets(12, 0, 12, 0));
            copyColConstraints(cab, fila);

            Label empresa  = bold(c.getEmpresa());
            Label contacto = text(c.getContacto() != null ? c.getContacto() : "-");
            Label telefono = text(c.getTelefono() != null ? c.getTelefono() : "-");
            Label email    = text(c.getEmail() != null ? c.getEmail() : "-");
            Label direccion= gray(c.getDireccionCompleta());
            Label pedidos  = new Label(String.valueOf(c.getTotalPedidos()));
            pedidos.setStyle("-fx-background-color:#3B82F6;-fx-text-fill:white;-fx-background-radius:20;-fx-padding:2 8;-fx-font-weight:bold;");
            Label total    = new Label(String.format("%.2f€", c.getTotalFacturado()));
            total.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-weight:bold;");

            HBox acc = new HBox(6);
            if (SessionManager.getInstance().canManageClients()) {
                Button btnEdit = new Button("✏");
                btnEdit.setStyle("-fx-background-color:#EEF2FF;-fx-text-fill:#4F46E5;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:3 8;");
                btnEdit.setOnAction(ev -> showForm(c));
                Button btnDel = new Button("✕");
                btnDel.setStyle("-fx-background-color:#FEE2E2;-fx-text-fill:#EF4444;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:3 8;");
                btnDel.setOnAction(ev -> {
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar cliente?", ButtonType.YES, ButtonType.NO);
                    if (a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                        clienteDAO.delete(c.getId());
                        renderTabla(clienteDAO.findAll());
                    }
                });
                acc.getChildren().addAll(btnEdit, btnDel);
            } else {
                acc.getChildren().add(gray("Solo consulta"));
            }

            fila.addRow(0, empresa, contacto, telefono, email, direccion, pedidos, total, acc);
            tablaContainer.getChildren().addAll(fila, StyleHelper.separator());
        }
    }

    private GridPane cabecera() {
        GridPane g = new GridPane();
        g.setHgap(12);
        String[] cols = {"Empresa","Contacto","Teléfono","Email","Dirección","Pedidos","Total","Acciones"};
        double[] w    = {140, 100, 110, 160, 170, 70, 100, 80};
        for (int i = 0; i < cols.length; i++) {
            Label h = new Label(cols[i]);
            h.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:11;-fx-font-weight:bold;");
            g.add(h, i, 0);
            ColumnConstraints cc = new ColumnConstraints(); cc.setPrefWidth(w[i]);
            g.getColumnConstraints().add(cc);
        }
        return g;
    }

    private void copyColConstraints(GridPane src, GridPane dst) {
        for (ColumnConstraints cc : src.getColumnConstraints()) {
            ColumnConstraints c2 = new ColumnConstraints(); c2.setPrefWidth(cc.getPrefWidth());
            dst.getColumnConstraints().add(c2);
        }
    }

    private void showForm(Cliente cliente) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(cliente == null ? "Nuevo cliente" : "Editar cliente");

        VBox form = new VBox(14);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color:white;");
        form.setPrefWidth(440);

        Label t = new Label(cliente == null ? "Nuevo cliente" : "Editar cliente");
        t.setFont(Font.font("System", FontWeight.BOLD, 20));

        TextField tfEmpresa  = StyleHelper.textField("Nombre empresa"); tfEmpresa.setPrefWidth(390);
        TextField tfContacto = StyleHelper.textField("Nombre contacto"); tfContacto.setPrefWidth(390);
        TextField tfTel      = StyleHelper.textField("+34 ..."); tfTel.setPrefWidth(390);
        TextField tfEmail    = StyleHelper.textField("correo@empresa.com"); tfEmail.setPrefWidth(390);
        TextField tfDir      = StyleHelper.textField("Calle, número"); tfDir.setPrefWidth(390);
        TextField tfCiudad   = StyleHelper.textField("Ciudad"); tfCiudad.setPrefWidth(190);
        TextField tfCP       = StyleHelper.textField("C.P."); tfCP.setPrefWidth(190);

        if (cliente != null) {
            tfEmpresa.setText(cliente.getEmpresa());
            tfContacto.setText(cliente.getContacto());
            tfTel.setText(cliente.getTelefono());
            tfEmail.setText(cliente.getEmail());
            tfDir.setText(cliente.getDireccion());
            tfCiudad.setText(cliente.getCiudad());
            tfCP.setText(cliente.getCodigoPostal());
        }

        HBox ciudadRow = new HBox(10, vbox("Ciudad", tfCiudad), vbox("C.P.", tfCP));

        HBox btns = new HBox(10); btns.setAlignment(Pos.CENTER_RIGHT);
        Button btnG = StyleHelper.btnPrimary("Guardar");
        Button btnC = StyleHelper.btnSecondary("Cancelar");
        btns.getChildren().addAll(btnC, btnG);
        btnC.setOnAction(e -> dialog.close());
        btnG.setOnAction(e -> {
            Cliente nc = cliente != null ? cliente : new Cliente();
            nc.setEmpresa(tfEmpresa.getText());
            nc.setContacto(tfContacto.getText());
            nc.setTelefono(tfTel.getText());
            nc.setEmail(tfEmail.getText());
            nc.setDireccion(tfDir.getText());
            nc.setCiudad(tfCiudad.getText());
            nc.setCodigoPostal(tfCP.getText());
            nc.setActivo(true);
            if (cliente == null) clienteDAO.save(nc); else clienteDAO.update(nc);
            renderTabla(clienteDAO.findAll());
            dialog.close();
        });

        form.getChildren().addAll(t,
            vbox("Empresa", tfEmpresa),
            vbox("Contacto", tfContacto),
            vbox("Teléfono", tfTel),
            vbox("Email", tfEmail),
            vbox("Dirección", tfDir),
            ciudadRow, btns);
        dialog.setScene(new Scene(form));
        dialog.showAndWait();
    }

    private VBox vbox(String lbl, javafx.scene.Node field) {
        Label l = new Label(lbl);
        l.setStyle("-fx-font-size:12;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.TEXT_DARK + ";");
        return new VBox(4, l, field);
    }
    private Label title(String t) { Label l = new Label(t); l.setFont(Font.font("System", FontWeight.BOLD, 26)); return l; }
    private Label sub(String t)   { Label l = new Label(t); l.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;"); return l; }
    private Label bold(String t)  { Label l = new Label(t); l.setStyle("-fx-font-weight:bold;-fx-font-size:13;"); return l; }
    private Label text(String t)  { return new Label(t); }
    private Label gray(String t)  { Label l = new Label(t); l.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:12;"); return l; }
}
