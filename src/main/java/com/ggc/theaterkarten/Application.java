package com.ggc.theaterkarten;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        UIManager UIManager = new UIManager();

        BorderPane mainLayout = UIManager.createMainLayout();

        Scene scene = new Scene(mainLayout, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Theater Ticket Management");
        primaryStage.show();
        /*try {
            //Layout for the primary stage
            BorderPane root = new BorderPane();

            //Create a MenuBar
            MenuBar menuBar = new MenuBar();

            //Create a Menu
            Menu fileMenu = new Menu("File");
            Menu helpMenu = new Menu("Help");

            //Create a MenuItem with ActionListener
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent arg0) {
                    System.exit(0);
                }
            });

            //Add Menu Item to the menu
            fileMenu.getItems().add(exitItem);
            //Add Menus to the MenuBar
            menuBar.getMenus().addAll(fileMenu, helpMenu);

            //Set the menubar on Top of the BorderPane
            root.setTop(menuBar);

            //Set the TableView in center
            //root.setCenter(vt.addTableToView());

            //Set the control bar on bottom
            root.setCenter(controlBar(primaryStage));

            //Information about the screen
            Screen screen = Screen.getPrimary();

            //Rectangle to save screen information
            Rectangle2D rect = screen.getBounds();

            Scene scene = new Scene(root);
            //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            //set dimension of primaryStage
            primaryStage.setX(rect.getMinX());
            primaryStage.setY(rect.getMinY());
            primaryStage.setWidth(rect.getWidth() / 2);
            primaryStage.setHeight(rect.getHeight() / 2);
			//primaryStage.setFullScreen(true);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }*/
    }

    private HBox controlBar(Stage stage) {
        //control bar layout
        HBox hBox = new HBox();
        Label idLabel = new Label("ID:");
        TextField idTextField = new TextField();
        idTextField.setMinWidth(30);
        idTextField.setMaxWidth(90);
        hBox.setPadding(new Insets(20, 20, 20, 20));
        hBox.setSpacing(5);
        hBox.getChildren().addAll(idLabel, idTextField);
        return hBox;
    }

    public static void main(String[] args) {
        launch();
    }
}