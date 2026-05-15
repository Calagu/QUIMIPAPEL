package com.qumi.app.util;

import com.qumi.app.model.Cliente;
import com.qumi.app.model.Pedido;
import com.qumi.app.model.Producto;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CsvExporter {
    private CsvExporter() {}

    public static void exportProductos(Window owner, List<Producto> productos) throws IOException {
        File file = choose(owner, "productos_qumi.csv");
        if (file == null) return;
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("ID;Nombre;Categoría;Descripción;Precio;Stock;Stock mínimo;Activo\n");
            for (Producto p : productos) {
                writer.write(String.join(";",
                        String.valueOf(p.getId()), esc(p.getNombre()), esc(p.getCategoria()), esc(p.getDescripcion()),
                        String.valueOf(p.getPrecio()), String.valueOf(p.getStock()), String.valueOf(p.getStockMinimo()), p.getActivoTexto()) + "\n");
            }
        }
    }

    public static void exportClientes(Window owner, List<Cliente> clientes) throws IOException {
        File file = choose(owner, "clientes_qumi.csv");
        if (file == null) return;
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("ID;Nombre;Empresa;Teléfono;Email;Dirección;Zona;Activo\n");
            for (Cliente c : clientes) {
                writer.write(String.join(";",
                        String.valueOf(c.getId()), esc(c.getNombre()), esc(c.getEmpresa()), esc(c.getTelefono()),
                        esc(c.getEmail()), esc(c.getDireccion()), esc(c.getZona()), c.getActivoTexto()) + "\n");
            }
        }
    }

    public static void exportPedidos(Window owner, List<Pedido> pedidos) throws IOException {
        File file = choose(owner, "pedidos_qumi.csv");
        if (file == null) return;
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("ID;Cliente;Producto;Cantidad;Estado;Urgencia;Fecha pedido;Fecha entrega;Total;Dirección;Notas\n");
            for (Pedido p : pedidos) {
                writer.write(String.join(";",
                        String.valueOf(p.getId()), esc(p.getClienteNombre()), esc(p.getProductoNombre()), String.valueOf(p.getCantidad()),
                        p.getEstadoTexto(), p.getUrgenciaTexto(), p.getFechaPedido() == null ? "" : p.getFechaPedido().format(fmt),
                        p.getFechaEntrega() == null ? "" : p.getFechaEntrega().format(fmt), String.valueOf(p.getTotal()),
                        esc(p.getDireccionEntrega()), esc(p.getNotas())) + "\n");
            }
        }
    }

    private static File choose(Window owner, String defaultFileName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar datos");
        chooser.setInitialFileName(defaultFileName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        return chooser.showSaveDialog(owner);
    }

    private static String esc(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
