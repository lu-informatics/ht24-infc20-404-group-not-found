/**
 * This class, StockUpdateController, is responsible for handling the update of drink stock in a database.
 * It interacts with the user interface elements and performs database operations to update the stock of a specified drink.
 * 
 * The main functionalities include:
 * - Updating the stock of a drink based on user input.
 * - Validating user input to ensure both drink name and new stock are provided.
 * - Checking if the specified drink exists in the database before attempting to update its stock.
 * - Displaying appropriate messages to the user based on the outcome of the operations.
 * - Closing the dialog window when requested.
 * 
 * The class uses JavaFX for the user interface and JDBC for database interactions.
 * It relies on stored procedures in the database to perform the necessary operations.
 * 
 * Key methods:
 * - updateDrinkStock(): Handles the logic for updating the drink stock.
 * - doesDrinkExist(Connection conn, String drinkName): Checks if a drink exists in the database.
 * - closeDialog(): Closes the dialog window.
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

// -------------------------------------------------------------------------------------------------CLASS

public class StockUpdateController {

    @FXML
    private TextField drinkNameField;

    @FXML
    private TextField newDrinkStockField;

    @FXML
    private Button updateStockButton;

    @FXML
    private Label messageLabel;

    @FXML
    private void updateDrinkStock() {
        String drinkName = drinkNameField.getText().trim();
        String newDrinkStockStr = newDrinkStockField.getText().trim();

        if (drinkName.isEmpty() || newDrinkStockStr.isEmpty()) {
            messageLabel.setText("Both Drink Name and New Drink Stock must be filled out.");
            return;
        }

        try {
            int newDrinkStock = Integer.parseInt(newDrinkStockStr);

            try (Connection conn = Data.getConnection()) {
                if (conn != null) {
                    if (!doesDrinkExist(conn, drinkName)) {
                        messageLabel.setText("Error: Drink Name does not exist in the database.");
                        return;
                    }

                    try (CallableStatement stmt = conn.prepareCall("{CALL uspUpdateDrinkStock(?, ?)}")) {
                        stmt.setString(1, drinkName);
                        stmt.setInt(2, newDrinkStock);
                        stmt.execute();

                        messageLabel.setStyle("-fx-text-fill: green;");
                        messageLabel.setText("Drink stock updated successfully!");
                        drinkNameField.clear();
                        newDrinkStockField.clear();
                    }
                } else {
                    messageLabel.setText("Connection error.");
                }
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("New Drink Stock must be a valid integer.");
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean doesDrinkExist(Connection conn, String drinkName) throws SQLException {
        String query = "{CALL uspDoesDrinkExist(?, ?)}";
        try (CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setString(1, drinkName);
            stmt.registerOutParameter(2, java.sql.Types.BIT);
            stmt.execute();
            return stmt.getBoolean(2);
        }
    }

    @FXML
    private void closeDialog() {
        // Close the dialog window
        Stage stage = (Stage) drinkNameField.getScene().getWindow();
        stage.close();
    }
}

// -------------------------------------------------------------------------------------------------CLASS
