module su.lu.ics {
    requires javafx.controls;
    requires javafx.fxml;

    opens su.lu.ics to javafx.fxml;
    exports su.lu.ics;
}
