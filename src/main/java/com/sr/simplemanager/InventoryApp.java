package com.sr.simplemanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class InventoryApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("InventoryApp.fxml"));
        // Obtener las dimensiones de la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Establecer el tamaño de la ventana principal a las dimensiones de la pantalla
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        primaryStage.setTitle("Inventory App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}