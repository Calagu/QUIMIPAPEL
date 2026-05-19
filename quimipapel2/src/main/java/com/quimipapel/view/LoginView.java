package com.quimipapel.view;

import com.quimipapel.dao.UsuarioDAO;
import com.quimipapel.model.Usuario;
import com.quimipapel.util.SessionManager;
import com.quimipapel.util.StyleHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginView {

    private final Stage stage;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginView(Stage stage) { this.stage = stage; }

    public void show() {

        /* ── Panel izquierdo con imagen corporativa ── */
        VBox left = new VBox(20);
        left.setAlignment(Pos.CENTER);
        left.setPadding(new Insets(30, 24, 30, 24));
        left.setStyle("-fx-background-color:#FFFFFF;");
        left.setMinWidth(360);
        left.setPrefWidth(480);
        left.setMaxWidth(560);

        ImageView loginBanner = buildLoginBanner();
        left.getChildren().add(loginBanner);

        /* ── Panel derecho blanco ── */
        VBox right = new VBox();
        right.setAlignment(Pos.CENTER);
        right.setStyle("-fx-background-color:#FFFFFF;");
        HBox.setHgrow(right, Priority.ALWAYS);

        VBox form = new VBox(14);
        form.setAlignment(Pos.TOP_LEFT);
        form.setMaxWidth(390);

        Label title = new Label("Iniciar sesión");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill:" + StyleHelper.TEXT_DARK + ";");

        Label subtitle = new Label("Introduce tus credenciales para acceder");
        subtitle.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;");
        VBox.setMargin(subtitle, new Insets(0, 0, 8, 0));

        Label emailLbl = labelField("Correo electrónico");
        TextField emailField = new TextField();
        emailField.setPromptText("usuario@quimipapel.com");
        emailField.setPrefHeight(42);
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle(inputStyle());

        Label passLbl = labelField("Contraseña");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Mínimo 6 caracteres");
        passField.setPrefHeight(42);
        passField.setMaxWidth(Double.MAX_VALUE);
        passField.setStyle(inputStyle());

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill:#EF4444;-fx-font-size:12;");
        errorLabel.setWrapText(true);

        Button loginBtn = new Button("Entrar al sistema");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(46);
        loginBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        loginBtn.setStyle(btnGreen());
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(btnGreenDark()));
        loginBtn.setOnMouseExited (e -> loginBtn.setStyle(btnGreen()));
        VBox.setMargin(loginBtn, new Insets(6, 0, 0, 0));

        Label hint = new Label("Demo: carlos.fernandez@quimipapel.com / password");
        hint.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:11;");
        hint.setWrapText(true);

        form.getChildren().addAll(
            title, subtitle,
            new VBox(6, emailLbl, emailField),
            new VBox(6, passLbl,  passField),
            errorLabel, loginBtn, hint
        );

        right.getChildren().add(form);
        right.setPadding(new Insets(50, 60, 50, 60));

        /* ── Acción login ── */
        Runnable doLogin = () -> {
            String email = emailField.getText().trim();
            String pass  = passField.getText();
            if (email.isBlank() || pass.isBlank()) {
                errorLabel.setText("Completa todos los campos.");
                return;
            }
            Usuario user;
            try {
                user = usuarioDAO.validarCredenciales(email, pass);
            } catch (Exception ex) {
                errorLabel.setText("No se pudo conectar con la base de datos. Revisa MySQL y el script database/quimipapel.sql.");
                return;
            }
            if (user == null) {
                errorLabel.setText("Correo o contraseña incorrectos, o usuario desactivado.");
                return;
            }
            SessionManager.getInstance().setUsuarioActual(user);
            new MainView(stage).show();
        };
        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());

        /* ── Layout raíz ── */
        HBox root = new HBox(left, right);

        double sw = Screen.getPrimary().getVisualBounds().getWidth();
        double sh = Screen.getPrimary().getVisualBounds().getHeight();
        double w  = Math.max(800,  Math.min(sw * 0.72, 1050));
        double h  = Math.max(500,  Math.min(sh * 0.68,  680));

        Scene scene = new Scene(root, w, h);
        stage.setScene(scene);
        stage.setTitle("QUIMIPAPEL - Iniciar sesión");
        stage.setMinWidth(750); stage.setMinHeight(480);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }


    private ImageView buildLoginBanner() {
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/quimipapel-login-banner.png"));
            imageView.setImage(image);
        } catch (Exception ignored) {
            // Si la imagen no existe en recursos, la app no se rompe.
        }
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(430);
        imageView.setSmooth(true);
        return imageView;
    }

    private Label labelField(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:12;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.TEXT_DARK + ";");
        return l;
    }
    private String inputStyle() {
        return "-fx-background-color:#F9FAFB;-fx-border-color:" + StyleHelper.BORDER +
               ";-fx-border-radius:8;-fx-background-radius:8;-fx-padding:0 14;-fx-font-size:13;";
    }
    private String btnGreen()     { return "-fx-background-color:" + StyleHelper.GREEN + ";-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;"; }
    private String btnGreenDark() { return "-fx-background-color:" + StyleHelper.GREEN_DARK + ";-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;"; }
}
