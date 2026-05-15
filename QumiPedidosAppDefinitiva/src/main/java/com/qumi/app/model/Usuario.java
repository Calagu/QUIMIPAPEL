package com.qumi.app.model;

public class Usuario {
    private int id;
    private String nombre;
    private String username;
    private String password;
    private Role role;
    private boolean activo;

    public Usuario() {}

    public Usuario(int id, String nombre, String username, String password, Role role, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.role = role;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getRoleTexto() { return role == null ? "" : role.getLabel(); }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getActivoTexto() { return activo ? "Sí" : "No"; }
}
