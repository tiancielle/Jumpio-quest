package com.jumpiquest.engine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * Manages heart ImageViews shown in the HUD.
 * Displays 3 hearts (64x64) and updates images based on player's lives.
 */
public class HeartManager {
    private final Player player;
    private final Pane root;
    private final HBox heartsBox;
    private final ImageView[] hearts = new ImageView[3];
    private Image redHeart;
    private Image emptyHeart;

    public HeartManager(Player player, Pane root) {
        this.player = player;
        this.root = root;
        this.heartsBox = new HBox(8);
        this.heartsBox.setPadding(new Insets(10));
        this.heartsBox.setAlignment(Pos.TOP_LEFT);

        loadImages();

        for (int i = 0; i < hearts.length; i++) {
            ImageView iv = new ImageView();
            iv.setFitWidth(64);
            iv.setFitHeight(64);
            iv.setPreserveRatio(true);
            hearts[i] = iv;
            heartsBox.getChildren().add(iv);
        }

        // Add to root overlay (StackPane / Pane) so it sits above the canvas
        root.getChildren().add(heartsBox);
        // If root is a StackPane, align hearts to top-left; make hearts mouse-transparent
        try {
            javafx.scene.layout.StackPane.setAlignment(heartsBox, Pos.TOP_LEFT);
        } catch (Exception ex) {
            // ignore if not a StackPane
        }
        heartsBox.setMouseTransparent(true);

        updateHearts();
    }

    private void loadImages() {
        // Try loading from assets/img/ then res/ folder on disk
        try {
            java.io.File f1 = new java.io.File("assets/img/redheart.png");
            if (f1.exists()) {
                redHeart = new Image(f1.toURI().toString());
            } else {
                java.io.File f2 = new java.io.File("res/redheart.png");
                if (f2.exists()) redHeart = new Image(f2.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Could not load redheart.png: " + e.getMessage());
        }

        try {
            java.io.File f1 = new java.io.File("assets/img/emptyheart.png");
            if (f1.exists()) {
                emptyHeart = new Image(f1.toURI().toString());
            } else {
                java.io.File f2 = new java.io.File("res/emptyheart.png");
                if (f2.exists()) emptyHeart = new Image(f2.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Could not load emptyheart.png: " + e.getMessage());
        }
        // If images are still null, generate simple heart images so hearts are visible
        if (redHeart == null || emptyHeart == null) {
            generateFallbackHearts();
        }
    }

    private void generateFallbackHearts() {
        try {
            int size = 48;
            javafx.scene.canvas.Canvas c1 = new javafx.scene.canvas.Canvas(size, size);
            javafx.scene.canvas.GraphicsContext gc1 = c1.getGraphicsContext2D();
            gc1.setFill(javafx.scene.paint.Color.RED);
            drawHeartShape(gc1, 4, 4, size - 8, true);
            javafx.scene.image.WritableImage wi1 = new javafx.scene.image.WritableImage(size, size);
            c1.snapshot(null, wi1);
            redHeart = wi1;

            javafx.scene.canvas.Canvas c2 = new javafx.scene.canvas.Canvas(size, size);
            javafx.scene.canvas.GraphicsContext gc2 = c2.getGraphicsContext2D();
            gc2.setFill(javafx.scene.paint.Color.GRAY);
            drawHeartShape(gc2, 4, 4, size - 8, true);
            javafx.scene.image.WritableImage wi2 = new javafx.scene.image.WritableImage(size, size);
            c2.snapshot(null, wi2);
            emptyHeart = wi2;
        } catch (Exception e) {
            System.out.println("Failed to generate fallback hearts: " + e.getMessage());
        }
    }

    // Simple heart drawing used for fallback images
    private void drawHeartShape(javafx.scene.canvas.GraphicsContext gc, double x, double y, double size, boolean fill) {
        double r = size * 0.25;
        double cx1 = x + r;
        double cy1 = y + r;
        double cx2 = x + r*3;
        double cy2 = y + r;
        double triX1 = x;
        double triY1 = y + r;
        double triX2 = x + size;
        double triY2 = y + r;
        double triX3 = x + size*0.5;
        double triY3 = y + size;
        if (fill) {
            gc.fillOval(cx1 - r, cy1 - r, r*2, r*2);
            gc.fillOval(cx2 - r, cy2 - r, r*2, r*2);
            double[] xs = { triX1, triX3, triX2 };
            double[] ys = { triY1, triY3, triY2 };
            gc.fillPolygon(xs, ys, 3);
        } else {
            gc.strokeOval(cx1 - r, cy1 - r, r*2, r*2);
            gc.strokeOval(cx2 - r, cy2 - r, r*2, r*2);
            double[] xs = { triX1, triX3, triX2 };
            double[] ys = { triY1, triY3, triY2 };
            gc.strokePolygon(xs, ys, 3);
        }
    }

    /**
     * Update the heart images to reflect current player's lives.
     */
    public void updateHearts() {
        int lives = player.getLives();
        for (int i = 0; i < hearts.length; i++) {
            if (i < lives) {
                if (redHeart != null) hearts[i].setImage(redHeart);
                else hearts[i].setImage(null);
            } else {
                if (emptyHeart != null) hearts[i].setImage(emptyHeart);
                else hearts[i].setImage(null);
            }
        }
    }

    /**
     * Remove hearts from the root (cleanup)
     */
    public void remove() {
        root.getChildren().remove(heartsBox);
    }
}
