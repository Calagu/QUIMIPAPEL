package com.qumi.app.controller;

import com.qumi.app.dao.ClienteDAO;
import com.qumi.app.model.Cliente;
import com.qumi.app.util.Alerts;
import com.qumi.app.util.CsvExporter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class ClientesController {
    @FXML private TableView<Cliente> clientesTable;
    @FXML private TableColumn<Cliente, Integer> idColumn;
    @FXML private TableColumn<Cliente, String> nombreColumn;
    @FXML private TableColumn<Cliente, String> empresaColumn;
    @FXML private TableColumn<Cliente, String> telefonoColumn;
    @FXML private TableColumn<Cliente, String> emailColumn;
    @FXML private TableColumn<Cliente, String> zonaColumn;
    @FXML private TableColumn<Cliente, String> activoColumn;

    @FXML private TextField searchField;
    @FXML private TextField nombreField;
    @FXML private TextField empresaField;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private TextField zonaField;
    @FXML private TextArea direccionArea;
    @FXML private CheckBox activoCheck;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Cliente selected;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        empresaColumn.setCellValueFactory(new PropertyValueFactory<>("empresa"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        zonaColumn.setCellValueFactory(new PropertyValueFactory<>("zona"));
        activoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getActivoTexto()));
        clientesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, value) -> select(value));
        activoCheck.setSelected(true);
        refresh();
    }

    @FXML
    private void refresh() {
        try {
            List<Cliente> clientes = clienteDAO.findAll(searchField.getText());
            clientesTable.setItems(FXCollections.observableArrayList(clientes));
        } catch (SQLException e) {
            Alerts.error("Error al cargar clientes", e.getMessage());
        }
    }

    @FXML
    private void save() {
        try {
            Cliente c = selected == null ? new Cliente() : selected;
            c.setNombre(required(nombreField.getText(), "Nombre"));
            c.setEmpresa(empresaField.getText());
            c.setTelefono(telefonoField.getText());
            c.setEmail(emailField.getText());
            c.setDireccion(required(direccionArea.getText(), "Dirección"));
            c.setZona(zonaField.getText());
            c.setActivo(activoCheck.isSelected());
            clienteDAO.save(c);
            clear();
            refresh();
            Alerts.info("Cliente guardado", "El cliente se ha guardado correctamente.");
        } catch (Exception e) {
            Alerts.error("No se pudo guardar", e.getMessage());
        }
    }

    @FXML
    private void delete() {
        if (selected == null) {
            Alerts.info("Selecciona un cliente", "Elige un cliente de la tabla.");
            return;
        }
        try {
            clienteDAO.delete(selected.getId());
            clear();
            refresh();
        } catch (SQLException e) {
            Alerts.error("No se pudo eliminar", "El cliente puede tener pedidos asociados. Puedes desactivarlo en lugar de eliminarlo.");
        }
    }

    @FXML
    private void clear() {
        selected = null;
        clientesTable.getSelectionModel().clearSelection();
        nombreField.clear();
        empresaField.clear();
        telefonoField.clear();
        emailField.clear();
        direccionArea.clear();
        zonaField.clear();
        activoCheck.setSelected(true);
    }

    @FXML
    private void exportCsv() {
        try {
            CsvExporter.exportClientes(clientesTable.getScene().getWindow(), clientesTable.getItems());
            Alerts.info("Exportación completada", "Se ha generado el archivo CSV.");
        } catch (Exception e) {
            Alerts.error("No se pudo exportar", e.getMessage());
        }
    }

    private void select(Cliente c) {
        selected = c;
        if (c == null) return;
        nombreField.setText(c.getNombre());
        empresaField.setText(c.getEmpresa());
        telefonoField.setText(c.getTelefono());
        emailField.setText(c.getEmail());
        direccionArea.setText(c.getDireccion());
        zonaField.setText(c.getZona());
        activoCheck.setSelected(c.isActivo());
    }

    private String required(String value, String field) {
        if (value == null || value.trim().isBlank()) throw new IllegalArgumentException(field + " es obligatorio.");
        return value.trim();
    }
}
