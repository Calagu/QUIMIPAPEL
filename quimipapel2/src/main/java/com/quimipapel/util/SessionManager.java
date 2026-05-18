package com.quimipapel.util;

import com.quimipapel.model.Usuario;

/** Mantiene la sesión del usuario logueado y centraliza permisos por rol. */
public class SessionManager {

    private static SessionManager instance;
    private Usuario usuarioActual;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario u) { usuarioActual = u; }
    public void cerrarSesion() { usuarioActual = null; }

    public String getRolActual() {
        return usuarioActual != null ? usuarioActual.getRol() : "";
    }

    public boolean hasRole(String rol) {
        return usuarioActual != null && rol != null && rol.equals(usuarioActual.getRol());
    }

    public boolean isAdmin() { return hasRole("Administrador"); }
    public boolean isComercial() { return hasRole("Comercial"); }
    public boolean isRepartidor() { return hasRole("Repartidor"); }
    public boolean isOficina() { return hasRole("Oficina"); }

    public boolean canManageUsers() { return isAdmin(); }
    public boolean canManageProducts() { return isAdmin() || isOficina(); }
    public boolean canManageClients() { return isAdmin() || isOficina(); }
    public boolean canCreateOrders() { return isAdmin() || isOficina() || isComercial(); }
    public boolean canDeleteOrders() { return isAdmin() || isOficina(); }
    public boolean canManageDelivery() { return isAdmin() || isOficina() || isRepartidor(); }
}
