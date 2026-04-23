package com.aabhushan.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Properties props = new Properties();
            try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    System.out.println("Sorry, unable to find config.properties");
                } else {
                    props.load(input);
                    URL = props.getProperty("db.url");
                    USER = props.getProperty("db.user");
                    PASSWORD = props.getProperty("db.password");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (URL == null) {
            // Fallback for safety or direct testing
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/jewellery", "root", "Love@novelbook21");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // Test connection
    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                System.out.println("Connection Successful!");
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
