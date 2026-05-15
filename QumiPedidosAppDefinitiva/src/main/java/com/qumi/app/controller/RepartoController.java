package com.qumi.app.controller;

import com.qumi.app.dao.PedidoDAO;
import com.qumi.app.model.EstadoPedido;
import com.qumi.app.model.Pedido;
import com.qumi.app.util.Alerts;
import com.qumi.app.util.CsvExporter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RepartoController {
    @FXML private TableView<Pedido> repartoTable;
    @FXML private TableColumn<Pedido, Integer> idColumn;
    @FXML private TableColumn<Pedido, String> clienteColumn;
    @FXML private TableColumn<Pedido, String> direccionColumn;
    @FXML private TableColumn<Pedido, String> productoColumn;
    @FXML private TableColumn<Pedido, Integer> cantidadColumn;
    @FXML private TableColumn<Pedido, String> urgenciaColumn;
    @FXML private TableColumn<Pedido, String> estadoColumn;
    @FXML private TableColumn<Pedido, LocalDate> fechaEntregaColumn;
    @FXML private TextArea incidenciaArea;
    @FXML private Label selectedLabel;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private Pedido selected;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccionEntrega"));
        productoColumn.setCellValueFactory(new PropertyValueFactory<>("productoNombre"));
        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        urgenciaColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUrgenciaTexto()));
        estadoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstadoTexto()));
        fechaEntregaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        repartoTable.getSelectionModel().selectedItemProperty().addListener((obs, old, value) -> select(value));
        refresh();
    }

    @FXML
    private void refresh() {
        try {
            List<Pedido> pedidos = pedidoDAO.findForReparto();
            repartoTable.setItems(FXCollections.observableArrayList(pedidos));
        } catch (SQLException e) {
            Alerts.error("Error al cargar reparto", e.getMessage());
        }
    }

    @FXML private void marcarPreparacion() { change(EstadoPedido.EN_PREPARACION, "Pedido enviado a preparación."); }
    @FXML private void marcarReparto() { change(EstadoPedido.EN_REPARTO, "Pedido marcado como en reparto."); }
    @FXML private void marcarEntregado() { change(EstadoPedido.ENTREGADO, "Pedido marcado como entregado."); }
    @FXML private void marcarIncidencia() { change(EstadoPedido.INCIDENCIA, "Incidencia registrada."); }

    @FXML
    private void exportCsv() {
        try {
            CsvExporter.exportPedidos(repartoTable.getScene().getWindow(), repartoTable.getItems());
            Alerts.info("Exportación completada", "Se ha generado el archivo CSV.");
        } catch (Exception e) {
            Alerts.error("No se pudo exportar", e.getMessage());
        }
    }

    private void change(EstadoPedido estado, String okMessage) {
        if (selected == null) {
            Alerts.info("Selecciona un pedido", "Elige un pedido de la tabla.");
            return;
        }
        try {
            pedidoDAO.updateEstado(selected.getId(), estado, incidenciaArea.getText());
            incidenciaArea.clear();
            refresh();
            Alerts.info("Estado actualizado", okMessage);
        } catch (SQLException e) {
            Alerts.error("No se pudo actualizar", e.getMessage());
        }
    }

    private void select(Pedido pedido) {
        selected = pedido;
        if (pedido == null) {
            selectedLabel.setText("Sin pedido seleccionado");
            return;
        }
        selectedLabel.setText("Pedido #" + pedido.getId() + " · " + pedido.getClienteNombre());
        incidenciaArea.setText(pedido.getNotas());
    }
}
