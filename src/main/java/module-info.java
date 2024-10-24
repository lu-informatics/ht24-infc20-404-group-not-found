module se.lu.ics.mavenjdbc {
    // Allow using JavaFX components
    requires javafx.controls;
    requires javafx.fxml;

    // Allow using SQL Server JDBC driver
    requires transitive java.sql; // This should be transitive

    // Open your package that contains FXML controller or related classes to JavaFX
    opens se.lu.ics.launch to javafx.fxml;

    // Specify the packages in your project
    exports se.lu.ics.launch; // Export launch package
    exports se.lu.ics.data; // Export data package
    exports se.lu.ics.controller; // Export the controller package

    requires transitive javafx.graphics;

    opens se.lu.ics.controller to javafx.fxml; // Allow FXML to access your controller
}
