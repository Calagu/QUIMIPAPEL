package com.qumi.app.model;

public class Indicadores {
    private final int totalPedidos;
    private final int pendientes;
    private final int enReparto;
    private final int entregados;
    private final int urgentes;
    private final int stockBajo;

    public Indicadores(int totalPedidos, int pendientes, int enReparto, int entregados, int urgentes, int stockBajo) {
        this.totalPedidos = totalPedidos;
        this.pendientes = pendientes;
        this.enReparto = enReparto;
        this.entregados = entregados;
        this.urgentes = urgentes;
        this.stockBajo = stockBajo;
    }

    public int getTotalPedidos() { return totalPedidos; }
    public int getPendientes() { return pendientes; }
    public int getEnReparto() { return enReparto; }
    public int getEntregados() { return entregados; }
    public int getUrgentes() { return urgentes; }
    public int getStockBajo() { return stockBajo; }
}
