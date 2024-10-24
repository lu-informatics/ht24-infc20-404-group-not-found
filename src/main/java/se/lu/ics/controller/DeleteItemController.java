/**
 * This class, DeleteItemController, is responsible for handling the deletion of dishes and drinks
 * from a database through a JavaFX user interface. It provides methods to delete a dish or a drink
 * based on user input and displays appropriate messages based on the success or failure of the operations.
 * 
 * Uses following components:
 * 
 * Input field for the name of the dish to be deleted.
 * Input field for the name of the drink to be deleted.
 * Button to trigger the deletion of a dish.
 * Button to trigger the deletion of a drink.
 * 
 * Main methods:
 * 
 * - deleteDish(): Deletes a dish from the database using a stored procedure 'uspDeleteDish'.
 * - deleteDrink(): Deletes a drink from the database using a stored procedure 'uspDeleteDrink'.
 * - closeDialog(): Closes the current dialog window.
 * 
 * Each delete method performs the following steps:
 * 1. Retrieves the name of the dish or drink from the corresponding TextField.
 * 2. Checks if the input is empty and displays an error message if it is.
 * 3. Establishes a connection to the database.
 * 4. Calls the appropriate stored procedure to delete the item.
 * 5. Displays a success message if the deletion is successful, or an error message if it fails.
 * 6. Clears the input field after a successful deletion.
 * 
 * The closeDialog() method simply closes the current window.
 */
//-------------------------------------------------------IMPORTS

package se.lu.ics.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import se.lu.ics.data.Data;

//-------------------------------------------------------IMPORTS

//-------------------------------------------------------------------------------------------------CLASS

public class DeleteItemController {

    @FXML
    private TextField dishNameField;

    @FXML
    private TextField drinkNameField;

    @FXML
    private Button deleteDishButton;

    @FXML
    private Button deleteDrinkButton;

    @FXML
    private Label messageLabel;

    @FXML
    private void deleteDish() {
        String dishName = dishNameField.getText().trim();

        if (dishName.isEmpty()) {
            messageLabel.setText("Dish Name must be filled out to delete.");
            return;
        }

        try (Connection conn = Data.getConnection()) {
            if (conn != null) {
                try (CallableStatement stmt = conn.prepareCall("{CALL uspDeleteDish(?)}")) {
                    stmt.setString(1, dishName);
                    stmt.execute();

                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("Dish deleted successfully!");
                    dishNameField.clear();
                }
            } else {
                messageLabel.setText("Connection error.");
            }
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteDrink() {
        String drinkName = drinkNameField.getText().trim();

        if (drinkName.isEmpty()) {
            messageLabel.setText("Drink Name must be filled out to delete.");
            return;
        }

        try (Connection conn = Data.getConnection()) {
            if (conn != null) {
                try (CallableStatement stmt = conn.prepareCall("{CALL uspDeleteDrink(?)}")) {
                    stmt.setString(1, drinkName);
                    stmt.execute();

                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("Drink deleted successfully!");
                    drinkNameField.clear();
                }
            } else {
                messageLabel.setText("Connection error.");
            }
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) dishNameField.getScene().getWindow();
        stage.close();
    }
}

// -------------------------------------------------------------------------------------------------CLASS
