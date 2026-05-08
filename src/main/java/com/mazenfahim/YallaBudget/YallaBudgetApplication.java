package com.mazenfahim.YallaBudget;

import com.mazenfahim.YallaBudget.model.BudgetModel;
import com.mazenfahim.YallaBudget.model.PinModel;
import com.mazenfahim.YallaBudget.model.SQLiteDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX application that initializes the database and selects the first view.
 */
public class YallaBudgetApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SQLiteDatabase.createTables();

        PinModel pinModel = new PinModel();
        BudgetModel budgetModel = new BudgetModel();

        String initialView;
        if (pinModel.userExists()) {
            initialView = "PinUnlockView.fxml";
        } else if (budgetModel.cycleExists()) {
            initialView = "PinUnlockView.fxml";
        } else {
            initialView = "PinSetupView.fxml";
        }

        FXMLLoader fxmlLoader = new FXMLLoader(YallaBudgetApplication.class.getResource(initialView));
        Scene scene = new Scene(fxmlLoader.load(), 860, 600);
        stage.setTitle("Yalla Budget");
        stage.setScene(scene);
        stage.show();
    }
}
