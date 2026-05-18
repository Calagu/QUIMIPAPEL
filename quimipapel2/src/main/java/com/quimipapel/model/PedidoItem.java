package com.quimipapel.model;

public class PedidoItem {
    private int id;
    private int pedidoId;
    private int productoId;
    private String productoNombre;
    private int cantidad;
    private double precioUnit;

    public PedidoItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String pn) { this.productoNombre = pn; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnit() { return precioUnit; }
    public void setPrecioUnit(double precioUnit) { this.precioUnit = precioUnit; }
    public double getSubtotal() { return cantidad * precioUnit; }
}
