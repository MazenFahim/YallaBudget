module com.mazenfahim.YallaBudget {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mazenfahim.YallaBudget to javafx.fxml;
    opens com.mazenfahim.YallaBudget.controller to javafx.fxml;

    exports com.mazenfahim.YallaBudget;
    exports com.mazenfahim.YallaBudget.model;
    exports com.mazenfahim.YallaBudget.controller;
}
