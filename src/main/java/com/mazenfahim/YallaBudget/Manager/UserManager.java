package com.mazenfahim.YallaBudget.Manager;

import com.mazenfahim.YallaBudget.Model.BudgetCycle;
import com.mazenfahim.YallaBudget.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    public boolean userExist() {
        String sql="SELECT 1 FROM user WHERE id=1";
        try(Connection connection=SQLiteManager.connect();
            PreparedStatement stmt=connection.prepareStatement(sql);
            ResultSet rs= stmt.executeQuery()){
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveUser(User user){
        String sql="INSERT INTO user(id,username,pin)" +
                "Values(?,?,?)";
        try (Connection conn = SQLiteManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1,1);
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getPIN());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User loadUser(){
        String sql="SELECT * FROM user where id=1";
        User user = null;

        try(Connection connection=SQLiteManager.connect();
            PreparedStatement stmt=connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            if(rs.next()){
                user=new User(
                        rs.getString("username"),
                        rs.getString("pin")
                );
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public void updateuser(User user){
        String sql="UPDATE user SET pin=? WHERE id=1";
        try (Connection conn = SQLiteManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getPIN());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
