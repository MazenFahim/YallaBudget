module com.mazenfahim.YallaBudget {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mazenfahim.YallaBudget to javafx.fxml;
    exports com.mazenfahim.YallaBudget;
}