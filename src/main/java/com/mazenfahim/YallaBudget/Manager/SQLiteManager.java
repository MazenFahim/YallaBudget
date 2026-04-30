package com.mazenfahim.YallaBudget.Manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SQLiteManager {
    private static final String URL = "jdbc:sqlite:yallabudget.db";
    public void createTables(){
    String query="""
            CREATE TABLE IF NOT EXISTS budget_cycle (
            id INTEGER PRIMARY KEY ,
            total_allowance REAL,
            start_date TEXT,
            end_date TEXT,
            remaining_balance REAL
    );
    CREATE TABLE IF NOT EXISTS user(
    id INTEGER PRIMARY KEY,
    username TEXT,
    pin TEXT
    );
    
    CREATE TABLE IF NOT EXISTS category (
            id INTEGER PRIMARY KEY,
            name TEXT,
            description TEXT);

    CREATE TABLE IF NOT EXISTS expense (  
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            amount REAL,
            category_id INTEGER,
            cycle_id INTEGER,
            timestamp TEXT,
            FOREIGN KEY (category_id) REFERENCES category(id),
            FOREIGN KEY (cycle_id) REFERENCES budget_cycle(id)
    );
    """;
}



    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    public void executeQuery(String sql){
    //
    }
}
