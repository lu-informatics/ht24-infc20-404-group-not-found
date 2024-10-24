/**
 * The ReceiptsController class is responsible for managing the display of receipts in the user interface.
 * It interacts with the database to retrieve receipt data and displays it in a formatted manner using JavaFX components.
 * 
 * The class contains the following key methods:
 * 
 * - initialize(): This method is called automatically after the FXML file has been loaded. It calls the displayReceipts() method to populate the UI with receipt data.
 * 
 * - displayReceipts(): This method connects to the database, retrieves receipt data using a stored procedure, and dynamically creates UI components to display each receipt's details.
 *   It handles potential SQL exceptions by displaying an error message in the UI.
 * 
 * - handleBackButton(): This method handles the action of the back button. It loads a different FXML file to navigate back to the primary view.
 * 
 * The class uses various JavaFX components such as VBox, ScrollPane, TextFlow, and HBox to structure and style the receipt information.
 * It also uses the Data class to obtain a database connection.
 */

//-------------------------------------------------------IMPORTS

package se.lu.ics.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import se.lu.ics.data.Data;

//-------------------------------------------------------IMPORTS

//-------------------------------------------------------------------------------------------------CLASS

public class ReceiptsController {

    @FXML
    private VBox receiptsVBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {
        displayReceipts();
    }

    private void displayReceipts() {
        try (Connection conn = Data.getConnection()) {
            if (conn != null) {
                try (CallableStatement stmt = conn.prepareCall("{CALL uspGetOldReceipts()}")) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        int receiptNo = rs.getInt("ReceiptNo");
                        String employeeID = rs.getString("EmployeeID");
                        String receiptDate = rs.getString("ReceiptDate");
                        String employeeName = rs.getString("EmployeeName");
                        String drinkName = rs.getString("DrinkName");
                        String dishName = rs.getString("DishName");
                        BigDecimal drinkPrice = rs.getBigDecimal("DrinkPrice");
                        BigDecimal dishPrice = rs.getBigDecimal("DishPrice");
                        BigDecimal totalPrice = rs.getBigDecimal("TotalPrice");

                        TextFlow receiptFlow = new TextFlow();
                        receiptFlow.setStyle(
                                "-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-border-style: solid; "
                                        +
                                        "-fx-background-color: white;");
                        receiptFlow.setPrefWidth(580);

                        Text receiptNoText = new Text("Receipt No: ");
                        receiptNoText.setStyle("-fx-font-weight: bold;");
                        Text receiptNoValue = new Text(String.valueOf(receiptNo) + "\n");

                        Text employeeIDText = new Text("Employee ID: ");
                        employeeIDText.setStyle("-fx-font-weight: bold;");
                        Text employeeIDValue = new Text(employeeID + "\n");

                        Text employeeNameText = new Text("Employee Name: ");
                        employeeNameText.setStyle("-fx-font-weight: bold;");
                        Text employeeNameValue = new Text(employeeName + "\n");

                        Text receiptDateText = new Text("Receipt Date: ");
                        receiptDateText.setStyle("-fx-font-weight: bold;");
                        Text receiptDateValue = new Text(receiptDate + "\n");

                        Text drinkText = new Text("Drink: ");
                        drinkText.setStyle("-fx-font-weight: bold;");
                        Text drinkValue = new Text(drinkName + " (Price: " + drinkPrice + " kr" + ")\n");

                        Text dishText = new Text("Dish: ");
                        dishText.setStyle("-fx-font-weight: bold;");
                        Text dishValue = new Text(dishName + " (Price: " + dishPrice + " kr" + ")\n");

                        Text totalPriceText = new Text("Total Price: ");
                        totalPriceText.setStyle("-fx-font-weight: bold;");
                        Text totalPriceValue = new Text(totalPrice + " kr" + "\n");

                        receiptFlow.getChildren().addAll(
                                receiptNoText, receiptNoValue,
                                employeeIDText, employeeIDValue,
                                employeeNameText, employeeNameValue,
                                receiptDateText, receiptDateValue,
                                drinkText, drinkValue,
                                dishText, dishValue,
                                totalPriceText, totalPriceValue);

                        receiptFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

                        HBox centeredHBox = new HBox(receiptFlow);
                        centeredHBox.setStyle("-fx-alignment: center;");
                        centeredHBox.setPrefWidth(600);
                        centeredHBox.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));

                        receiptsVBox.getChildren().add(centeredHBox);
                    }
                }
            }
        } catch (SQLException e) {
            TextFlow errorFlow = new TextFlow();
            errorFlow.getChildren().add(new Text("Error retrieving receipts: " + e.getMessage()));
            receiptsVBox.getChildren().add(errorFlow);
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/view/Primary.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) receiptsVBox.getScene().getWindow();
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
