package com.qumi.app.controller;

import com.qumi.app.dao.ClienteDAO;
import com.qumi.app.dao.PedidoDAO;
import com.qumi.app.dao.ProductoDAO;
import com.qumi.app.model.Cliente;
import com.qumi.app.model.EstadoPedido;
import com.qumi.app.model.Pedido;
import com.qumi.app.model.Producto;
import com.qumi.app.model.Urgencia;
import com.qumi.app.util.Alerts;
import com.qumi.app.util.CsvExporter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PedidosController {
    @FXML private TableView<Pedido> pedidosTable;
    @FXML private TableColumn<Pedido, Integer> idColumn;
    @FXML private TableColumn<Pedido, String> clienteColumn;
    @FXML private TableColumn<Pedido, String> productoColumn;
    @FXML private TableColumn<Pedido, Integer> cantidadColumn;
    @FXML private TableColumn<Pedido, String> estadoColumn;
    @FXML private TableColumn<Pedido, String> urgenciaColumn;
    @FXML private TableColumn<Pedido, LocalDate> fechaPedidoColumn;
    @FXML private TableColumn<Pedido, LocalDate> fechaEntregaColumn;
    @FXML private TableColumn<Pedido, Double> totalColumn;
    @FXML private TableColumn<Pedido, String> direccionColumn;

    @FXML private ComboBox<Cliente> clienteCombo;
    @FXML private ComboBox<Producto> productoCombo;
    @FXML private TextField cantidadField;
    @FXML private ComboBox<Urgencia> urgenciaCombo;
    @FXML private DatePicker fechaEntregaPicker;
    @FXML private TextArea direccionArea;
    @FXML private TextArea notasArea;

    @FXML private TextField searchField;
    @FXML private ComboBox<EstadoPedido> estadoFilterCombo;
    @FXML private ComboBox<Urgencia> urgenciaFilterCombo;
    @FXML private DatePicker desdePicker;
    @FXML private DatePicker hastaPicker;
    @FXML private ComboBox<EstadoPedido> cambiarEstadoCombo;
    @FXML private TextArea notasCambioArea;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private Pedido selected;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        productoColumn.setCellValueFactory(new PropertyValueFactory<>("productoNombre"));
        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        estadoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstadoTexto()));
        urgenciaColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUrgenciaTexto()));
        fechaPedidoColumn.setCellValueFactory(new PropertyValueFactory<>("fechaPedido"));
        fechaEntregaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccionEntrega"));

        urgenciaCombo.setItems(FXCollections.observableArrayList(Urgencia.values()));
        urgenciaCombo.setValue(Urgencia.NORMAL);
        estadoFilterCombo.setItems(FXCollections.observableArrayList(EstadoPedido.values()));
        urgenciaFilterCombo.setItems(FXCollections.observableArrayList(Urgencia.values()));
        cambiarEstadoCombo.setItems(FXCollections.observableArrayList(EstadoPedido.values()));
        fechaEntregaPicker.setValue(LocalDate.now().plusDays(1));

        pedidosTable.getSelectionModel().selectedItemProperty().addListener((obs, old, value) -> select(value));
        loadCombos();
        refresh();
    }

    private void loadCombos() {
        try {
            clienteCombo.setItems(FXCollections.observableArrayList(clienteDAO.findActive()));
            productoCombo.setItems(FXCollections.observableArrayList(productoDAO.findActive()));
        } catch (SQLException e) {
            Alerts.error("Error al cargar listas", e.getMessage());
        }
    }

    @FXML
    private void refresh() {
        try {
            List<Pedido> pedidos = pedidoDAO.findAll(
                    searchField.getText(), estadoFilterCombo.getValue(), urgenciaFilterCombo.getValue(),
                    desdePicker.getValue(), hastaPicker.getValue()
            );
            pedidosTable.setItems(FXCollections.observableArrayList(pedidos));
        } catch (SQLException e) {
            Alerts.error("Error al cargar pedidos", e.getMessage());
        }
    }

    @FXML
    private void clearFilters() {
        searchField.clear();
        estadoFilterCombo.setValue(null);
        urgenciaFilterCombo.setValue(null);
        desdePicker.setValue(null);
        hastaPicker.setValue(null);
        refresh();
    }

    @FXML
    private void createPedido() {
        try {
            Cliente cliente = clienteCombo.getValue();
            Producto producto = productoCombo.getValue();
            if (cliente == null) throw new IllegalArgumentException("Selecciona un cliente.");
            if (producto == null) throw new IllegalArgumentException("Selecciona un producto.");
            int cantidad = parseInt(cantidadField.getText(), "Cantidad");
            if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
            String direccion = direccionArea.getText() == null || direccionArea.getText().isBlank()
                    ? cliente.getDireccion()
                    : direccionArea.getText().trim();

            pedidoDAO.createPedido(
                    cliente.getId(), producto, cantidad, urgenciaCombo.getValue(), fechaEntregaPicker.getValue(),
                    direccion, notasArea.getText()
            );
            clearForm();
            loadCombos();
            refresh();
            Alerts.info("Pedido creado", "El pedido se ha creado y el stock se ha actualizado.");
        } catch (Exception e) {
            Alerts.error("No se pudo crear el pedido", e.getMessage());
        }
    }

    @FXML
    private void updateEstado() {
        if (selected == null) {
            Alerts.info("Selecciona un pedido", "Elige un pedido de la tabla para cambiar su estado.");
            return;
        }
        if (cambiarEstadoCombo.getValue() == null) {
            Alerts.info("Selecciona un estado", "Indica el nuevo estado del pedido.");
            return;
        }
        try {
            pedidoDAO.updateEstado(selected.getId(), cambiarEstadoCombo.getValue(), notasCambioArea.getText());
            refresh();
            Alerts.info("Estado actualizado", "El pedido ha cambiado de estado.");
        } catch (SQLException e) {
            Alerts.error("No se pudo actualizar", e.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        clienteCombo.setValue(null);
        productoCombo.setValue(null);
        cantidadField.clear();
        urgenciaCombo.setValue(Urgencia.NORMAL);
        fechaEntregaPicker.setValue(LocalDate.now().plusDays(1));
        direccionArea.clear();
        notasArea.clear();
    }

    @FXML
    private void exportCsv() {
        try {
            CsvExporter.exportPedidos(pedidosTable.getScene().getWindow(), pedidosTable.getItems());
            Alerts.info("Exportación completada", "Se ha generado el archivo CSV.");
        } catch (Exception e) {
            Alerts.error("No se pudo exportar", e.getMessage());
        }
    }

    private void select(Pedido pedido) {
        selected = pedido;
        if (pedido == null) return;
        cambiarEstadoCombo.setValue(pedido.getEstado());
        notasCambioArea.setText(pedido.getNotas());
    }

    private int parseInt(String value, String field) {
        if (value == null || value.trim().isBlank()) throw new IllegalArgumentException(field + " es obligatoria.");
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(field + " debe ser un número entero."); }
    }
}
