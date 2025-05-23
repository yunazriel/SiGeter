package com.sigeter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = loadFXML("MainView");
        scene = new Scene(root);
        
//        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
//        double screenWidth = screenBounds.getWidth();
//        double screenHeight = screenBounds.getHeight();
//        
//        System.out.println("Lebar layar: " + screenWidth);
//        System.out.println("Tinggi layar: " + screenHeight);
        
        stage.setScene(scene);
        stage.setTitle("SiGeter - Sistem Informasi Gempa Terkini");
        stage.setMaximized(true);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
