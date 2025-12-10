package com.jumpiquest.main;

public class GameSettings {
    public enum Difficulty { FACILE, MOYEN, DIFFICILE }

    private static Difficulty difficulty = Difficulty.FACILE;
    private static int highScore = 0;

    public static void setDifficulty(Difficulty d) { difficulty = d; }
    public static Difficulty getDifficulty() { return difficulty; }

    public static int getHighScore() { return highScore; }
    public static void setHighScore(int s) { highScore = s; }
}
