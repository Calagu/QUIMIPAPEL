package com.qumi.semana1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Semana1App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Semana1App.class.getResource("/com/qumi/semana1/view/login-boceto.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        scene.getStylesheets().add(Semana1App.class.getResource("/com/qumi/semana1/css/semana1.css").toExternalForm());
        stage.setTitle("Quimi Papel");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
