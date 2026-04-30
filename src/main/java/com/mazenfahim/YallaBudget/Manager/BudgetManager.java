package com.mazenfahim.YallaBudget.Manager;

import com.mazenfahim.YallaBudget.Model.BudgetCycle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BudgetManager {

    public void saveCycle(BudgetCycle cycle){
        if(cycleExist())
            updateCycle(cycle);
        else
            insertCycle(cycle);
    }

    public void insertCategories(){
        String sql = "INSERT INTO category (name, description) VALUES " +
                "('Food', 'Expenses related to meals, groceries, and dining')," +
                "('Entertainment', 'Movies, games, events, and leisure activities')," +
                "('Transportation', 'Public transport, fuel, taxis, and travel')," +
                "('Shopping', 'Clothes, electronics, and personal items')," +
                "('Health', 'Medical expenses, pharmacy, and wellness')," +
                "('Education', 'Courses, books, and learning materials')";
        try (Connection conn = SQLiteManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertCycle(BudgetCycle cycle){
        String sql="INSERT INTO budget_cycle(id,total_allowance,start_date,end_date,remaining_balance)" +
                "Values(?,?,?,?,?)";
        try (Connection conn = SQLiteManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1,1);
            stmt.setDouble(2, cycle.getTotal_Allowance());
            stmt.setString(3, cycle.getStartDate().toString());
            stmt.setString(4, cycle.getEndDate().toString());
            stmt.setDouble(5, cycle.getRemainingBalance());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BudgetCycle loadCycle(){
        String sql="SELECT * FROM budget_cycle where id=1";
        BudgetCycle cycle = null;

        try(Connection connection=SQLiteManager.connect();
            PreparedStatement stmt=connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            if(rs.next()){
                cycle=new BudgetCycle(
                        rs.getDouble("total_allowance"),
                        java.time.LocalDate.parse(rs.getString("start_date")),
                        java.time.LocalDate.parse(rs.getString("end_date"))
                );
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cycle;
    }

    public void updateCycle(BudgetCycle cycle){
        String sql="UPDATE budget_cycle SET total_allowance=?,start_date=?,end_date=?," +
                "remaining_balance=?WHERE id=1";
        try (Connection conn = SQLiteManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, cycle.getTotal_Allowance());
            stmt.setString(2, cycle.getStartDate().toString());
            stmt.setString(3, cycle.getEndDate().toString());
            stmt.setDouble(4, cycle.getRemainingBalance());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCycle(BudgetCycle Cycle){
        String sql="DELETE From budget_cycle";
        try(Connection connection=SQLiteManager.connect();
        PreparedStatement stmt=connection.prepareStatement(sql)){
            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean cycleExist() {
    String sql="SELECT 1 FROM budget_cycle WHERE id=1";
        try(Connection connection=SQLiteManager.connect();
            PreparedStatement stmt=connection.prepareStatement(sql);
        ResultSet rs= stmt.executeQuery()){
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
