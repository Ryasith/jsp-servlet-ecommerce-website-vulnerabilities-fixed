package com.ecommerce.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Database instance;
    private Connection conn;

    // Private constructor to prevent instantiation from outside
    private Database() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jsp-servlet-ecommerce-website", "root", "root");
        } catch (SQLException e) {
            System.err.println("Failed to establish a database connection: " + e.getMessage());
        }
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }

    public static void main(String[] args) {
        // Usage of the singleton instance
        Database database = Database.getInstance();
        System.out.println(database.getConnection());
    }
}
