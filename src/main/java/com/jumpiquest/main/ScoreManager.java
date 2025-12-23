package com.jumpiquest.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScoreManager {
    private int currentScore = 0;
    private int highScore = 0;
    private static final String HIGHSCORE_FILE = "highscore.txt";
    private final DatabaseManager db;
    // single threaded executor for DB writes so we never block UI/game loop
    private final ExecutorService dbWriter = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "db-writer");
        t.setDaemon(true);
        return t;
    });

    public ScoreManager() {
        db = new DatabaseManager();
        db.initDatabase();
        // load high score from DB (fallback to file if DB returns 0)
        highScore = db.getHighScore();
        if (highScore <= 0) loadHighScore();
    }

    /**
     * Add points to the current score.
     */
    public void addPoints(int points) {
        currentScore += points;
    }

    /**
     * Reset current score (for a new game).
     */
    public void resetCurrentScore() {
        currentScore = 0;
    }

    /**
     * Get the current score.
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Get the high score.
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Set the high score manually (for testing or initialization).
     */
    public void setHighScore(int score) {
        this.highScore = score;
    }

    /**
     * Load high score from file. If file doesn't exist, start with 0.
     */
    public void loadHighScore() {
        try {
            File file = new File(HIGHSCORE_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                reader.close();
                
                if (line != null && !line.trim().isEmpty()) {
                    try {
                        highScore = Integer.parseInt(line.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Could not parse high score: " + line);
                        highScore = 0;
                    }
                }
            } else {
                highScore = 0;
            }
        } catch (IOException e) {
            System.out.println("Error loading high score: " + e.getMessage());
            highScore = 0;
        }
    }

    /**
     * Save high score to file.
     */
    public void saveHighScore() {
        // write to file (keep legacy behavior)
        try {
            FileWriter writer = new FileWriter(HIGHSCORE_FILE);
            writer.write(String.valueOf(highScore));
            writer.close();
            System.out.println("High score saved (file): " + highScore);
        } catch (IOException e) {
            System.out.println("Error saving high score: " + e.getMessage());
        }
        // also persist to DB asynchronously
        final int toSave = highScore;
        dbWriter.submit(() -> db.saveScore(toSave));
    }

    /**
     * Update high score if current score is higher.
     * @return true if high score was updated
     */
    public boolean updateHighScoreIfNeeded() {
        if (currentScore > highScore) {
            highScore = currentScore;
            saveHighScore();
            return true;
        }
        return false;
    }

    /**
     * Save the current score to DB (asynchronously) without blocking the game loop.
     */
    public void saveCurrentScoreAsync() {
        final int toSave = currentScore;
        dbWriter.submit(() -> db.saveScore(toSave));
    }

    /**
     * Shutdown DB writer executor cleanly (call on application exit if desired).
     */
    public void shutdown() {
        dbWriter.shutdown();
    }
}
