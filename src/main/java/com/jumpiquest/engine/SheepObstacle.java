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
    // Shared images for variety
    private static Image sheepImage = null;
    private static Image chickenImage = null;
    private static Image cowImage = null;

    private final Image image; // chosen image for this instance
    private final int levelWidth;
    private final double speed; // pixels per second
    // Visual render size (independent from collision hitbox)
    private final double visualHeight = 120.0; // tweakable
    // Vertical visual offset (pixels). Positive values move image down, negative move up.
    // We want the animal to float 15px above the hitbox: use -15.
    private final double visualOffsetY = -15.0;

    public SheepObstacle(double x, double y, int levelWidth) {
        super(x, y);
        this.levelWidth = levelWidth;
        this.speed = 140.0; // smooth constant speed (px/sec)
        loadImages();
        // pick a random image for visual variety
        int pick = ThreadLocalRandom.current().nextInt(3);
        Image chosen = null;
        if (pick == 0) chosen = sheepImage;
        else if (pick == 1) chosen = chickenImage;
        else chosen = cowImage;
        this.image = chosen;
    }

    private void loadImage() {
        // kept for backward compatibility, but not used
    }

    private void loadImages() {
        if (sheepImage != null || chickenImage != null || cowImage != null) return;
        try {
            File fs = new File("res/MobileObstacles/sheep.gif");
            if (fs.exists()) sheepImage = new Image(fs.toURI().toString());
        } catch (Exception e) {
            System.out.println("Could not load sheep sprite: " + e.getMessage());
            sheepImage = null;
        }
        try {
            File fc = new File("res/MobileObstacles/chicken.png");
            if (fc.exists()) chickenImage = new Image(fc.toURI().toString());
        } catch (Exception e) {
            System.out.println("Could not load chicken sprite: " + e.getMessage());
            chickenImage = null;
        }
        try {
            File fv = new File("res/MobileObstacles/cow.gif");
            if (fv.exists()) cowImage = new Image(fv.toURI().toString());
        } catch (Exception e) {
            System.out.println("Could not load cow sprite: " + e.getMessage());
            cowImage = null;
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
        // Draw the visual image centered on the collision hitbox.
        if (image != null) {
            double imgW = image.getWidth();
            double imgH = image.getHeight();
            // scale to maintain aspect ratio based on desired visualHeight
            double scale = visualHeight / imgH;
            double drawW = imgW * scale;
            double drawH = imgH * scale;
            // center on hitbox (x,y is top-left of hitbox with width w and height h)
            double centerX = x + w / 2.0;
            double centerY = y + h / 2.0;
            double drawX = centerX - drawW / 2.0;
            double drawY = centerY - drawH / 2.0 + visualOffsetY;
            gc.drawImage(image, drawX, drawY, drawW, drawH);
        } else {
            // fallback: draw collision rectangle
            super.render(gc);
        }
    }
}
