package com.jumpiquest.engine;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * A sheep obstacle that slides left across the ground using a sprite image.
 * It moves at a constant speed (time-based) and respawns on the right side
 * of the level after it leaves the left edge.
 */
public class SheepObstacle extends MobileObstacle {
    private static Image sheepImage = null;
    private final int levelWidth;
    private final double speed; // pixels per second

    public SheepObstacle(double x, double y, int levelWidth) {
        super(x, y);
        this.levelWidth = levelWidth;
        this.speed = 140.0; // smooth constant speed (px/sec)
        loadImage();
    }

    private void loadImage() {
        if (sheepImage != null) return;
        try {
            File f = new File("res/sheep.png");
            if (f.exists()) {
                sheepImage = new Image(f.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Could not load sheep sprite: " + e.getMessage());
            sheepImage = null;
        }
    }

    @Override
    public void update(double dt) {
        // time-based movement for smoothness
        x -= speed * dt;

        // respawn on right side if fully left off-screen
        if (x + w < 0) {
            double offset = ThreadLocalRandom.current().nextDouble(50, 300);
            x = levelWidth + offset;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (sheepImage != null) {
            gc.drawImage(sheepImage, x, y, w, h);
        } else {
            super.render(gc);
        }
    }
}
