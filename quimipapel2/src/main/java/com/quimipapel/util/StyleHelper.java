package com.quimipapel.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/** Utilidades de estilo reutilizables para toda la UI. */
public class StyleHelper {

    // ─── Colores corporativos ────────────────────────────
    public static final String GREEN       = "#16A34A";
    public static final String GREEN_DARK  = "#15803D";
    public static final String GREEN_LIGHT = "#DCFCE7";
    public static final String BG_SIDEBAR  = "#F9FAFB";
    public static final String BG_MAIN     = "#F3F4F6";
    public static final String CARD_BG     = "#FFFFFF";
    public static final String TEXT_DARK   = "#111827";
    public static final String TEXT_GRAY   = "#6B7280";
    public static final String BORDER      = "#E5E7EB";

    // ─── Colores de estado ───────────────────────────────
    public static final String COLOR_PENDIENTE  = "#F59E0B";
    public static final String COLOR_PREPARADO  = "#3B82F6";
    public static final String COLOR_ENTREGADO  = "#16A34A";
    public static final String COLOR_INCIDENCIA = "#EF4444";
    public static final String COLOR_URGENTE    = "#EF4444";

    public static String badgeColor(String estado) {
        return switch (estado == null ? "" : estado) {
            case "Pendiente"  -> COLOR_PENDIENTE;
            case "Preparado"  -> COLOR_PREPARADO;
            case "Cargado"    -> "#6366F1";
            case "En reparto" -> "#8B5CF6";
            case "Entregado"  -> COLOR_ENTREGADO;
            case "Incidencia" -> COLOR_INCIDENCIA;
            case "Urgente"    -> COLOR_URGENTE;
            default -> TEXT_GRAY;
        };
    }

    public static String stockColor(String nivel) {
        return switch (nivel == null ? "" : nivel) {
            case "Alto"      -> COLOR_ENTREGADO;
            case "Medio"     -> COLOR_PENDIENTE;
            case "Bajo"      -> COLOR_INCIDENCIA;
            case "Sin stock" -> "#9CA3AF";
            default -> TEXT_GRAY;
        };
    }

    /** Badge con color según estado */
    public static Label badge(String texto) {
        Label l = new Label(texto);
        String c = badgeColor(texto);
        l.setStyle(String.format(
            "-fx-background-color:%s20;-fx-text-fill:%s;" +
            "-fx-background-radius:20;-fx-padding:3 10 3 10;" +
            "-fx-font-size:11;-fx-font-weight:bold;", c, c));
        return l;
    }

    /** Tarjeta blanca con sombra */
    public static VBox card(double radius) {
        VBox v = new VBox();
        v.setStyle(String.format(
            "-fx-background-color:%s;-fx-background-radius:%.0f;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),8,0,0,2);",
            CARD_BG, radius));
        return v;
    }

    /** Botón primario verde */
    public static javafx.scene.control.Button btnPrimary(String text) {
        javafx.scene.control.Button b = new javafx.scene.control.Button(text);
        b.setStyle(btnGreenStyle(GREEN));
        b.setOnMouseEntered(e -> b.setStyle(btnGreenStyle(GREEN_DARK)));
        b.setOnMouseExited (e -> b.setStyle(btnGreenStyle(GREEN)));
        return b;
    }

    private static String btnGreenStyle(String color) {
        return String.format(
            "-fx-background-color:%s;-fx-text-fill:white;-fx-background-radius:8;" +
            "-fx-font-weight:bold;-fx-font-size:13;-fx-padding:8 18 8 18;-fx-cursor:hand;", color);
    }

    /** Botón secundario con borde */
    public static javafx.scene.control.Button btnSecondary(String text) {
        javafx.scene.control.Button b = new javafx.scene.control.Button(text);
        b.setStyle(String.format(
            "-fx-background-color:transparent;-fx-text-fill:%s;" +
            "-fx-border-color:%s;-fx-border-radius:8;-fx-background-radius:8;" +
            "-fx-font-size:13;-fx-padding:8 18 8 18;-fx-cursor:hand;",
            TEXT_DARK, BORDER));
        return b;
    }

    /** Avatar circular con iniciales */
    public static StackPane avatar(String iniciales, String colorHex, double size) {
        Circle circle = new Circle(size / 2, Color.web(colorHex));
        Label label = new Label(iniciales);
        label.setFont(Font.font("System", FontWeight.BOLD, size * 0.35));
        label.setTextFill(Color.WHITE);
        StackPane sp = new StackPane(circle, label);
        sp.setMaxSize(size, size);
        sp.setMinSize(size, size);
        return sp;
    }

    /** Campo de texto estilizado */
    public static javafx.scene.control.TextField textField(String prompt) {
        javafx.scene.control.TextField tf = new javafx.scene.control.TextField();
        tf.setPromptText(prompt);
        tf.setStyle(String.format(
            "-fx-background-color:%s;-fx-border-color:%s;" +
            "-fx-border-radius:8;-fx-background-radius:8;" +
            "-fx-padding:8 12 8 12;-fx-font-size:13;", CARD_BG, BORDER));
        return tf;
    }

    /** ComboBox estilizado */
    public static <T> javafx.scene.control.ComboBox<T> comboBox() {
        javafx.scene.control.ComboBox<T> cb = new javafx.scene.control.ComboBox<>();
        cb.setStyle(String.format(
            "-fx-background-color:%s;-fx-border-color:%s;" +
            "-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13;",
            CARD_BG, BORDER));
        return cb;
    }

    /** Línea separadora horizontal */
    public static Region separator() {
        Region r = new Region();
        r.setPrefHeight(1);
        r.setStyle("-fx-background-color:" + BORDER + ";");
        return r;
    }
}
