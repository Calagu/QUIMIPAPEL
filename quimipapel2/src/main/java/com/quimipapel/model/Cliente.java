package com.quimipapel.model;

public class Cliente {
    private int id;
    private String empresa;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private boolean activo;
    private int totalPedidos;
    private double totalFacturado;

    public Cliente() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String cp) { this.codigoPostal = cp; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public int getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(int n) { this.totalPedidos = n; }
    public double getTotalFacturado() { return totalFacturado; }
    public void setTotalFacturado(double t) { this.totalFacturado = t; }

    public String getDireccionCompleta() {
        return direccion + (ciudad != null ? ", " + codigoPostal + " " + ciudad : "");
    }
}
