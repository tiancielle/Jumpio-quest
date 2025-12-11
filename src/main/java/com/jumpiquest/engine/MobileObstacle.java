package com.jumpiquest.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MobileObstacle {
    public double x;
    public double y;
    public final double w = 100; // width
    public final double h = 80; // height
    public final double speed = 1.5; // pixels per frame (moving left)

    public MobileObstacle(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Update position: move left by speed each frame.
     * @param dt delta time in seconds (unused for now, speed is per-frame)
     */
    public void update(double dt) {
        x -= speed; // move left
    }

    /**
     * Render as a colored rectangle.
     * Later this can be replaced with an image.
     */
    public void render(GraphicsContext gc) {
        gc.setFill(Color.PURPLE); // distinctive color for mobile obstacles
        gc.fillRect(x, y, w, h);
        
        // outline for visibility
        gc.setStroke(Color.DARKVIOLET);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, w, h);
    }

    /**
     * Check if this obstacle is completely off-screen (left side).
     * @return true if x + w < 0 (completely off screen to the left)
     */
    public boolean isOffScreen() {
        return x + w < 0;
    }

    /**
     * Check collision with a player (AABB collision).
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
