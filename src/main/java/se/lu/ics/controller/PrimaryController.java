/**
 * This class, PrimaryController, is a JavaFX controller that manages the primary user interface of the application.
 * It handles various user interactions such as showing dishes, drinks, confirming orders, and updating stock.
 * The class interacts with the database to validate and process orders, and it uses FXML to load different views.
 * 
 * Key functionalities include:
 * - Displaying dishes and drinks.
 * - Opening dialogs for deleting items, viewing deleted items, and updating stock.
 * - Confirming orders by validating input fields and interacting with the database.
 * - Resetting input fields and error messages.
 * - Checking receipts by loading the appropriate view.
 * 
 * The class uses several private helper methods to check the existence of employees, drinks, and dishes in the database.
 * 
 * The FXML annotations are used to link UI components to the controller.
 * The methods are annotated with @FXML to indicate that they are event handlers for the UI components.
 */

//-------------------------------------------------------IMPORTS

package se.lu.ics.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import se.lu.ics.data.Data;
import java.sql.Types;

//-------------------------------------------------------IMPORTS

//-------------------------------------------------------------------------------------------------CLASS

public class PrimaryController {

    @FXML
    private Button dishButton;

    @FXML
    private Button drinksButton;

    @FXML
    private Button confirmOrderButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button checkReceiptsButton;

    @FXML
    private Button deleteItemButton;

    @FXML
    private Button checkDeletedItemsButton;

    @FXML
    private Button updateStockButton;

    @FXML
    private TextField drinkNameField;

    @FXML
    private TextField dishNameField;

    @FXML
    private TextField employeeIdField;

    @FXML
    private Label errorMessage;

    @FXML
    private Label receiptMessage;

    @FXML
    private void showDishes() {
        System.out.println("Button clicked - Show Dishes");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/view/ShowDishes.fxml"));
            Parent root = loader.load();

            DishesController dishesController = loader.getController();
            dishesController.displayDishes();

            Stage stage = (Stage) dishButton.getScene().getWindow();
            stage.setWidth(640);
            stage.setHeight(480);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDeleteDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/view/DeleteItems.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Delete Item");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(deleteItemButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            // Show the dialog
            dialogStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDeletedItemsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/view/DeletedItems.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Deleted Items");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(checkDeletedItemsButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDishUpdateDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/view/UpdateDishStock.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Dish Stock");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(updateStockButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDrinks() {
        System.out.println("Button clicked - Show Drinks");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/view/ShowDrinks.fxml"));
            Parent root = loader.load();

            DrinkController drinkController = loader.getController();
            drinkController.displayDrinks();

            Stage stage = (Stage) dishButton.getScene().getWindow();
            stage.setWidth(640);
            stage.setHeight(480);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openStockUpdateDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/view/UpdateDrinkStock.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Drink Stock");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(updateStockButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            dialogStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void confirmOrder() {
        String drinkName = drinkNameField.getText().trim();
        String dishName = dishNameField.getText().trim();
        String employeeIdStr = employeeIdField.getText().trim();

        StringBuilder errorMessages = new StringBuilder();

        if (drinkName.isEmpty()) {
            errorMessages.append("Drink Name must be filled out.\n");
        }
        if (dishName.isEmpty()) {
            errorMessages.append("Dish Name must be filled out.\n");
        }
        if (employeeIdStr.isEmpty()) {
            errorMessages.append("Employee ID must be filled out.\n");
        }

        if (!employeeIdStr.matches("\\d+")) {
            errorMessages.append("Invalid Employee ID. Must be a number.\n");
        }

        if (errorMessages.length() > 0) {
            errorMessage.setText(errorMessages.toString());
            receiptMessage.setText("");
            return;
        }

        int employeeId = Integer.parseInt(employeeIdStr);

        try (Connection conn = Data.getConnection()) {
            if (conn != null) {
                if (!doesEmployeeExist(conn, employeeId)) {
                    errorMessages.append("Error: Employee ID does not exist in the database.\n");
                }

                if (!doesDrinkExist(conn, drinkName)) {
                    errorMessages.append("Error: Drink Name does not exist in the database.\n");
                }

                if (!doesDishExist(conn, dishName)) {
                    errorMessages.append("Error: Dish Name does not exist in the database.\n");
                }

                if (errorMessages.length() > 0) {
                    errorMessage.setText(errorMessages.toString());
                    receiptMessage.setText("");
                    return;
                }

                try (CallableStatement stmt = conn.prepareCall("{CALL uspInsertAndPrintReceipt(?, ?, ?)}")) {
                    stmt.setString(1, drinkName.isEmpty() ? null : drinkName);
                    stmt.setString(2, dishName.isEmpty() ? null : dishName);
                    stmt.setInt(3, employeeId);

                    boolean hasResults = stmt.execute();

                    if (hasResults) {
                        ResultSet rs = stmt.getResultSet();
                        while (rs.next()) {
                            receiptMessage.setText("Order confirmed! Receipt No: " + rs.getInt("ReceiptNo") +
                                    ", Total Price: " + rs.getBigDecimal("TotalPrice"));
                            errorMessage.setText("");
                        }
                    } else {
                        receiptMessage.setText("Order confirmed!");
                        errorMessage.setText("");
                    }
                }
            } else {
                errorMessage.setText("Connection is null.");
                receiptMessage.setText("");
            }
        } catch (SQLException e) {
            errorMessage.setText("Error: " + e.getMessage());
            receiptMessage.setText("");
            e.printStackTrace();
        }
    }

    @FXML
    private void checkReceipts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/view/CheckReceipt.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) checkReceiptsButton.getScene().getWindow();
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resetFields() {
        drinkNameField.clear();
        dishNameField.clear();
        employeeIdField.clear();

        errorMessage.setText("");
        receiptMessage.setText("");
    }

    private boolean doesEmployeeExist(Connection conn, int employeeId) throws SQLException {
        String query = "{CALL uspDoesEmployeeExist(?, ?)}";
        try (CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, employeeId);
            stmt.registerOutParameter(2, Types.BIT);
            stmt.execute();
            return stmt.getBoolean(2);
        }
    }

    private boolean doesDrinkExist(Connection conn, String drinkName) throws SQLException {
        String query = "{CALL uspDoesDrinkExist(?, ?)}";
        try (CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setString(1, drinkName);
            stmt.registerOutParameter(2, Types.BIT);
            stmt.execute();
            return stmt.getBoolean(2);
        }
    }

    private boolean doesDishExist(Connection conn, String dishName) throws SQLException {
        String query = "{CALL uspDoesDishExist(?, ?)}";
        try (CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setString(1, dishName);
            stmt.registerOutParameter(2, Types.BIT);
            stmt.execute();
            return stmt.getBoolean(2);
        }
    }
}

// -------------------------------------------------------------------------------------------------CLASS
