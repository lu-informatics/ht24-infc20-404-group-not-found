/**
 * Controller class for managing the Deleted Items view in a JavaFX application.
 * This class is responsible for loading deleted items from the database and displaying them in a TableView.
 * It also provides functionality to close the window.
 * 
 * The class uses FXML annotations to bind UI components and initialize them.
 * 
 * Methods:
 * - initialize(): Initializes the TableView and loads deleted items from the database.
 * - loadDeletedItems(): Executes a stored procedure to fetch deleted items from the database and populates the TableView.
 * - closeWindow(): Closes the current window.
 * 
 * FXML Components:
 * - deletedItemsTable: TableView to display the list of deleted items.
 * - itemNameColumn: TableColumn to display the name of each deleted item.
 * 
 * Database Interaction:
 * - The loadDeletedItems() method uses a CallableStatement to execute a stored procedure named 'uspWriteDeletedItems'.
 * - The ResultSet from the stored procedure is used to populate the TableView with deleted items.
 */

//-------------------------------------------------------IMPORTS

package se.lu.ics.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//-------------------------------------------------------IMPORTS

//-------------------------------------------------------------------------------------------------CLASS

public class DeletedItemsController {

    @FXML
    private TableView<ObservableList<String>> deletedItemsTable;

    @FXML
    private TableColumn<ObservableList<String>, String> itemNameColumn;

    private ObservableList<ObservableList<String>> data;

    @FXML
    private void initialize() {
        data = FXCollections.observableArrayList();
        itemNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));

        loadDeletedItems();
    }

    private void loadDeletedItems() {
        String query = "{CALL uspWriteDeletedItems()}";
        try (Connection connection = se.lu.ics.data.Data.getConnection();
                CallableStatement stmt = connection.prepareCall(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("ItemName"));
                data.add(row);
            }

            deletedItemsTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeWindow() {

        Stage stage = (Stage) deletedItemsTable.getScene().getWindow();
        stage.close();
    }
}

// -------------------------------------------------------------------------------------------------CLASS
