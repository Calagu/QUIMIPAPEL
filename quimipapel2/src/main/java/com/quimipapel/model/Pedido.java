package com.quimipapel.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private int clienteId;
    private String clienteNombre;
    private String clienteDireccion;
    private String clienteTelefono;
    private int usuarioId;
    private Integer conductorId;
    private String conductorNombre;
    private LocalDateTime fecha;
    private String estado;   // Pendiente | Preparado | Cargado | En reparto | Entregado | Incidencia
    private String urgencia; // Normal | Urgente
    private boolean reparto;
    private String notas;
    private double total;
    private int numItems;
    private List<PedidoItem> items = new ArrayList<>();

    public Pedido() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String cn) { this.clienteNombre = cn; }
    public String getClienteDireccion() { return clienteDireccion; }
    public void setClienteDireccion(String clienteDireccion) { this.clienteDireccion = clienteDireccion; }
    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public Integer getConductorId() { return conductorId; }
    public void setConductorId(Integer conductorId) { this.conductorId = conductorId; }
    public String getConductorNombre() { return conductorNombre; }
    public void setConductorNombre(String cn) { this.conductorNombre = cn; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUrgencia() { return urgencia; }
    public void setUrgencia(String urgencia) { this.urgencia = urgencia; }
    public boolean isReparto() { return reparto; }
    public void setReparto(boolean reparto) { this.reparto = reparto; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public int getNumItems() { return numItems; }
    public void setNumItems(int numItems) { this.numItems = numItems; }
    public List<PedidoItem> getItems() { return items; }
    public void setItems(List<PedidoItem> items) { this.items = items; }

    public String getIdFormateado() { return "#" + id; }
}
