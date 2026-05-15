package com.qumi.app.model;

public class Producto {
    private int id;
    private String nombre;
    private String categoria;
    private String descripcion;
    private double precio;
    private int stock;
    private int stockMinimo;
    private boolean activo;

    public Producto() {}

    public Producto(int id, String nombre, String categoria, String descripcion, double precio, int stock, int stockMinimo, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getActivoTexto() { return activo ? "Sí" : "No"; }
    public String getStockEstado() { return stock <= stockMinimo ? "Bajo" : "OK"; }

    @Override
    public String toString() {
        return nombre + " · " + String.format("%.2f €", precio);
    }
}
