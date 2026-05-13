package com.qumi.app;

import com.qumi.app.dao.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    private static MainApp instance;
    private Stage stage;

    public static MainApp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.stage = primaryStage;
        Database.init();
        showLogin();
        stage.setTitle("QUMI Papel - Gestión de pedidos y reparto");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.show();
    }

    public void showLogin() throws IOException {
        Parent root = loadFXML("login-view.fxml");
        Scene scene = new Scene(root, 1050, 700);
        scene.getStylesheets().add(getClass().getResource("/com/qumi/app/css/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    public void showMain() throws IOException {
        Parent root = loadFXML("main-view.fxml");
        Scene scene = new Scene(root, 1200, 760);
        scene.getStylesheets().add(getClass().getResource("/com/qumi/app/css/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    public Parent loadFXML(String fileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qumi/app/view/" + fileName));
        return loader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
