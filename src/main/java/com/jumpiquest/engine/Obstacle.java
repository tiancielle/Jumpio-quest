package com.jumpiquest.engine;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Obstacle {
    public enum Type { WALL, HOLE, PLATFORM }

    public final Type type;
    public final double x;
    public final double w;
    // only for wall
    public final double h;
    
    // sprite image for walls (tree.png, rock.png, or wood.png)
    private Image spriteImage = null;
    private String spriteName = null;
    // visual size for rendering the sprite (can be larger than hitbox)
    private double visualW = 90;
    private double visualH = 75;

    public Obstacle(Type type, double x, double w, double h) {
        this.type = type;
        this.x = x;
        this.w = w;
        this.h = h;
        
        // if this is a wall, randomly select and load a sprite
        if (type == Type.WALL) {
            loadRandomSprite();
        }
    }
    
    private void loadRandomSprite() {
        String[] sprites = { "tree.png", "rock.png", "wood.png" };
        spriteName = sprites[ThreadLocalRandom.current().nextInt(sprites.length)];
        
        try {
            File f = new File("res/levels/" + spriteName);
            if (f.exists()) {
                spriteImage = new Image(f.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Could not load sprite " + spriteName + ": " + e.getMessage());
            spriteImage = null;
        }
    }

    public void render(GraphicsContext gc, double groundY) {
        if (type == Type.WALL) {
            // Keep the hitbox exactly at (x, groundY - h) with size (w, h).
            // Draw the sprite larger visually, centered on the hitbox and
            // with its bottom aligned to the ground (so it appears to sit on it).
            if (spriteImage != null) {
                double drawX = x + (w - visualW) / 2.0; // center image over hitbox
                double drawY = groundY - visualH; // align image bottom to ground
                gc.drawImage(spriteImage, drawX, drawY, visualW, visualH);
            } else {
                // If sprite missing, draw a visible fallback rectangle sized to the hitbox
                // (this keeps collision behaviour unchanged). In normal operation the
                // sprite files should exist in `res/` so this branch is rarely used.
                gc.setFill(Color.DIMGRAY);
                gc.fillRect(x, groundY - h, w, h);
            }
        } else if (type == Type.HOLE) {
            // draw hole as a black rectangle cut in the ground
            gc.setFill(Color.BLACK);
            gc.fillRect(x, groundY, w, 100);
        } else if (type == Type.PLATFORM) {
            // draw a ground/platform segment
            gc.setFill(Color.DARKGREEN);
            // draw a thicker ground so it looks like the floor
            gc.fillRect(x, groundY, w, 200);
        }
    }
}
