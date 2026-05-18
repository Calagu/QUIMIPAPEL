package com.quimipapel.view;

import com.quimipapel.dao.UsuarioDAO;
import com.quimipapel.model.Usuario;
import com.quimipapel.util.PasswordUtil;
import com.quimipapel.util.StyleHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UsuariosView {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private VBox tablaContainer;

    public Region build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + StyleHelper.BG_MAIN + ";");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(4,
            title("Gestión de Usuarios"),
            sub("Administra usuarios y permisos del sistema"));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnNuevo = StyleHelper.btnPrimary("+ Nuevo usuario");
        btnNuevo.setOnAction(e -> showForm(null));
        header.getChildren().addAll(titleBox, sp, btnNuevo);

        // KPIs
        int total    = usuarioDAO.countTotal();
        int admins   = usuarioDAO.countAdmins();
        int activos  = usuarioDAO.countActivos();
        int inactivos= usuarioDAO.countInactivos();

        HBox kpis = new HBox(16,
            kpiCard("👤", "Usuarios totales",   total,     StyleHelper.GREEN),
            kpiCard("🛡",  "Administradores",    admins,    StyleHelper.COLOR_INCIDENCIA),
            kpiCard("👥", "Usuarios activos",   activos,   "#3B82F6"),
            kpiCard("👤", "Usuarios inactivos", inactivos, StyleHelper.TEXT_GRAY)
        );
        kpis.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        // Filtros
        VBox filtrosCard = StyleHelper.card(12);
        filtrosCard.setPadding(new Insets(16));
        HBox filtros = new HBox(12);
        filtros.setAlignment(Pos.CENTER_LEFT);
        TextField tfSearch = StyleHelper.textField("Buscar por nombre o correo...");
        tfSearch.setPrefWidth(350); tfSearch.setPrefHeight(38);
        ComboBox<String> cbRol = StyleHelper.<String>comboBox();
        cbRol.setItems(FXCollections.observableArrayList("Todos los roles","Administrador","Comercial","Repartidor","Oficina"));
        cbRol.setValue("Todos los roles");

        tfSearch.setOnKeyReleased(e -> filterTable(tfSearch.getText(), cbRol.getValue()));
        cbRol.setOnAction(e -> filterTable(tfSearch.getText(), cbRol.getValue()));

        filtros.getChildren().addAll(tfSearch, cbRol);
        filtrosCard.getChildren().add(filtros);

        // Tabla
        tablaContainer = new VBox(0);
        VBox tablaCard = StyleHelper.card(12);
        tablaCard.setPadding(new Insets(20));
        tablaCard.getChildren().add(tablaContainer);

        renderTabla(usuarioDAO.findAll());

        root.getChildren().addAll(header, kpis, filtrosCard, tablaCard);
        return root;
    }

    private void filterTable(String q, String rol) {
        List<Usuario> lista = usuarioDAO.findAll();
        if (q != null && !q.isBlank()) {
            String qLower = q.toLowerCase();
            lista = lista.stream()
                .filter(u -> u.getNombre().toLowerCase().contains(qLower) ||
                             u.getEmail().toLowerCase().contains(qLower))
                .toList();
        }
        if (rol != null && !rol.equals("Todos los roles")) {
            String rolF = rol;
            lista = lista.stream().filter(u -> rolF.equals(u.getRol())).toList();
        }
        renderTabla(lista);
    }

    private void renderTabla(List<Usuario> usuarios) {
        tablaContainer.getChildren().clear();

        GridPane cab = new GridPane(); cab.setHgap(12);
        String[] colNames = {"Usuario","Email","Rol","Permisos","Último acceso","Estado","Acciones"};
        double[] widths   = {150, 200, 110, 130, 140, 70, 80};
        for (int i = 0; i < colNames.length; i++) {
            Label h = new Label(colNames[i]);
            h.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:11;-fx-font-weight:bold;");
            cab.add(h, i, 0);
            ColumnConstraints cc = new ColumnConstraints(); cc.setPrefWidth(widths[i]);
            cab.getColumnConstraints().add(cc);
        }
        tablaContainer.getChildren().addAll(cab, StyleHelper.separator());

        for (Usuario u : usuarios) {
            GridPane fila = new GridPane(); fila.setHgap(12); fila.setVgap(4);
            fila.setPadding(new Insets(10, 0, 10, 0));
            for (ColumnConstraints cc : cab.getColumnConstraints()) {
                ColumnConstraints c2 = new ColumnConstraints(); c2.setPrefWidth(cc.getPrefWidth());
                fila.getColumnConstraints().add(c2);
            }

            HBox userBox = new HBox(8, StyleHelper.avatar(u.getIniciales(), StyleHelper.GREEN, 32), new Label(u.getNombre()) {{
                setStyle("-fx-font-weight:bold;-fx-font-size:13;");
            }});
            userBox.setAlignment(Pos.CENTER_LEFT);

            Label email = new Label(u.getEmail()); email.setStyle("-fx-font-size:12;");
            Label rol = rolBadge(u.getRol());
            Label permisos = permisosLabel(u.getRol());
            Label acceso = new Label(u.getUltimoAcceso() != null ? u.getUltimoAcceso().format(FMT) : "—");
            acceso.setStyle("-fx-font-size:12;-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";");
            Label estado = new Label("• " + (u.isActivo() ? "Activo" : "Inactivo"));
            estado.setStyle("-fx-text-fill:" + (u.isActivo() ? StyleHelper.GREEN : StyleHelper.TEXT_GRAY) + ";-fx-font-size:12;");

            Button btnEdit = new Button("✏");
            btnEdit.setStyle("-fx-background-color:#EEF2FF;-fx-text-fill:#4F46E5;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:3 8;");
            btnEdit.setOnAction(ev -> showForm(u));
            Button btnDel = new Button("✕");
            btnDel.setStyle("-fx-background-color:#FEE2E2;-fx-text-fill:#EF4444;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:3 8;");
            btnDel.setOnAction(ev -> {
                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Desactivar usuario?", ButtonType.YES, ButtonType.NO);
                if (a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    usuarioDAO.delete(u.getId());
                    renderTabla(usuarioDAO.findAll());
                }
            });
            HBox acc = new HBox(6, btnEdit, btnDel);

            fila.addRow(0, userBox, email, rol, permisos, acceso, estado, acc);
            tablaContainer.getChildren().addAll(fila, StyleHelper.separator());
        }
    }

    private Label rolBadge(String rol) {
        String color = switch (rol) {
            case "Administrador" -> "#EF4444";
            case "Comercial"     -> "#3B82F6";
            case "Repartidor"    -> "#F59E0B";
            case "Oficina"       -> StyleHelper.GREEN;
            default              -> StyleHelper.TEXT_GRAY;
        };
        Label l = new Label(rol);
        l.setStyle(String.format("-fx-background-color:%s;-fx-text-fill:white;-fx-background-radius:20;-fx-padding:2 10;-fx-font-size:11;-fx-font-weight:bold;", color));
        return l;
    }

    private Label permisosLabel(String rol) {
        String perms = switch (rol) {
            case "Administrador" -> "Todos los permisos";
            case "Comercial"     -> "Pedidos / Clientes";
            case "Repartidor"    -> "Reparto";
            case "Oficina"       -> "Pedidos / Productos";
            default              -> "-";
        };
        Label l = new Label(perms);
        l.setStyle("-fx-background-color:#F3F4F6;-fx-background-radius:6;-fx-padding:2 8;-fx-font-size:11;");
        return l;
    }

    private void showForm(Usuario usuario) {
        Stage d = new Stage(); d.initModality(Modality.APPLICATION_MODAL);
        d.setTitle(usuario == null ? "Nuevo usuario" : "Editar usuario");

        VBox form = new VBox(14); form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color:white;"); form.setPrefWidth(420);

        Label t = new Label(usuario == null ? "Nuevo usuario" : "Editar usuario");
        t.setFont(Font.font("System", FontWeight.BOLD, 20));

        TextField tfNombre = StyleHelper.textField("Nombre completo"); tfNombre.setPrefWidth(370);
        TextField tfEmail  = StyleHelper.textField("email@quimipapel.com"); tfEmail.setPrefWidth(370);
        TextField tfTel    = StyleHelper.textField("+34..."); tfTel.setPrefWidth(370);

        ComboBox<String> cbRol = StyleHelper.<String>comboBox();
        cbRol.setItems(FXCollections.observableArrayList("Administrador","Comercial","Repartidor","Oficina"));
        cbRol.setPrefWidth(370);

        PasswordField pfPass = new PasswordField(); pfPass.setPromptText(usuario == null ? "Contraseña obligatoria" : "Nueva contraseña opcional");
        pfPass.setStyle("-fx-background-radius:8;-fx-border-radius:8;-fx-border-color:" + StyleHelper.BORDER + ";-fx-padding:8 12;-fx-font-size:13;");
        pfPass.setPrefWidth(370);

        if (usuario != null) {
            tfNombre.setText(usuario.getNombre());
            tfEmail.setText(usuario.getEmail());
            tfTel.setText(usuario.getTelefono());
            cbRol.setValue(usuario.getRol());
        }

        HBox btns = new HBox(10); btns.setAlignment(Pos.CENTER_RIGHT);
        Button btnG = StyleHelper.btnPrimary("Guardar");
        Button btnC = StyleHelper.btnSecondary("Cancelar");
        btns.getChildren().addAll(btnC, btnG);
        btnC.setOnAction(e -> d.close());
        btnG.setOnAction(e -> {
            Usuario nu = usuario != null ? usuario : new Usuario();
            nu.setNombre(tfNombre.getText());
            nu.setEmail(tfEmail.getText());
            nu.setTelefono(tfTel.getText());
            nu.setRol(cbRol.getValue());
            nu.setActivo(true);
            if (usuario == null && pfPass.getText().isBlank()) {
                new Alert(Alert.AlertType.WARNING, "La contraseña es obligatoria para crear un usuario.", ButtonType.OK).showAndWait();
                return;
            }
            if (!pfPass.getText().isBlank()) {
                nu.setPasswordHash(PasswordUtil.hash(pfPass.getText()));
            }
            if (usuario == null) usuarioDAO.save(nu); else {
                usuarioDAO.update(nu);
                if (!pfPass.getText().isBlank()) usuarioDAO.updatePassword(nu.getId(), nu.getPasswordHash());
            }
            renderTabla(usuarioDAO.findAll());
            d.close();
        });

        form.getChildren().addAll(t,
            vbox("Nombre", tfNombre),
            vbox("Email", tfEmail),
            vbox("Teléfono", tfTel),
            vbox("Rol", cbRol),
            vbox("Contraseña", pfPass),
            btns);
        d.setScene(new Scene(form));
        d.showAndWait();
    }

    private VBox kpiCard(String icon, String label, int valor, String color) {
        VBox card = StyleHelper.card(12); card.setPadding(new Insets(20)); card.setSpacing(8);
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

    private VBox vbox(String lbl, javafx.scene.Node field) {
        Label l = new Label(lbl); l.setStyle("-fx-font-size:12;-fx-font-weight:bold;");
        return new VBox(4, l, field);
    }
    private Label title(String t) { Label l = new Label(t); l.setFont(Font.font("System", FontWeight.BOLD, 26)); return l; }
    private Label sub(String t)   { Label l = new Label(t); l.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;"); return l; }
}
