package com.qumi.app.controller;

import com.qumi.app.MainApp;
import com.qumi.app.Session;
import com.qumi.app.model.Role;
import com.qumi.app.model.Usuario;
import com.qumi.app.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML private Label userLabel;
    @FXML private Label roleLabel;
    @FXML private StackPane contentPane;
    @FXML private Button dashboardButton;
    @FXML private Button clientesButton;
    @FXML private Button productosButton;
    @FXML private Button pedidosButton;
    @FXML private Button repartoButton;
    @FXML private Button usuariosButton;

    @FXML
    private void initialize() {
        Usuario user = Session.getCurrentUser();
        if (user != null) {
            userLabel.setText(user.getNombre());
            roleLabel.setText(user.getRoleTexto());
            configureRole(user.getRole());
        }
        showDashboard();
    }

    private void configureRole(Role role) {
        if (role == Role.REPARTIDOR) {
            clientesButton.setVisible(false);
            productosButton.setVisible(false);
            pedidosButton.setVisible(false);
            usuariosButton.setVisible(false);
        } else if (role == Role.COMERCIAL) {
            usuariosButton.setVisible(false);
            repartoButton.setVisible(false);
        } else if (role == Role.OFICINA) {
            usuariosButton.setVisible(false);
        }
    }

    @FXML private void showDashboard() { load("dashboard-view.fxml"); }
    @FXML private void showClientes() { load("clientes-view.fxml"); }
    @FXML private void showProductos() { load("productos-view.fxml"); }
    @FXML private void showPedidos() { load("pedidos-view.fxml"); }
    @FXML private void showReparto() { load("reparto-view.fxml"); }
    @FXML private void showUsuarios() { load("usuarios-view.fxml"); }

    @FXML
    private void logout() {
        try {
            Session.clear();
            MainApp.getInstance().showLogin();
        } catch (Exception e) {
            Alerts.error("Error", e.getMessage());
        }
    }

    private void load(String view) {
        try {
            Node node = MainApp.getInstance().loadFXML(view);
            contentPane.getChildren().setAll(node);
        } catch (Exception e) {
            Alerts.error("No se pudo cargar la pantalla", e.getMessage());
        }
    }
}
