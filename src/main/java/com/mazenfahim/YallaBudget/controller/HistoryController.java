package com.mazenfahim.YallaBudget.controller;

import com.mazenfahim.YallaBudget.model.BudgetCycle;
import com.mazenfahim.YallaBudget.model.BudgetModel;
import com.mazenfahim.YallaBudget.model.Category;
import com.mazenfahim.YallaBudget.model.Expense;
import com.mazenfahim.YallaBudget.model.ExpenseModel;
import com.mazenfahim.YallaBudget.model.HistoryModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryController {
    private ExpenseModel expenseModel = new ExpenseModel();
    private HistoryModel historyModel = new HistoryModel(expenseModel);
    private BudgetModel budgetModel = new BudgetModel();
    private Map<String, Integer> categoryNameToId = new HashMap<>();

    @FXML
    private TableView<Expense> expenseTableView;

    @FXML
    private ComboBox<String> categoryFilterComboBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Button deleteButton;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        setupTableColumns();
        populateCategoryFilter();
        loadAndDisplayExpenses();
        deleteButton.disableProperty().bind(expenseTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    private void setupTableColumns() {
        TableColumn<Expense, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().toLocalDate().toString()));

        TableColumn<Expense, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory().getCategoryName()));

        TableColumn<Expense, String> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f EGP", data.getValue().getAmount())));

        expenseTableView.getColumns().setAll(dateColumn, categoryColumn, amountColumn);
    }

    private void populateCategoryFilter() {
        categoryNameToId.clear();
        categoryFilterComboBox.getItems().clear();
        categoryFilterComboBox.getItems().add("All Categories");

        for (Category category : expenseModel.getCategories()) {
            categoryNameToId.put(category.getCategoryName(), category.getId());
            categoryFilterComboBox.getItems().add(category.getCategoryName());
        }

        categoryFilterComboBox.setValue("All Categories");
    }

    private void loadAndDisplayExpenses() {
        BudgetCycle cycle = budgetModel.loadCycle();
        if (cycle == null) {
            expenseTableView.setItems(FXCollections.observableArrayList());
            messageLabel.setText("No active budget cycle.");
            return;
        }

        List<Expense> expenses = historyModel.getTransactions(cycle.getId());

        String selectedCategory = categoryFilterComboBox.getValue();
        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            Integer categoryId = categoryNameToId.get(selectedCategory);
            if (categoryId != null) {
                expenses = historyModel.filterByCategory(expenses, categoryId);
            }
        }

        expenses = historyModel.filterByDateRange(expenses, fromDatePicker.getValue(), toDatePicker.getValue());
        expenses = historyModel.sortByLatest(expenses);

        expenseTableView.setItems(FXCollections.observableArrayList(expenses));
        messageLabel.setText(expenses.isEmpty() ? "No transactions found." : "");
    }

    @FXML
    public void onFilterChanged() {
        loadAndDisplayExpenses();
    }

    @FXML
    public void onDeleteExpenseClicked() {
        Expense selectedExpense = expenseTableView.getSelectionModel().getSelectedItem();
        if (selectedExpense == null) {
            messageLabel.setText("Please select an expense first.");
            return;
        }

        try {
            expenseModel.deleteExpense(selectedExpense);
            loadAndDisplayExpenses();
        } catch (Exception e) {
            messageLabel.setText("Could not delete the expense.");
        }
    }

    @FXML
    public void onBackClicked() {
        try {
            navigateTo("DashboardView.fxml");
        } catch (IOException e) {
            messageLabel.setText("Could not return to the dashboard.");
        }
    }

    private void navigateTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mazenfahim/YallaBudget/" + fxmlFile));
        Scene scene = new Scene(loader.load(), 900, 680);
        Stage stage = (Stage) expenseTableView.getScene().getWindow();
        stage.setScene(scene);
    }
}
