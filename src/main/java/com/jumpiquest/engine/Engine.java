package com.jumpiquest.engine;

import java.util.HashSet;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Engine {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Player player;
    private final Level level;
    private final HUD hud;
    private final Set<KeyCode> keys = new HashSet<>();
    private AnimationTimer timer;
    private long lastNs = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private double cameraX = 0; // horizontal camera offset in world coords
    private Image backgroundImage;

    public Engine(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.player = new Player(100, 400);
        this.level = new Level();
        this.hud = new HUD(player);
        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        try {
            // Load background image from resources
            backgroundImage = new Image(getClass().getResourceAsStream("/landscape.jpg"));
        } catch (Exception e) {
            // If image not found, leave backgroundImage null and render default color
            System.out.println("Background image not found: " + e.getMessage());
            backgroundImage = null;
        }
    }

    public void attachInput(Scene scene) {
        scene.setOnKeyPressed(e -> keys.add(e.getCode()));
        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));
    }

    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNs == 0) lastNs = now;
                double dt = (now - lastNs) / 1e9;
                lastNs = now;
                update(dt);
                render();
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) timer.stop();
    }

    private void update(double dt) {
        if (gameOver || gameWon) return;

        boolean left = keys.contains(KeyCode.LEFT) || keys.contains(KeyCode.A);
        boolean right = keys.contains(KeyCode.RIGHT) || keys.contains(KeyCode.D);
        boolean jump = keys.contains(KeyCode.SPACE) || keys.contains(KeyCode.W) || keys.contains(KeyCode.UP);
        player.update(dt, left, right, jump, level);

        // wall collisions handled by level
        level.handleWallCollisions(player);

        // update mobile obstacles and check collisions
        for (MobileObstacle mob : level.mobileObstacles) {
            mob.update(dt);
            
            // Check collision with player
            if (mob.collidsWith(player.x, player.y, player.w, player.h)) {
                player.takeDamage();
                if (player.getLives() <= 0) {
                    gameOver = true;
                    if (timer != null) timer.stop();
                } else {
                    // respawn the player at spawn point
                    player.x = level.spawnX;
                    player.y = level.spawnY;
                    player.vx = 0;
                    player.vy = 0;
                    player.onGround = true;
                }
                break; // only take damage once per frame
            }
        }

        // detect falling into hole: if player top goes below ground level while over a hole
        double centerX = player.x + player.w / 2.0;
        if (level.isHoleAt(centerX) && player.y > level.getGroundY()) {
            // player fell into a hole
            player.takeDamage();
            if (player.getLives() <= 0) {
                // Game Over: stop the timer and mark flag
                gameOver = true;
                if (timer != null) timer.stop();
            } else {
                // respawn the player at spawn point
                player.x = level.spawnX;
                player.y = level.spawnY;
                player.vx = 0;
                player.vy = 0;
                player.onGround = true;
            }
        }

        // check for level completion: player reached the end
        if (player.x >= level.levelWidth - 100) {
            gameWon = true;
            if (timer != null) timer.stop();
        }
    }

    private void render() {
        // Render background (sky/scenery) - not affected by camera
        gc.setFill(Color.DARKSLATEGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Draw background image if loaded
        if (backgroundImage != null) {
            // Tile the background image across the canvas with parallax effect
            double imageWidth = backgroundImage.getWidth();
            double imageHeight = backgroundImage.getHeight();
            double canvasHeight = canvas.getHeight();
            double canvasWidth = canvas.getWidth();
            
            // Parallax: background moves slower than camera for depth effect
            double parallaxCameraX = cameraX * 0.3; // 30% parallax speed
            double startX = -(parallaxCameraX % imageWidth);
            
            // Fill the entire canvas height with background image tiles
            for (double x = startX; x < canvasWidth; x += imageWidth) {
                gc.drawImage(backgroundImage, x, 0, imageWidth, imageHeight);
            }
        }

        // update camera position to keep player visible
        // keep player roughly centered on screen (at 1/3 from left)
        double targetCameraX = Math.max(0, player.x - canvas.getWidth() / 3.0);
        cameraX = targetCameraX;

        // save graphics state and translate canvas for camera scrolling
        gc.save();
        gc.translate(-cameraX, 0);

        // render level (ground, holes, walls)
        level.render(gc);

        // render player
        player.render(gc);

        // restore graphics state (undo translation for HUD)
        gc.restore();

        // render HUD on top-left (always visible, not affected by camera)
        hud.render(gc);
        
        // if game over, render an overlay message
        if (gameOver) {
            gc.setFill(new Color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(48));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("GAME OVER", canvas.getWidth() / 2.0, canvas.getHeight() / 2.0);
        }

        // if game won, render a victory message
        if (gameWon) {
            gc.setFill(new Color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font(48));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("LEVEL COMPLETE!", canvas.getWidth() / 2.0, canvas.getHeight() / 2.0 - 30);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(24));
            gc.fillText("You reached the end!", canvas.getWidth() / 2.0, canvas.getHeight() / 2.0 + 30);
        }
    }
}
