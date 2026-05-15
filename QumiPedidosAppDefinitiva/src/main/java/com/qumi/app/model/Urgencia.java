package com.qumi.app.model;

public enum Urgencia {
    BAJA("Baja", 1),
    NORMAL("Normal", 2),
    ALTA("Alta", 3),
    URGENTE("Urgente", 4);

    private final String label;
    private final int priority;

    Urgencia(String label, int priority) {
        this.label = label;
        this.priority = priority;
    }

    public String getLabel() {
        return label;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return label;
    }
}
