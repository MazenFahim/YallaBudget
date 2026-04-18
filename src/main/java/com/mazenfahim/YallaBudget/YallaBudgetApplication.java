package com.mazenfahim.YallaBudget;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class YallaBudgetApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(YallaBudgetApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 860, 600);
        stage.setTitle("Yalla Budget");
        stage.setScene(scene);
        stage.show();
    }
}
