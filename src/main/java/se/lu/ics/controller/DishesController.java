/**
 * The DishesController class is responsible for handling the display of dishes
 * and navigating back to the primary view in a JavaFX application.
 * 
 * The class contains the following methods:
 * 
 * - displayDishes(): Fetches dish data from the database using a stored procedure
 *   and displays it in a ListView. It clears the ListView before adding new items.
 *   Each dish's name, price, type, and stock are displayed.
 * 
 * - handleBackButton(): Handles the action of the back button. It loads the primary
 *   view FXML file and sets it as the current scene in the stage. The stage's width
 *   and height are also set.
 * 
 * The class uses the following imports:
 * - se.lu.ics.data.Data: For database connection.
 * - javafx.fxml.FXML: For FXML annotations.
 * - javafx.scene.control.ListView: For displaying the list of dishes.
 * - javafx.stage.Stage: For managing the application window.
 * - javafx.scene.Parent: For the root node of the scene graph.
 * - javafx.scene.Scene: For the scene to be displayed.
 * - javafx.fxml.FXMLLoader: For loading FXML files.
 * - java.io.IOException: For handling IO exceptions.
 * - java.sql.CallableStatement: For executing stored procedures.
 * - java.sql.Connection: For database connection.
 * - java.sql.ResultSet: For handling the result set from the database.
 * - java.sql.SQLException: For handling SQL exceptions.
 */

//------------------------------------------------------- IMPORTS

package se.lu.ics.controller;

import se.lu.ics.data.Data;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

//-------------------------------------------------------IMPORTS

//-------------------------------------------------------------------------------------------------CLASS

public class DishesController {

    @FXML
    private ListView<String> dishesListView;

    public void displayDishes() {
        try (Connection connection = Data.getConnection()) {
            try (CallableStatement callableStatement = connection.prepareCall("{CALL uspPrintDishes}")) {
                ResultSet resultSet = callableStatement.executeQuery();

                dishesListView.getItems().clear();

                while (resultSet.next()) {
                    String dishName = resultSet.getString("DishName");
                    String dishPrice = resultSet.getString("DishPrice");
                    String dishType = resultSet.getString("DishType");
                    String dishStock = resultSet.getString("DishStock");

                    dishesListView.getItems().add("Name: " + dishName);
                    dishesListView.getItems().add("Price: " + dishPrice + " kr");
                    dishesListView.getItems().add("Type: " + dishType);
                    dishesListView.getItems().add("Stock: " + dishStock + " units");

                    dishesListView.getItems().add("");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/view/primary.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) dishesListView.getScene().getWindow();
            stage.setWidth(640);
            stage.setHeight(480);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// -------------------------------------------------------------------------------------------------CLASS
