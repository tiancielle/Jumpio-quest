package com.jumpiquest.engine;

import java.io.File;
import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class FoodItem {
    public double x;
    public double y;
    public final double w = 64; // width (increased to 64x64)
    public final double h = 64; // height (increased to 64x64)
    public final int value = 10; // points awarded when collected
    
    private Image foodImage;
    private static final String[] FOOD_IMAGES = {
        "carrot.png",
        "strawberry.png",
        "mushroom.png",
        "apple.png",
        "salade.png",
        "tomato.png",
        "meat.png",
        "banana.png",
        "cheese.png"
    };

    public FoodItem(double x, double y) {
        this.x = x;
        this.y = y;
        this.foodImage = loadRandomFoodImage();
    }

    /**
     * Load a random food image from the /res/ folder.
     * Tries to load from assets/img/ first, then falls back to res/
     */
    private Image loadRandomFoodImage() {
        Random rnd = new Random();
        String imageName = FOOD_IMAGES[rnd.nextInt(FOOD_IMAGES.length)];
        
        try {
            // Try loading from assets/img/ folder first
            File assetsDir = new File("assets/img/" + imageName);
            if (assetsDir.exists()) {
                return new Image(assetsDir.toURI().toString());
            }
            
            // Fallback to res/ folder
            File resDir = new File("res/foodItem/" + imageName);
            if (resDir.exists()) {
                return new Image(resDir.toURI().toString());
            }
            
            System.out.println("Food image not found: " + imageName);
            return null;
        } catch (Exception e) {
            System.out.println("Could not load food image " + imageName + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Render the food item as an image on the canvas.
     * Falls back to a colored rectangle if image is not available.
     */
    public void render(GraphicsContext gc) {
        if (foodImage != null) {
            // Draw the food image at 64x64
            gc.drawImage(foodImage, x, y, w, h);
        } else {
            // Fallback: render as gold rectangle (64x64)
            gc.setFill(Color.GOLD);
            gc.fillRect(x, y, w, h);
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(2);
            gc.strokeRect(x, y, w, h);
        }
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
