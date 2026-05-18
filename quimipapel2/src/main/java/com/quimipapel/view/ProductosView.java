package com.quimipapel.view;

import com.quimipapel.dao.ProductoDAO;
import com.quimipapel.model.Producto;
import com.quimipapel.util.SessionManager;
import com.quimipapel.util.StyleHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ProductosView {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private VBox tablaContainer;
    private TextField searchField;
    private ComboBox<String> cbCategoria;
    private ComboBox<String> cbStock;

    public Region build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + StyleHelper.BG_MAIN + ";");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(4,
            title("Gestión de Productos"),
            sub("Administra el catálogo de productos"));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnNuevo = new Button("+ Nuevo producto");
        btnNuevo.setStyle("-fx-background-color:#F59E0B;-fx-text-fill:white;-fx-background-radius:8;-fx-font-weight:bold;-fx-font-size:13;-fx-padding:8 18;-fx-cursor:hand;");
        btnNuevo.setOnAction(e -> showForm(null));
        header.getChildren().addAll(titleBox, sp);
        if (SessionManager.getInstance().canManageProducts()) header.getChildren().add(btnNuevo);

        // Filtros
        VBox filtrosCard = StyleHelper.card(12);
        filtrosCard.setPadding(new Insets(16));
        HBox filtrosRow = new HBox(12);
        filtrosRow.setAlignment(Pos.CENTER_LEFT);

        searchField = StyleHelper.textField("Buscar por nombre, SKU o categoría...");
        searchField.setPrefWidth(320); searchField.setPrefHeight(38);

        cbCategoria = StyleHelper.<String>comboBox();
        List<String> cats = productoDAO.getCategorias();
        cats.add(0, "Todas las categorías");
        cbCategoria.setItems(FXCollections.observableArrayList(cats));
        cbCategoria.setValue("Todas las categorías");

        cbStock = StyleHelper.<String>comboBox();
        cbStock.setItems(FXCollections.observableArrayList("Todos los stocks","Alto","Medio","Bajo","Sin stock"));
        cbStock.setValue("Todos los stocks");

        searchField.setOnKeyReleased(e -> doSearch());
        cbCategoria.setOnAction(e -> doSearch());
        cbStock.setOnAction(e -> doSearch());

        filtrosRow.getChildren().addAll(searchField, cbCategoria, cbStock);
        filtrosCard.getChildren().add(filtrosRow);

        // Tabla
        tablaContainer = new VBox(0);
        VBox tablaCard = StyleHelper.card(12);
        tablaCard.setPadding(new Insets(20));
        tablaCard.getChildren().add(tablaContainer);

        renderTabla(productoDAO.findAll());

        root.getChildren().addAll(header, filtrosCard, tablaCard);
        return root;
    }

    private void doSearch() {
        String q   = searchField.getText();
        String cat = cbCategoria.getValue();
        String stk = cbStock.getValue().equals("Todos los stocks") ? null : cbStock.getValue();
        renderTabla(productoDAO.search(q, cat, stk));
    }

    private void renderTabla(List<Producto> productos) {
        tablaContainer.getChildren().clear();

        GridPane cab = new GridPane();
        cab.setHgap(12);
        String[] colNames = {"Producto","SKU","Categoría","Precio","Stock","Estado","Acciones"};
        double[] widths   = {220, 120, 110, 80, 60, 80, 80};
        for (int i = 0; i < colNames.length; i++) {
            Label h = new Label(colNames[i]);
            h.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:11;-fx-font-weight:bold;");
            cab.add(h, i, 0);
            ColumnConstraints cc = new ColumnConstraints(); cc.setPrefWidth(widths[i]);
            cab.getColumnConstraints().add(cc);
        }
        tablaContainer.getChildren().addAll(cab, StyleHelper.separator());

        if (productos.isEmpty()) {
            tablaContainer.getChildren().add(new Label("No se encontraron productos.") {{
                setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-padding:16;");
            }});
            return;
        }

        for (Producto p : productos) {
            GridPane fila = new GridPane();
            fila.setHgap(12); fila.setVgap(4);
            fila.setPadding(new Insets(10, 0, 10, 0));
            for (ColumnConstraints cc : cab.getColumnConstraints()) {
                ColumnConstraints c2 = new ColumnConstraints(); c2.setPrefWidth(cc.getPrefWidth());
                fila.getColumnConstraints().add(c2);
            }

            HBox nombreBox = new HBox(8);
            Label iconP = new Label("📦");
            Label nomLabel = new Label(p.getNombre());
            nomLabel.setStyle("-fx-font-weight:bold;-fx-font-size:13;");
            nomLabel.setWrapText(true);
            nombreBox.getChildren().addAll(iconP, nomLabel);

            Label sku  = new Label(p.getSku()); sku.setStyle("-fx-font-size:12;-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";");
            Label cat  = new Label(p.getCategoriaNombre() != null ? p.getCategoriaNombre() : "-");
            cat.setStyle("-fx-background-color:#F3F4F6;-fx-background-radius:20;-fx-padding:2 8;-fx-font-size:11;");
            Label precio = new Label(String.format("%.2f€", p.getPrecio()));
            precio.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";-fx-font-weight:bold;");
            Label stock = new Label(String.valueOf(p.getStock()));
            stock.setStyle("-fx-font-size:13;");

            String nivel = p.getNivelStock();
            Label estado = new Label("• " + nivel);
            estado.setStyle("-fx-text-fill:" + StyleHelper.stockColor(nivel) + ";-fx-font-size:12;-fx-font-weight:bold;");

            HBox acc = new HBox(6);
            if (SessionManager.getInstance().canManageProducts()) {
                Button btnEdit = new Button("✏");
                btnEdit.setStyle("-fx-background-color:#EEF2FF;-fx-text-fill:#4F46E5;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:3 8;");
                btnEdit.setOnAction(ev -> showForm(p));

                Button btnDel = new Button("✕");
                btnDel.setStyle("-fx-background-color:#FEE2E2;-fx-text-fill:#EF4444;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:3 8;");
                btnDel.setOnAction(ev -> {
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar producto?", ButtonType.YES, ButtonType.NO);
                    if (a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                        productoDAO.delete(p.getId());
                        renderTabla(productoDAO.findAll());
                    }
                });

                acc.getChildren().addAll(btnEdit, btnDel);
            } else {
                acc.getChildren().add(this.gray("Solo consulta"));
            }

            fila.addRow(0, nombreBox, sku, cat, precio, stock, estado, acc);
            tablaContainer.getChildren().addAll(fila, StyleHelper.separator());
        }
    }

    private void showForm(Producto prod) {
        Stage d = new Stage();
        d.initModality(Modality.APPLICATION_MODAL);
        d.setTitle(prod == null ? "Nuevo producto" : "Editar producto");

        VBox form = new VBox(14);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color:white;");
        form.setPrefWidth(420);

        Label t = new Label(prod == null ? "Nuevo producto" : "Editar producto");
        t.setFont(Font.font("System", FontWeight.BOLD, 20));

        TextField tfNombre = StyleHelper.textField("Nombre del producto"); tfNombre.setPrefWidth(370);
        TextField tfSKU    = StyleHelper.textField("SKU"); tfSKU.setPrefWidth(370);

        List<String> catList = productoDAO.getCategorias();
        ComboBox<String> cbCat = StyleHelper.<String>comboBox();
        cbCat.setItems(FXCollections.observableArrayList(catList));
        cbCat.setPrefWidth(370);

        TextField tfPrecio = StyleHelper.textField("Precio €"); tfPrecio.setPrefWidth(170);
        TextField tfStock  = StyleHelper.textField("Stock"); tfStock.setPrefWidth(170);
        TextField tfMin    = StyleHelper.textField("Stock mínimo"); tfMin.setPrefWidth(170);

        if (prod != null) {
            tfNombre.setText(prod.getNombre());
            tfSKU.setText(prod.getSku());
            cbCat.setValue(prod.getCategoriaNombre());
            tfPrecio.setText(String.valueOf(prod.getPrecio()));
            tfStock.setText(String.valueOf(prod.getStock()));
            tfMin.setText(String.valueOf(prod.getStockMinimo()));
        }

        HBox stockRow = new HBox(10, vbox("Stock actual", tfStock), vbox("Stock mínimo", tfMin));

        HBox btns = new HBox(10); btns.setAlignment(Pos.CENTER_RIGHT);
        Button btnG = StyleHelper.btnPrimary("Guardar");
        Button btnC = StyleHelper.btnSecondary("Cancelar");
        btns.getChildren().addAll(btnC, btnG);
        btnC.setOnAction(e -> d.close());
        btnG.setOnAction(e -> {
            Producto np = prod != null ? prod : new Producto();
            np.setNombre(tfNombre.getText());
            np.setSku(tfSKU.getText());
            np.setCategoriaNombre(cbCat.getValue());
            try { np.setPrecio(Double.parseDouble(tfPrecio.getText().replace(",","."))); } catch (NumberFormatException ignored) {}
            try { np.setStock(Integer.parseInt(tfStock.getText())); } catch (NumberFormatException ignored) {}
            try { np.setStockMinimo(Integer.parseInt(tfMin.getText())); } catch (NumberFormatException ignored) {}
            np.setActivo(true);
            if (prod == null) productoDAO.save(np); else productoDAO.update(np);
            renderTabla(productoDAO.findAll());
            d.close();
        });

        form.getChildren().addAll(t,
            vbox("Nombre", tfNombre),
            vbox("SKU", tfSKU),
            vbox("Categoría", cbCat),
            vbox("Precio", tfPrecio),
            stockRow, btns);
        d.setScene(new Scene(form));
        d.showAndWait();
    }

    private VBox vbox(String lbl, javafx.scene.Node field) {
        Label l = new Label(lbl);
        l.setStyle("-fx-font-size:12;-fx-font-weight:bold;");
        return new VBox(4, l, field);
    }

    private Label title(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("System", FontWeight.BOLD, 26));
        return l;
    }

    private Label sub(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:13;");
        return l;
    }

    private Label gray(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:12;");
        return l;
    }
}
