package com.ggc.theaterkarten;

import javafx.stage.Stage;
import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        UIManager UIManager = new UIManager(primaryStage);

        UIManager.manageScenes();
    }
    public static void main(String[] args) {
        launch();
    }
}