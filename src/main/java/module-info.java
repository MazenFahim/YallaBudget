/**
 * Declares the YallaBudget JavaFX module and its exported packages.
 */
module com.mazenfahim.YallaBudget {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.mazenfahim.YallaBudget to javafx.fxml;
    opens com.mazenfahim.YallaBudget.controller to javafx.fxml;

    exports com.mazenfahim.YallaBudget;
    exports com.mazenfahim.YallaBudget.model;
    exports com.mazenfahim.YallaBudget.controller;
}
