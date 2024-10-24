/**
 * This is the main class for launching a JavaFX application.
 * 
 * The class extends `Application` and overrides the `start` method to set up the primary stage.
 * It loads an FXML file to create the initial scene and sets the dimensions of the stage.
 * 
 * Methods:
 * - `start(Stage stage)`: This method is the entry point for the JavaFX application. It sets up the primary stage with a scene loaded from an FXML file.
 * - `setRoot(String fxml)`: This method allows changing the root of the scene to a different FXML file.
 * - `loadFXML(String fxml)`: This private method loads an FXML file from the specified path and returns the root node.
 * - `main(String[] args)`: The main method that launches the JavaFX application.
 * 
 * Usage:
 * - Run the `main` method to start the JavaFX application.
 * - The application will load the "Primary.fxml" file located in the `/se/lu/ics/view/` directory.
 * - The primary stage dimensions are set to 720x560 pixels.
 */

//-------------------------------------------------------IMPORTS

package se.lu.ics.launch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

//-------------------------------------------------------IMPORTS

// -------------------------------------------------------------------------------------------------CLASS

public class Launch extends Application { // JavaFX application class

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
        stage.setWidth(720);
        stage.setHeight(560);

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launch.class.getResource("/se/lu/ics/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}

// -------------------------------------------------------------------------------------------------CLASS
