package com.mazenfahim.YallaBudget.controller;

import com.mazenfahim.YallaBudget.model.BudgetCycle;
import com.mazenfahim.YallaBudget.model.BudgetModel;
import com.mazenfahim.YallaBudget.model.Category;
import com.mazenfahim.YallaBudget.model.Expense;
import com.mazenfahim.YallaBudget.model.ExpenseModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ExpenseEntryController {
    private ExpenseModel expenseModel = new ExpenseModel();
    private BudgetModel budgetModel = new BudgetModel();

    @FXML
    private TextField amountInput;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setText("");
        List<Category> categories = expenseModel.getCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    @FXML
    public void onSubmitExpenseClicked() {
        try {
            double amount = Double.parseDouble(amountInput.getText().trim());
            Category selectedCategory = categoryComboBox.getValue();
            BudgetCycle cycle = budgetModel.loadCycle();

            if (!expenseModel.validateExpense(amount, selectedCategory)) {
                showValidationError("Please enter a valid amount and select a category.");
                return;
            }

            if (cycle == null) {
                showValidationError("Please create a budget cycle first.");
                return;
            }

            Expense expense = new Expense(amount, selectedCategory, cycle.getId());
            expenseModel.addExpense(cycle, expense);
            navigateTo("DashboardView.fxml");
        } catch (NumberFormatException e) {
            showValidationError("Please enter a valid amount.");
        } catch (Exception e) {
            showValidationError("Could not save the expense.");
        }
    }

    @FXML
    public void onCancelClicked() {
        try {
            navigateTo("DashboardView.fxml");
        } catch (IOException e) {
            showValidationError("Could not return to the dashboard.");
        }
    }

    private void navigateTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) amountInput.getScene().getWindow();
        stage.setScene(scene);
    }

    private void showValidationError(String message) {
        errorLabel.setText(message);
    }
}
