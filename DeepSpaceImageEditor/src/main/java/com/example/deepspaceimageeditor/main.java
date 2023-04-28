package com.example.deepspaceimageeditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("DeepSpaceEditor.fxml"));
            Scene scene = new Scene(root);

            // Add the stylesheet to the scene
            String css = this.getClass().getResource("Stylesheet.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setScene(scene); // Add the scene to the stage
            stage.setTitle("Deep Space Editor");
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}