package com.jumpiquest.engine;

import java.io.InputStream;
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
