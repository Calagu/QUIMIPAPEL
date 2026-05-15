package com.qumi.app.controller;

import com.qumi.app.dao.UsuarioDAO;
import com.qumi.app.model.Role;
import com.qumi.app.model.Usuario;
import com.qumi.app.util.Alerts;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class UsuariosController {
    @FXML private TableView<Usuario> usuariosTable;
    @FXML private TableColumn<Usuario, Integer> idColumn;
    @FXML private TableColumn<Usuario, String> nombreColumn;
    @FXML private TableColumn<Usuario, String> usernameColumn;
    @FXML private TableColumn<Usuario, String> roleColumn;
    @FXML private TableColumn<Usuario, String> activoColumn;

    @FXML private TextField searchField;
    @FXML private TextField nombreField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private CheckBox activoCheck;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario selected;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoleTexto()));
        activoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getActivoTexto()));
        roleCombo.setItems(FXCollections.observableArrayList(Role.values()));
        roleCombo.setValue(Role.COMERCIAL);
        activoCheck.setSelected(true);
        usuariosTable.getSelectionModel().selectedItemProperty().addListener((obs, old, value) -> select(value));
        refresh();
    }

    @FXML
    private void refresh() {
        try {
            List<Usuario> usuarios = usuarioDAO.findAll(searchField.getText());
            usuariosTable.setItems(FXCollections.observableArrayList(usuarios));
        } catch (SQLException e) {
            Alerts.error("Error al cargar usuarios", e.getMessage());
        }
    }

    @FXML
    private void save() {
        try {
            Usuario u = selected == null ? new Usuario() : selected;
            u.setNombre(required(nombreField.getText(), "Nombre"));
            u.setUsername(required(usernameField.getText(), "Usuario"));
            u.setPassword(required(passwordField.getText(), "Contraseña"));
            u.setRole(roleCombo.getValue());
            u.setActivo(activoCheck.isSelected());
            usuarioDAO.save(u);
            clear();
            refresh();
            Alerts.info("Usuario guardado", "El usuario se ha guardado correctamente.");
        } catch (Exception e) {
            Alerts.error("No se pudo guardar", e.getMessage());
        }
    }

    @FXML
    private void delete() {
        if (selected == null) {
            Alerts.info("Selecciona un usuario", "Elige un usuario de la tabla.");
            return;
        }
        try {
            usuarioDAO.delete(selected.getId());
            clear();
            refresh();
        } catch (SQLException e) {
            Alerts.error("No se pudo eliminar", e.getMessage());
        }
    }

    @FXML
    private void clear() {
        selected = null;
        usuariosTable.getSelectionModel().clearSelection();
        nombreField.clear();
        usernameField.clear();
        passwordField.clear();
        roleCombo.setValue(Role.COMERCIAL);
        activoCheck.setSelected(true);
    }

    private void select(Usuario u) {
        selected = u;
        if (u == null) return;
        nombreField.setText(u.getNombre());
        usernameField.setText(u.getUsername());
        passwordField.setText(u.getPassword());
        roleCombo.setValue(u.getRole());
        activoCheck.setSelected(u.isActivo());
    }

    private String required(String value, String field) {
        if (value == null || value.trim().isBlank()) throw new IllegalArgumentException(field + " es obligatorio.");
        return value.trim();
    }
}
