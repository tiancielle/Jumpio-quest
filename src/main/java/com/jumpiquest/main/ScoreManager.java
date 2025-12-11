package com.jumpiquest.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ScoreManager {
    private int currentScore = 0;
    private int highScore = 0;
    private static final String HIGHSCORE_FILE = "highscore.txt";

    public ScoreManager() {
        loadHighScore();
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
        try {
            FileWriter writer = new FileWriter(HIGHSCORE_FILE);
            writer.write(String.valueOf(highScore));
            writer.close();
            System.out.println("High score saved: " + highScore);
        } catch (IOException e) {
            System.out.println("Error saving high score: " + e.getMessage());
        }
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
}
