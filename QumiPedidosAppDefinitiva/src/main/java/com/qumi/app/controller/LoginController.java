package com.qumi.app.controller;

import com.qumi.app.MainApp;
import com.qumi.app.Session;
import com.qumi.app.dao.UsuarioDAO;
import com.qumi.app.model.Usuario;
import com.qumi.app.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Optional;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    @FXML
    private void login() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Introduce usuario y contraseña.");
            return;
        }

        try {
            Optional<Usuario> user = usuarioDAO.authenticate(username, password);
            if (user.isPresent()) {
                Session.setCurrentUser(user.get());
                MainApp.getInstance().showMain();
            } else {
                errorLabel.setText("Credenciales incorrectas o usuario inactivo.");
            }
        } catch (SQLException e) {
            Alerts.error("Error de base de datos", e.getMessage());
        } catch (Exception e) {
            Alerts.error("Error", "No se pudo abrir la aplicación: " + e.getMessage());
        }
    }
}
