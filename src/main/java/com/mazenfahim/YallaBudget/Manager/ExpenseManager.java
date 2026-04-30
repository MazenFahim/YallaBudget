package com.mazenfahim.YallaBudget.Manager;

import com.mazenfahim.YallaBudget.Model.BudgetCycle;
import com.mazenfahim.YallaBudget.Model.Category;
import com.mazenfahim.YallaBudget.Model.Expense;

import java.time.LocalDate;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
import java.util.ArrayList;



public class ExpenseManager {
public  void saveExpense(Expense expense){
    String sql="INSERT INTO expense(amount,category_id,cycle_id,timestamp)VALUES(?,?,?,?)";

    try (Connection conn = SQLiteManager.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setDouble(1, expense.getAmount());
        stmt.setInt(2, expense.getCategory().getId());
        stmt.setInt(3, 1);
        stmt.setString(4, expense.getTimestamp().toString());

        stmt.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }

}
    public  void updateExpense(Expense expense)
{//
    double oldAmount = expense.getAmount();

    String sql = "UPDATE expense SET amount = ?, category_id = ?, timestamp = ? WHERE id = ? AND cycle_id = 1";

    try (Connection conn = SQLiteManager.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setDouble(1, expense.getAmount());
        stmt.setInt(2, expense.getCategory().getId());
        stmt.setString(3, expense.getTimestamp().toString());
        stmt.setInt(4, expense.getId());

        stmt.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
     }

    public void deleteExpense(Expense expense){

        String sql = "DELETE FROM expense WHERE id = ? AND cycle_id = 1";

        try (Connection conn = SQLiteManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, expense.getId());
            stmt.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        }
}


public   List<Expense> getExpensesByCycle(int cycleId){
    List<Expense> expenses = new ArrayList<>();

    String sql = "SELECT e.*, c.id as cat_id, c.name as cat_name, c.description as cat_desc " +
            "FROM expense e " +
            "JOIN category c ON e.category_id = c.id " +
            "WHERE e.cycle_id = ? " +
            "ORDER BY e.timestamp ASC";
    try (Connection conn = SQLiteManager.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, cycleId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            // Create Category object
            Category category = new Category(
                    rs.getInt("e.category_id"),
                    rs.getString("cat_name"),
                    rs.getString("cat_desc")
            );

            // Create Expense object
            Expense expense = new Expense(
                    rs.getDouble("amount"),
                    category,
                    cycleId
            );
            expense.setId(rs.getInt("id"));
            expense.setTimestamp(LocalDate.from(java.time.LocalDateTime.parse(rs.getString("timestamp"))));

            expenses.add(expense);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return expenses;
}


}

