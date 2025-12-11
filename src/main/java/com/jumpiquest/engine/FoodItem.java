package com.jumpiquest.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FoodItem {
    public double x;
    public double y;
    public final double w = 20; // width
    public final double h = 20; // height
    public final int value = 10; // points awarded when collected

    public FoodItem(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Render as a colored rectangle on the canvas.
     * Color varies by value (simple system for now).
     */
    public void render(GraphicsContext gc) {
        // Use different colors for visual appeal (gold for now)
        gc.setFill(Color.GOLD);
        gc.fillRect(x, y, w, h);
        
        // outline for visibility
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, w, h);
    }

    /**
     * Check collision with player using AABB.
     * @param playerX player's x position
     * @param playerY player's y position
     * @param playerW player's width
     * @param playerH player's height
     * @return true if rectangles overlap
     */
    public boolean collidsWith(double playerX, double playerY, double playerW, double playerH) {
        return playerX < x + w &&
               playerX + playerW > x &&
               playerY < y + h &&
               playerY + playerH > y;
    }
}
