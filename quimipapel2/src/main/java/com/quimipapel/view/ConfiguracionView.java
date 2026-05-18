package com.quimipapel.view;

import com.quimipapel.dao.UsuarioDAO;
import com.quimipapel.model.Usuario;
import com.quimipapel.util.PasswordUtil;
import com.quimipapel.util.SessionManager;
import com.quimipapel.util.StyleHelper;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ConfiguracionView {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Region build() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + StyleHelper.BG_MAIN + ";");

        Label titleLbl = new Label("Configuración de Perfil");
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 26));
        Label subLbl = new Label("Administra tu información personal y preferencias");
        subLbl.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;");
        VBox header = new VBox(4, titleLbl, subLbl);

        Usuario user = SessionManager.getInstance().getUsuarioActual();
        String nombre    = user != null ? user.getNombre()   : "";
        String email     = user != null ? user.getEmail()    : "";
        String telefono  = user != null ? user.getTelefono() : "";
        String rol       = user != null ? user.getRol()      : "";
        String iniciales = user != null ? user.getIniciales(): "??";

        // Dos columnas
        HBox cols = new HBox(20);

        // ─── Columna izquierda ───────────────────────────────
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // Información personal
        VBox infoCard = StyleHelper.card(12);
        infoCard.setPadding(new Insets(20));
        infoCard.setSpacing(16);

        Label infoTitle = new Label("ℹ Información personal");
        infoTitle.setFont(Font.font("System", FontWeight.BOLD, 15));

        // Campos
        TextField tfNombre   = StyleHelper.textField("Nombre completo");
        tfNombre.setText(nombre); tfNombre.setPrefHeight(38);
        TextField tfEmail    = StyleHelper.textField("correo@empresa.com");
        tfEmail.setText(email); tfEmail.setPrefHeight(38);
        TextField tfTelefono = StyleHelper.textField("+34...");
        tfTelefono.setText(telefono); tfTelefono.setPrefHeight(38);
        TextField tfRol      = StyleHelper.textField("");
        tfRol.setText(rol); tfRol.setEditable(false);
        tfRol.setPrefHeight(38);
        tfRol.setStyle(tfRol.getStyle() + "-fx-background-color:#F3F4F6;");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(14);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(vbox("Nombre completo", tfNombre),   0, 0);
        grid.add(vbox("Correo electrónico", tfEmail), 1, 0);
        grid.add(vbox("Teléfono", tfTelefono),        0, 1);
        grid.add(vbox("Rol", tfRol),                  1, 1);

        infoCard.getChildren().addAll(infoTitle, StyleHelper.separator(), grid);

        // Cambiar contraseña
        VBox passCard = StyleHelper.card(12);
        passCard.setPadding(new Insets(20));
        passCard.setSpacing(14);

        Label passTitle = new Label("🔒 Cambiar contraseña");
        passTitle.setFont(Font.font("System", FontWeight.BOLD, 15));

        PasswordField pfActual   = passField("Contraseña actual");
        PasswordField pfNueva    = passField("Nueva contraseña");
        PasswordField pfConfirma = passField("Confirmar contraseña");

        Label passMsg = new Label("");
        passMsg.setStyle("-fx-font-size:12;");

        GridPane passGrid = new GridPane();
        passGrid.setHgap(12); passGrid.setVgap(14);
        passGrid.getColumnConstraints().addAll(col1, col2);
        passGrid.add(vbox("Contraseña actual", pfActual), 0, 0, 2, 1);
        passGrid.add(vbox("Nueva contraseña", pfNueva),   0, 1);
        passGrid.add(vbox("Confirmar contraseña", pfConfirma), 1, 1);

        passCard.getChildren().addAll(passTitle, StyleHelper.separator(), passGrid, passMsg);

        // Notificaciones
        VBox notifCard = StyleHelper.card(12);
        notifCard.setPadding(new Insets(20));
        notifCard.setSpacing(12);

        Label notifTitle = new Label("🔔 Notificaciones");
        notifTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        notifCard.getChildren().addAll(notifTitle, StyleHelper.separator());

        String[][] notifItems = {
            {"Nuevos pedidos",         "true"},
            {"Pedidos urgentes",       "true"},
            {"Incidencias",            "true"},
            {"Entregas completadas",   "false"},
            {"Actualizaciones del sistema", "false"}
        };
        for (String[] item : notifItems) {
            notifCard.getChildren().add(notifToggle(item[0], Boolean.parseBoolean(item[1])));
        }

        // Botones guardar / cancelar
        HBox botones = new HBox(12);
        botones.setAlignment(Pos.CENTER_LEFT);
        Button btnGuardar = StyleHelper.btnPrimary("💾 Guardar cambios");
        Button btnCancelar = StyleHelper.btnSecondary("Cancelar");

        Label saveMsg = new Label("");
        saveMsg.setStyle("-fx-font-size:12;");

        btnGuardar.setOnAction(e -> {
            if (user != null) {
                user.setNombre(tfNombre.getText());
                user.setEmail(tfEmail.getText());
                user.setTelefono(tfTelefono.getText());
                usuarioDAO.update(user);

                // Cambio de contraseña conectado a la base de datos con BCrypt
                if (!pfNueva.getText().isBlank()) {
                    if (pfActual.getText().isBlank() || !PasswordUtil.verify(pfActual.getText(), user.getPasswordHash())) {
                        passMsg.setText("⚠ La contraseña actual no es correcta.");
                        passMsg.setStyle("-fx-text-fill:#EF4444;-fx-font-size:12;");
                        return;
                    }
                    if (!pfNueva.getText().equals(pfConfirma.getText())) {
                        passMsg.setText("⚠ Las contraseñas no coinciden.");
                        passMsg.setStyle("-fx-text-fill:#EF4444;-fx-font-size:12;");
                        return;
                    }
                    String nuevoHash = PasswordUtil.hash(pfNueva.getText());
                    usuarioDAO.updatePassword(user.getId(), nuevoHash);
                    user.setPasswordHash(nuevoHash);
                    pfActual.clear(); pfNueva.clear(); pfConfirma.clear();
                    passMsg.setText("✓ Contraseña actualizada.");
                    passMsg.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-size:12;");
                }

                saveMsg.setText("✓ Cambios guardados correctamente.");
                saveMsg.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-size:12;");
            }
        });
        btnCancelar.setOnAction(e -> {
            tfNombre.setText(nombre);
            tfEmail.setText(email);
            tfTelefono.setText(telefono);
            saveMsg.setText("");
        });

        botones.getChildren().addAll(btnGuardar, btnCancelar, saveMsg);
        leftCol.getChildren().addAll(infoCard, passCard, notifCard, botones);

        // ─── Columna derecha: avatar + actividad + cerrar sesión ─
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(260);
        rightCol.setMinWidth(240);

        VBox avatarCard = StyleHelper.card(12);
        avatarCard.setPadding(new Insets(24));
        avatarCard.setSpacing(12);
        avatarCard.setAlignment(Pos.CENTER);

        StackPane avCircle = StyleHelper.avatar(iniciales, StyleHelper.GREEN, 72);
        Label avNombre = new Label(nombre);
        avNombre.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label avRol = new Label(rol);
        avRol.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;");
        Button btnFoto = StyleHelper.btnSecondary("Cambiar foto");
        btnFoto.setPrefWidth(160);
        avatarCard.getChildren().addAll(avCircle, avNombre, avRol, btnFoto);

        VBox actCard = StyleHelper.card(12);
        actCard.setPadding(new Insets(20));
        actCard.setSpacing(10);
        Label actTitle = new Label("🛡 Actividad reciente");
        actTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        actCard.getChildren().addAll(actTitle, StyleHelper.separator());

        String[][] actividades = {
            {"Inicio de sesión",    "Hoy a las 10:30"},
            {"Pedido #1250 creado", "Hoy a las 09:15"},
            {"Perfil actualizado",  "Ayer a las 16:45"}
        };
        for (String[] act : actividades) {
            VBox actItem = new VBox(2);
            Label aTitle = new Label(act[0]);
            aTitle.setStyle("-fx-font-size:12;-fx-font-weight:bold;");
            Label aTime  = new Label(act[1]);
            aTime.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:11;");
            actItem.getChildren().addAll(aTitle, aTime);
            actCard.getChildren().add(actItem);
        }

        // Cerrar sesión
        Button btnLogout = new Button("⏏ Cerrar sesión");
        btnLogout.setPrefWidth(Double.MAX_VALUE);
        btnLogout.setPrefHeight(42);
        btnLogout.setStyle("-fx-background-color:#EF4444;-fx-text-fill:white;-fx-background-radius:8;" +
                "-fx-font-weight:bold;-fx-font-size:13;-fx-cursor:hand;");
        btnLogout.setOnAction(e -> {
            SessionManager.getInstance().cerrarSesion();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            new LoginView(stage).show();
        });

        rightCol.getChildren().addAll(avatarCard, actCard, btnLogout);

        cols.getChildren().addAll(leftCol, rightCol);
        root.getChildren().addAll(header, cols);
        return root;
    }

    // ─── Helpers ────────────────────────────────────────────

    private HBox notifToggle(String label, boolean on) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-background-color:#F9FAFB;-fx-background-radius:8;");

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:13;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        // Toggle switch simulado
        final boolean[] estado = {on};
        StackPane toggle = buildToggle(on);
        toggle.setOnMouseClicked(e -> {
            estado[0] = !estado[0];
            toggle.getChildren().clear();
            toggle.getChildren().addAll(buildToggle(estado[0]).getChildren());
            toggle.setStyle(buildToggle(estado[0]).getStyle());
        });

        row.getChildren().addAll(lbl, sp, buildToggle(on));
        return row;
    }

    private StackPane buildToggle(boolean on) {
        StackPane track = new StackPane();
        track.setPrefSize(44, 24);
        track.setStyle(String.format(
            "-fx-background-color:%s;-fx-background-radius:12;", on ? StyleHelper.GREEN : "#D1D5DB"));
        javafx.scene.shape.Circle thumb = new javafx.scene.shape.Circle(10,
                javafx.scene.paint.Color.WHITE);
        StackPane.setAlignment(thumb, on ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        StackPane.setMargin(thumb, new Insets(0, on ? 2 : 0, 0, on ? 0 : 2));
        track.getChildren().add(thumb);
        track.setCursor(javafx.scene.Cursor.HAND);
        return track;
    }

    private VBox vbox(String lbl, javafx.scene.Node field) {
        Label l = new Label(lbl);
        l.setStyle("-fx-font-size:12;-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";");
        return new VBox(5, l, field);
    }

    private PasswordField passField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setPrefHeight(38);
        pf.setStyle("-fx-background-radius:8;-fx-border-radius:8;-fx-border-color:" +
                StyleHelper.BORDER + ";-fx-padding:0 12;-fx-font-size:13;");
        return pf;
    }
}
