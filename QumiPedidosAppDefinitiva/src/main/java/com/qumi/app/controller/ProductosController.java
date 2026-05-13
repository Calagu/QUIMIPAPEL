package com.qumi.app.controller;

import com.qumi.app.dao.ProductoDAO;
import com.qumi.app.model.Producto;
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

public class ProductosController {
    @FXML private TableView<Producto> productosTable;
    @FXML private TableColumn<Producto, Integer> idColumn;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, String> categoriaColumn;
    @FXML private TableColumn<Producto, Double> precioColumn;
    @FXML private TableColumn<Producto, Integer> stockColumn;
    @FXML private TableColumn<Producto, String> stockEstadoColumn;
    @FXML private TableColumn<Producto, String> activoColumn;

    @FXML private TextField searchField;
    @FXML private TextField nombreField;
    @FXML private TextField categoriaField;
    @FXML private TextField precioField;
    @FXML private TextField stockField;
    @FXML private TextField stockMinimoField;
    @FXML private TextArea descripcionArea;
    @FXML private CheckBox activoCheck;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private Producto selected;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        categoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockEstadoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStockEstado()));
        activoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getActivoTexto()));

        productosTable.getSelectionModel().selectedItemProperty().addListener((obs, old, value) -> select(value));
        activoCheck.setSelected(true);
        refresh();
    }

    @FXML
    private void refresh() {
        try {
            List<Producto> productos = productoDAO.findAll(searchField.getText());
            productosTable.setItems(FXCollections.observableArrayList(productos));
        } catch (SQLException e) {
            Alerts.error("Error al cargar productos", e.getMessage());
        }
    }

    @FXML
    private void save() {
        try {
            Producto p = selected == null ? new Producto() : selected;
            p.setNombre(required(nombreField.getText(), "Nombre"));
            p.setCategoria(required(categoriaField.getText(), "Categoría"));
            p.setDescripcion(descripcionArea.getText());
            p.setPrecio(parseDouble(precioField.getText(), "Precio"));
            p.setStock(parseInt(stockField.getText(), "Stock"));
            p.setStockMinimo(parseInt(stockMinimoField.getText(), "Stock mínimo"));
            p.setActivo(activoCheck.isSelected());
            productoDAO.save(p);
            clear();
            refresh();
            Alerts.info("Producto guardado", "El producto se ha guardado correctamente.");
        } catch (Exception e) {
            Alerts.error("No se pudo guardar", e.getMessage());
        }
    }

    @FXML
    private void delete() {
        if (selected == null) {
            Alerts.info("Selecciona un producto", "Elige un producto de la tabla.");
            return;
        }
        try {
            productoDAO.delete(selected.getId());
            clear();
            refresh();
        } catch (SQLException e) {
            Alerts.error("No se pudo eliminar", "El producto puede estar usado en pedidos. Puedes desactivarlo en lugar de eliminarlo.");
        }
    }

    @FXML
    private void clear() {
        selected = null;
        productosTable.getSelectionModel().clearSelection();
        nombreField.clear();
        categoriaField.clear();
        descripcionArea.clear();
        precioField.clear();
        stockField.clear();
        stockMinimoField.setText("5");
        activoCheck.setSelected(true);
    }

    @FXML
    private void exportCsv() {
        try {
            CsvExporter.exportProductos(productosTable.getScene().getWindow(), productosTable.getItems());
            Alerts.info("Exportación completada", "Se ha generado el archivo CSV.");
        } catch (Exception e) {
            Alerts.error("No se pudo exportar", e.getMessage());
        }
    }

    private void select(Producto p) {
        selected = p;
        if (p == null) return;
        nombreField.setText(p.getNombre());
        categoriaField.setText(p.getCategoria());
        descripcionArea.setText(p.getDescripcion());
        precioField.setText(String.valueOf(p.getPrecio()));
        stockField.setText(String.valueOf(p.getStock()));
        stockMinimoField.setText(String.valueOf(p.getStockMinimo()));
        activoCheck.setSelected(p.isActivo());
    }

    private String required(String value, String field) {
        if (value == null || value.trim().isBlank()) throw new IllegalArgumentException(field + " es obligatorio.");
        return value.trim();
    }

    private int parseInt(String value, String field) {
        try { return Integer.parseInt(required(value, field)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(field + " debe ser un número entero."); }
    }

    private double parseDouble(String value, String field) {
        try { return Double.parseDouble(required(value, field).replace(',', '.')); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(field + " debe ser un número decimal."); }
    }
}
