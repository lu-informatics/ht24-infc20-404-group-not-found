/**
 * The DrinkController class is responsible for managing the display of drinks in a ListView
 * and handling navigation back to the primary view.
 * 
 * Methods:
 * - displayDrinks(): Fetches drink data from the database using a stored procedure and displays it in the ListView.
 * - handleBackButton(): Handles the action of the back button, loading the primary view.
 * 
 * Dependencies:
 * - Data: Provides the database connection.
 * - ListView: JavaFX component to display the list of drinks.
 * - FXMLLoader: Loads the FXML file for the primary view.
 * 
 * Exception Handling:
 * - Catches and prints SQL and IO exceptions.
 */

//-------------------------------------------------------IMPORTS

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

public class DrinkController {

    @FXML
    private ListView<String> drinkListView;

    public void displayDrinks() {
        try (Connection connection = Data.getConnection()) {
            try (CallableStatement callableStatement = connection.prepareCall("{CALL uspPrintDrinks}")) {
                ResultSet resultSet = callableStatement.executeQuery();

                drinkListView.getItems().clear();

                while (resultSet.next()) {
                    String drinkName = resultSet.getString("DrinkName");
                    String drinkPrice = resultSet.getString("DrinkPrice");
                    String drinkType = resultSet.getString("DrinkType");
                    String drinkStock = resultSet.getString("DrinkStock");

                    drinkListView.getItems().add("Name: " + drinkName);
                    drinkListView.getItems().add("Price: " + drinkPrice + " kr");
                    drinkListView.getItems().add("Type: " + drinkType);
                    drinkListView.getItems().add("Stock: " + drinkStock + " units");

                    drinkListView.getItems().add("");
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

            Stage stage = (Stage) drinkListView.getScene().getWindow();
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
