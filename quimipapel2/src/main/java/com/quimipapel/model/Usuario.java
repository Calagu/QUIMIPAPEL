package com.quimipapel.model;

import java.time.LocalDateTime;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String passwordHash;
    private String rol;
    private boolean activo;
    private LocalDateTime ultimoAcceso;
    private String fotoPerfilPath;

    public Usuario() {}

    public Usuario(int id, String nombre, String email, String telefono,
                   String passwordHash, String rol, boolean activo, LocalDateTime ultimoAcceso) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
        this.ultimoAcceso = ultimoAcceso;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    public String getFotoPerfilPath() { return fotoPerfilPath; }
    public void setFotoPerfilPath(String fotoPerfilPath) { this.fotoPerfilPath = fotoPerfilPath; }

    /** Iniciales para el avatar */
    public String getIniciales() {
        if (nombre == null || nombre.isBlank()) return "??";
        String[] p = nombre.trim().split("\\s+");
        if (p.length == 1) return p[0].substring(0, Math.min(2, p[0].length())).toUpperCase();
        return ("" + p[0].charAt(0) + p[p.length-1].charAt(0)).toUpperCase();
    }
}
