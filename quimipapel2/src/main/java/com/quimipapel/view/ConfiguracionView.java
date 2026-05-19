package com.quimipapel.view;

import com.quimipapel.dao.NotificationDAO;
import com.quimipapel.dao.UsuarioDAO;
import com.quimipapel.model.Usuario;
import com.quimipapel.util.PasswordUtil;
import com.quimipapel.util.SessionManager;
import com.quimipapel.util.StyleHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class ConfiguracionView {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

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

        HBox cols = new HBox(20);

        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        VBox infoCard = StyleHelper.card(12);
        infoCard.setPadding(new Insets(20));
        infoCard.setSpacing(16);

        Label infoTitle = new Label("ℹ Información personal");
        infoTitle.setFont(Font.font("System", FontWeight.BOLD, 15));

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

        VBox notifCard = StyleHelper.card(12);
        notifCard.setPadding(new Insets(20));
        notifCard.setSpacing(12);

        Label notifTitle = new Label("🔔 Notificaciones");
        notifTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        Label notifMsg = new Label("Activa o desactiva tus avisos. Se guarda al instante.");
        notifMsg.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:12;");
        notifCard.getChildren().addAll(notifTitle, notifMsg, StyleHelper.separator());

        Map<String, Boolean> prefs = user != null ? notificationDAO.findByUsuarioId(user.getId()) : Map.of();
        notifCard.getChildren().addAll(
            notifCheck("Nuevos pedidos", "nuevos_pedidos", prefs.getOrDefault("nuevos_pedidos", true), user),
            notifCheck("Pedidos urgentes", "pedidos_urgentes", prefs.getOrDefault("pedidos_urgentes", true), user),
            notifCheck("Incidencias", "incidencias", prefs.getOrDefault("incidencias", true), user),
            notifCheck("Entregas completadas", "entregas_completadas", prefs.getOrDefault("entregas_completadas", false), user),
            notifCheck("Actualizaciones del sistema", "actualizaciones_sistema", prefs.getOrDefault("actualizaciones_sistema", false), user)
        );

        HBox botones = new HBox(12);
        botones.setAlignment(Pos.CENTER_LEFT);
        Button btnGuardar = StyleHelper.btnPrimary("💾 Guardar cambios");
        Button btnCancelar = StyleHelper.btnSecondary("Cancelar");

        Label saveMsg = new Label("");
        saveMsg.setStyle("-fx-font-size:12;");

        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(260);
        rightCol.setMinWidth(240);

        VBox avatarCard = StyleHelper.card(12);
        avatarCard.setPadding(new Insets(24));
        avatarCard.setSpacing(12);
        avatarCard.setAlignment(Pos.CENTER);

        StackPane[] avCircleRef = new StackPane[] { buildAvatar(user, 72) };
        Label avNombre = new Label(nombre);
        avNombre.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label avRol = new Label(rol);
        avRol.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;");
        Button btnFoto = StyleHelper.btnSecondary("Cambiar foto");
        btnFoto.setPrefWidth(160);
        avatarCard.getChildren().addAll(avCircleRef[0], avNombre, avRol, btnFoto);

        btnFoto.setOnAction(e -> {
            if (user == null) return;
            FileChooser fc = new FileChooser();
            fc.setTitle("Seleccionar foto de perfil");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fc.showOpenDialog(btnFoto.getScene().getWindow());
            if (file == null) return;
            try {
                String rutaGuardada = guardarFotoPerfil(user.getId(), file);
                if (usuarioDAO.updateFotoPerfil(user.getId(), rutaGuardada)) {
                    user.setFotoPerfilPath(rutaGuardada);
                    StackPane nuevoAvatar = buildAvatar(user, 72);
                    avatarCard.getChildren().set(0, nuevoAvatar);
                    avCircleRef[0] = nuevoAvatar;
                    SessionManager.getInstance().notifySessionChanged();
                    saveMsg.setText("✓ Foto guardada correctamente.");
                    saveMsg.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-size:12;");
                } else {
                    saveMsg.setText("⚠ No se pudo guardar la foto en la base de datos.");
                    saveMsg.setStyle("-fx-text-fill:#EF4444;-fx-font-size:12;");
                }
            } catch (IOException ex) {
                saveMsg.setText("⚠ No se pudo copiar la foto: " + ex.getMessage());
                saveMsg.setStyle("-fx-text-fill:#EF4444;-fx-font-size:12;");
            }
        });

        btnGuardar.setOnAction(e -> {
            if (user != null) {
                user.setNombre(tfNombre.getText());
                user.setEmail(tfEmail.getText());
                user.setTelefono(tfTelefono.getText());
                if (!usuarioDAO.update(user)) {
                    saveMsg.setText("⚠ No se pudieron guardar los datos.");
                    saveMsg.setStyle("-fx-text-fill:#EF4444;-fx-font-size:12;");
                    return;
                }

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

                avNombre.setText(user.getNombre());
                avatarCard.getChildren().set(0, buildAvatar(user, 72));
                SessionManager.getInstance().notifySessionChanged();
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

        VBox actCard = StyleHelper.card(12);
        actCard.setPadding(new Insets(20));
        actCard.setSpacing(10);
        Label actTitle = new Label("🛡 Actividad reciente");
        actTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        actCard.getChildren().addAll(actTitle, StyleHelper.separator());

        String[][] actividades = {
            {"Inicio de sesión",    "Hoy"},
            {"Perfil disponible",   "Ahora"},
            {"Preferencias",        "Configurables"}
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

        Button btnLogout = new Button("Cerrar sesión");
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

    private CheckBox notifCheck(String label, String key, boolean selected, Usuario user) {
        CheckBox cb = new CheckBox(label);
        cb.setSelected(selected);
        cb.setStyle("-fx-font-size:13;-fx-padding:8 10;-fx-background-color:#F9FAFB;-fx-background-radius:8;");
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (user != null) notificationDAO.updatePreference(user.getId(), key, newVal);
        });
        return cb;
    }

    private String guardarFotoPerfil(int userId, File origen) throws IOException {
        String nombreOriginal = origen.getName();
        String extension = ".png";
        int punto = nombreOriginal.lastIndexOf('.');
        if (punto >= 0 && punto < nombreOriginal.length() - 1) {
            extension = nombreOriginal.substring(punto).toLowerCase();
        }

        Path carpeta = Path.of(System.getProperty("user.home"), ".quimipapel", "profile_photos");
        Files.createDirectories(carpeta);

        Path destino = carpeta.resolve("usuario_" + userId + extension);
        Files.copy(origen.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return destino.toUri().toString();
    }

    private StackPane buildAvatar(Usuario user, double size) {
        if (user != null && user.getFotoPerfilPath() != null && !user.getFotoPerfilPath().isBlank()) {
            try {
                ImageView img = new ImageView(new Image(user.getFotoPerfilPath(), size, size, true, true));
                img.setFitWidth(size);
                img.setFitHeight(size);
                img.setClip(new Circle(size / 2, size / 2, size / 2));
                StackPane sp = new StackPane(img);
                sp.setMinSize(size, size);
                sp.setMaxSize(size, size);
                return sp;
            } catch (Exception ignored) {}
        }
        return StyleHelper.avatar(user != null ? user.getIniciales() : "??", StyleHelper.GREEN, size);
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
