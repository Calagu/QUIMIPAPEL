package com.quimipapel;

import com.quimipapel.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new LoginView(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
