package com.qumi.app.model;

public enum Role {
    ADMIN("Administrador"),
    COMERCIAL("Comercial"),
    OFICINA("Oficina"),
    REPARTIDOR("Repartidor");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
