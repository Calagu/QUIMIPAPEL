package com.qumi.app.controller;

import com.qumi.app.dao.PedidoDAO;
import com.qumi.app.model.Indicadores;
import com.qumi.app.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class DashboardController {
    @FXML private Label totalPedidosLabel;
    @FXML private Label pendientesLabel;
    @FXML private Label repartoLabel;
    @FXML private Label entregadosLabel;
    @FXML private Label urgentesLabel;
    @FXML private Label stockBajoLabel;

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @FXML
    private void initialize() {
        refresh();
    }

    @FXML
    private void refresh() {
        try {
            Indicadores i = pedidoDAO.getIndicadores();
            totalPedidosLabel.setText(String.valueOf(i.getTotalPedidos()));
            pendientesLabel.setText(String.valueOf(i.getPendientes()));
            repartoLabel.setText(String.valueOf(i.getEnReparto()));
            entregadosLabel.setText(String.valueOf(i.getEntregados()));
            urgentesLabel.setText(String.valueOf(i.getUrgentes()));
            stockBajoLabel.setText(String.valueOf(i.getStockBajo()));
        } catch (SQLException e) {
            Alerts.error("Error al cargar indicadores", e.getMessage());
        }
    }
}
