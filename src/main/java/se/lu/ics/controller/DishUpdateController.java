/**
 * This class, DishUpdateController, is responsible for handling the update of dish stock in a database.
 * It provides functionality to update the stock of a dish based on user input from a JavaFX interface.
 * 
 * The class contains the following key components:
 * 
 * - FXML fields: These are the UI components defined in the corresponding FXML file.
 *   - dishNameField: A TextField for entering the name of the dish.
 *   - newDishStockField: A TextField for entering the new stock value for the dish.
 *   - updateDishStockButton: A Button to trigger the stock update process.
 *   - messageLabel: A Label to display messages to the user.
 * 
 * - updateDishStock(): This method is called when the updateDishStockButton is clicked.
 *   It validates the input, checks if the dish exists in the database, and updates the stock if valid.
 * 
 * - doesDishExist(): This helper method checks if a dish exists in the database using a stored procedure.
 * 
 * - closeDialog(): This method closes the current dialog window.
 * 
 * The class uses JDBC to interact with the database and calls stored procedures to perform the necessary operations.
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

public class DishUpdateController {

    @FXML
    private TextField dishNameField;

    @FXML
    private TextField newDishStockField;

    @FXML
    private Button updateDishStockButton;

    @FXML
    private Label messageLabel;

    @FXML
    private void updateDishStock() {
        String dishName = dishNameField.getText().trim();
        String newDishStockStr = newDishStockField.getText().trim();

        if (dishName.isEmpty() || newDishStockStr.isEmpty()) {
            messageLabel.setText("Both Dish Name and New Dish Stock must be filled out.");
            return;
        }

        try {
            int newDishStock = Integer.parseInt(newDishStockStr);

            try (Connection conn = Data.getConnection()) {
                if (conn != null) {
                    if (!doesDishExist(conn, dishName)) {
                        messageLabel.setText("Error: Dish Name does not exist in the database.");
                        return;
                    }

                    try (CallableStatement stmt = conn.prepareCall("{CALL uspUpdateDishStock(?, ?)}")) {
                        stmt.setString(1, dishName);
                        stmt.setInt(2, newDishStock);
                        stmt.execute();

                        messageLabel.setStyle("-fx-text-fill: green;");
                        messageLabel.setText("Dish stock updated successfully!");
                        dishNameField.clear();
                        newDishStockField.clear();
                    }
                } else {
                    messageLabel.setText("Connection error.");
                }
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("New Dish Stock must be a valid integer.");
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean doesDishExist(Connection conn, String dishName) throws SQLException {
        String query = "{CALL uspDoesDishExist(?, ?)}";
        try (CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setString(1, dishName);
            stmt.registerOutParameter(2, java.sql.Types.BIT);
            stmt.execute();
            return stmt.getBoolean(2);
        }
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) dishNameField.getScene().getWindow();
        stage.close();
    }
}

// -------------------------------------------------------------------------------------------------CLASS
