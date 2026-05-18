package com.quimipapel.model;

public class Producto {
    private int id;
    private String nombre;
    private String sku;
    private int categoriaId;
    private String categoriaNombre;
    private double precio;
    private int stock;
    private int stockMinimo;
    private boolean activo;

    public Producto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String cn) { this.categoriaNombre = cn; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    /** Alto / Medio / Bajo según stock */
    public String getNivelStock() {
        if (stock <= 0) return "Sin stock";
        if (stock <= stockMinimo) return "Bajo";
        if (stock <= stockMinimo * 3) return "Medio";
        return "Alto";
    }
}
