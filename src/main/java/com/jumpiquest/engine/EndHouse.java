package com.jumpiquest.engine;

import java.io.File;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * End-level house. Visual object that signals level end and provides collision bounds.
 */
public class EndHouse {
    public double x;
    public double y;
    public final double width;
    public final double height;
    private Image image;
    private ImageView houseView;

    public EndHouse(double x, double y, double width, double height) {
        this.x = x;
        // use provided y exactly (Level calculates ground-aligned y)
        this.y = y;
        this.width = width;
        this.height = height;
        loadImage();
    }

    private void loadImage() {
        try {
            File f = new File("res/house.png");
            if (f.exists()) {
                image = new Image(f.toURI().toString());
            } else {
                // try resource stream fallback
                image = new Image(getClass().getResourceAsStream("/house.png"));
            }
        } catch (Exception e) {
            System.out.println("Could not load house image: " + e.getMessage());
            image = null;
        }
        // create ImageView for scene-graph display if needed (not used by default)
        try {
            houseView = new ImageView(image);
            houseView.setFitWidth(width);
            houseView.setFitHeight(height);
            houseView.setPreserveRatio(false);
            houseView.setOpacity(1.0);
            houseView.setMouseTransparent(true);
        } catch (Exception e) {
            houseView = null;
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            // fallback: draw a simple house rectangle
            gc.setFill(javafx.scene.paint.Color.SADDLEBROWN);
            gc.fillRect(x, y, width, height);
        }
    }

    public ImageView getImageView() {
        return houseView;
    }

    public void setImageViewOpacity(double alpha) {
        if (houseView != null) houseView.setOpacity(alpha);
    }
}
