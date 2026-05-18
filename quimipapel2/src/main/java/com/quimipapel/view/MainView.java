package com.quimipapel.view;

import com.quimipapel.dao.PedidoDAO;
import com.quimipapel.model.Usuario;
import com.quimipapel.util.SessionManager;
import com.quimipapel.util.StyleHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MainView {

    private final Stage stage;
    private BorderPane root;
    private VBox sidebarItems;
    private String seccionActual = "Inicio";
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final Map<String, Supplier<Region>> secciones = new LinkedHashMap<>();

    public MainView(Stage stage) { this.stage = stage; }

    public void show() {
        buildSeccionesPorRol();
        if (!secciones.containsKey(seccionActual)) seccionActual = secciones.keySet().iterator().next();

        root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setTop(buildTopbar());
        root.setCenter(wrapScroll(seccionActual));
        root.setStyle("-fx-background-color:" + StyleHelper.BG_MAIN + ";");

        SessionManager.getInstance().clearSessionListeners();
        SessionManager.getInstance().addSessionListener(() -> {
            if (root != null) root.setTop(buildTopbar());
        });

        double sw = Screen.getPrimary().getVisualBounds().getWidth();
        double sh = Screen.getPrimary().getVisualBounds().getHeight();

        Scene scene = new Scene(root, sw * 0.88, sh * 0.88);
        stage.setScene(scene);
        stage.setTitle("QUIMIPAPEL – Gestión DAM v1.3.1");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setMaximized(true);
        stage.show();
    }

    private void buildSeccionesPorRol() {
        secciones.clear();
        SessionManager sm = SessionManager.getInstance();

        if (sm.isRepartidor()) {
            secciones.put("Reparto", () -> new RepartoView().build());
            secciones.put("Configuración", () -> new ConfiguracionView().build());
            seccionActual = "Reparto";
            return;
        }

        secciones.put("Inicio", () -> new DashboardView(() -> navigateTo("Pedidos")).build());
        secciones.put("Pedidos", () -> new PedidosView().build());

        if (sm.isAdmin() || sm.isOficina() || sm.isComercial()) {
            secciones.put("Clientes", () -> new ClientesView().build());
        }
        if (sm.isAdmin() || sm.isOficina()) {
            secciones.put("Productos", () -> new ProductosView().build());
        }
        if (sm.canManageDelivery()) {
            secciones.put("Reparto", () -> new RepartoView().build());
        }
        if (sm.canManageUsers()) {
            secciones.put("Usuarios", () -> new UsuariosView().build());
        }
        secciones.put("Configuración", () -> new ConfiguracionView().build());
    }

    private void navigateTo(String nombre) {
        if (root == null || !secciones.containsKey(nombre)) return;
        seccionActual = nombre;
        refreshNavStyles();
        root.setCenter(wrapScroll(nombre));
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(210);
        sidebar.setMinWidth(190);
        sidebar.setMaxWidth(230);
        sidebar.setStyle(
            "-fx-background-color:#FFFFFF;" +
            "-fx-border-color:" + StyleHelper.BORDER + ";" +
            "-fx-border-width:0 1 0 0;");

        VBox logoBox = new VBox(3);
        logoBox.setPadding(new Insets(18, 16, 18, 16));
        Label logoLabel = new Label("QUIMIPAPEL");
        logoLabel.setFont(Font.font("System", FontWeight.BOLD, 17));
        logoLabel.setStyle("-fx-text-fill:" + StyleHelper.GREEN + ";");
        Label logoSub = new Label("Gestión por roles");
        logoSub.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:11;");
        logoBox.getChildren().addAll(logoLabel, logoSub);

        sidebarItems = new VBox(2);
        sidebarItems.setPadding(new Insets(4, 8, 4, 8));
        for (String s : secciones.keySet()) sidebarItems.getChildren().add(buildNavItem(s));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox footer = new VBox(2);
        footer.setPadding(new Insets(10, 12, 10, 12));
        footer.setStyle("-fx-background-color:#F3F4F6;-fx-background-radius:8;");
        footer.getChildren().addAll(
            new Label("Versión 1.3.1") {{ setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:10;"); }},
            new Label("© 2026 QUIMIPAPEL") {{ setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:10;"); }}
        );
        VBox.setMargin(footer, new Insets(0, 8, 8, 8));

        sidebar.getChildren().addAll(logoBox, StyleHelper.separator(), sidebarItems, spacer, footer);
        return sidebar;
    }

    private HBox buildNavItem(String nombre) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(9, 14, 9, 14));
        item.setCursor(javafx.scene.Cursor.HAND);
        item.setMaxWidth(Double.MAX_VALUE);

        Label icon  = new Label(getIcon(nombre));
        icon.setMinWidth(22);
        icon.setStyle("-fx-font-size:14;");
        Label label = new Label(nombre);
        label.setStyle("-fx-font-size:13;");
        item.getChildren().addAll(icon, label);

        applyNavStyle(item, label, nombre.equals(seccionActual));

        item.setOnMouseClicked(e -> navigateTo(nombre));
        return item;
    }

    private void applyNavStyle(HBox item, Label label, boolean active) {
        if (active) {
            item.setStyle("-fx-background-color:" + StyleHelper.GREEN + ";-fx-background-radius:8;");
            label.setStyle("-fx-text-fill:white;-fx-font-size:13;-fx-font-weight:bold;");
        } else {
            item.setStyle("-fx-background-color:transparent;-fx-background-radius:8;");
            label.setStyle("-fx-text-fill:" + StyleHelper.TEXT_DARK + ";-fx-font-size:13;");
            item.setOnMouseEntered(ev -> item.setStyle("-fx-background-color:#F3F4F6;-fx-background-radius:8;"));
            item.setOnMouseExited(ev -> {
                if (!seccionActual.equals(((Label)item.getChildren().get(1)).getText()))
                    item.setStyle("-fx-background-color:transparent;-fx-background-radius:8;");
            });
        }
    }

    private void refreshNavStyles() {
        if (sidebarItems == null) return;
        sidebarItems.getChildren().forEach(node -> {
            if (node instanceof HBox hb && hb.getChildren().size() >= 2) {
                Label lbl = (Label) hb.getChildren().get(1);
                applyNavStyle(hb, lbl, lbl.getText().equals(seccionActual));
            }
        });
    }

    private String getIcon(String s) {
        return switch (s) {
            case "Inicio"        -> "🏠";
            case "Pedidos"       -> "📦";
            case "Clientes"      -> "👤";
            case "Productos"     -> "🧊";
            case "Reparto"       -> "🚚";
            case "Usuarios"      -> "👥";
            case "Configuración" -> "⚙";
            default -> "•";
        };
    }

    private HBox buildTopbar() {
        HBox bar = new HBox(14);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10, 20, 10, 20));
        bar.setStyle(
            "-fx-background-color:#FFFFFF;" +
            "-fx-border-color:" + StyleHelper.BORDER + ";" +
            "-fx-border-width:0 0 1 0;");

        Label current = new Label("Rol: " + SessionManager.getInstance().getRolActual());
        current.setStyle("-fx-background-color:" + StyleHelper.GREEN_LIGHT + ";-fx-text-fill:" + StyleHelper.GREEN_DARK + ";-fx-background-radius:20;-fx-padding:6 12;-fx-font-size:12;-fx-font-weight:bold;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button bell = new Button("🔔 Notificaciones");
        bell.setStyle("-fx-background-color:#F9FAFB;-fx-border-color:" + StyleHelper.BORDER + ";-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;-fx-font-size:13;-fx-padding:7 12;");
        bell.setOnAction(e -> showNotificationsMenu(bell));

        Usuario user = SessionManager.getInstance().getUsuarioActual();
        String nombre    = user != null ? user.getNombre()    : "Usuario";
        String rol       = user != null ? user.getRol()       : "";

        VBox userInfo = new VBox(0);
        Label uNombre = new Label(nombre);
        uNombre.setStyle("-fx-font-weight:bold;-fx-font-size:12;");
        Label uRol = new Label(rol);
        uRol.setStyle("-fx-text-fill:" + StyleHelper.TEXT_GRAY + ";-fx-font-size:10;");
        userInfo.getChildren().addAll(uNombre, uRol);
        userInfo.setAlignment(Pos.CENTER_RIGHT);

        StackPane avatar = buildUserAvatar(user, 34);

        Button logout = new Button("Cerrar sesión");
        logout.setStyle("-fx-background-color:#FEE2E2;-fx-text-fill:#B91C1C;-fx-background-radius:8;-fx-font-weight:bold;-fx-cursor:hand;-fx-font-size:13;-fx-padding:7 12;");
        logout.setOnAction(e -> {
            SessionManager.getInstance().cerrarSesion();
            new LoginView(stage).show();
        });

        bar.getChildren().addAll(current, sp, bell, userInfo, avatar, logout);
        return bar;
    }

    private StackPane buildUserAvatar(Usuario user, double size) {
        if (user != null && user.getFotoPerfilPath() != null && !user.getFotoPerfilPath().isBlank()) {
            try {
                ImageView img = new ImageView(new Image(user.getFotoPerfilPath(), size, size, true, true));
                img.setFitWidth(size);
                img.setFitHeight(size);
                Circle clip = new Circle(size / 2, size / 2, size / 2);
                img.setClip(clip);
                StackPane sp = new StackPane(img);
                sp.setMaxSize(size, size);
                sp.setMinSize(size, size);
                return sp;
            } catch (Exception ignored) {}
        }
        return StyleHelper.avatar(user != null ? user.getIniciales() : "??", StyleHelper.GREEN, size);
    }

    private void showNotificationsMenu(Button owner) {
        ContextMenu menu = new ContextMenu();
        CustomMenuItem title = new CustomMenuItem(new Label("Notificaciones"));
        title.setHideOnClick(false);
        title.getContent().setStyle("-fx-font-weight:bold;-fx-padding:6 10;");

        MenuItem pendientes = new MenuItem("Pedidos pendientes: " + pedidoDAO.countPendientes());
        MenuItem urgentes = new MenuItem("Pedidos urgentes: " + pedidoDAO.countUrgentes());
        MenuItem incidencias = new MenuItem("Incidencias: " + pedidoDAO.countIncidencias());
        MenuItem entregados = new MenuItem("Entregados hoy: " + pedidoDAO.countEntregadosHoy());
        MenuItem abrirPedidos = new MenuItem("Abrir gestión de pedidos");
        abrirPedidos.setOnAction(e -> navigateTo("Pedidos"));

        menu.getItems().addAll(title, new SeparatorMenuItem(), pendientes, urgentes, incidencias, entregados, new SeparatorMenuItem(), abrirPedidos);
        menu.show(owner, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private ScrollPane wrapScroll(String seccion) {
        Region content = secciones.getOrDefault(seccion,
            () -> new StackPane(new Label("No tienes permiso para acceder a esta sección"))).get();

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle(
            "-fx-background-color:" + StyleHelper.BG_MAIN + ";" +
            "-fx-background:"       + StyleHelper.BG_MAIN + ";");
        return scroll;
    }
}
