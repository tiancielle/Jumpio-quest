package com.jumpiquest.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Simple SQLite helper for storing scores locally.
 * Uses JDBC URL: jdbc:sqlite:jumpioquest.db
 */
public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:jumpioquest.db";

    /** Create DB file and table if not exists. */
    public void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS scores ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "value INTEGER NOT NULL,"
                + "date TEXT NOT NULL"
                + ");";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Database init error: " + e.getMessage());
        }
    }

    /**
     * Save a score synchronously. Use from background thread if you don't want to block UI.
     */
    public void saveScore(int score) {
        String insert = "INSERT INTO scores(value, date) VALUES(?, datetime('now'))";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving score: " + e.getMessage());
        }
    }

    /**
     * Return the highest score stored in DB, or 0 if none.
     */
    public int getHighScore() {
        String query = "SELECT MAX(value) as maxv FROM scores";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("maxv");
            }
        } catch (SQLException e) {
            System.out.println("Error reading high score: " + e.getMessage());
        }
        return 0;
    }
}
