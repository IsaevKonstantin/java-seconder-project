package ru.otus.java.basic.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/seconder_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "12ShiZZiC97";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
