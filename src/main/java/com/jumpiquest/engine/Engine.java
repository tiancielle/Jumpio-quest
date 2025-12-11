package com.jumpiquest.engine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jumpiquest.main.ScoreManager;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Engine {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Player player;
    private final Level level;
    private final HUD hud;
    private final ScoreManager scoreManager;
    private final Set<KeyCode> keys = new HashSet<>();
    private AnimationTimer timer;
    private long lastNs = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private double cameraX = 0; // horizontal camera offset in world coords
    private Image backgroundImage;
    private Stage stage; // reference to primary stage

    public Engine(Canvas canvas, ScoreManager scoreManager, Stage stage) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.player = new Player(100, 400);
        this.level = new Level();
        this.scoreManager = scoreManager;
        this.stage = stage;
        this.hud = new HUD(player, scoreManager);
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
        
        // Handle mouse clicks for end screen buttons
        scene.setOnMouseClicked(e -> {
            if (gameOver || gameWon) {
                handleEndScreenClick(e.getX(), e.getY());
            }
        });
    }
    
    private void handleEndScreenClick(double mouseX, double mouseY) {
        // Button positions and sizes
        double centerX = canvas.getWidth() / 2.0;
        double buttonY = canvas.getHeight() / 2.0 + 80;
        double buttonW = 100;
        double buttonH = 100;
        
        // New Game button (left side)
        double newGameX = centerX - 120;
        if (mouseX >= newGameX && mouseX <= newGameX + buttonW && 
            mouseY >= buttonY && mouseY <= buttonY + buttonH) {
            // Go back to main menu
            stop();
            com.jumpiquest.main.MainMenu.show(stage, new com.jumpiquest.main.ScoreManager());
        }
        
        // Exit button (right side)
        double exitX = centerX + 20;
        if (mouseX >= exitX && mouseX <= exitX + buttonW && 
            mouseY >= buttonY && mouseY <= buttonY + buttonH) {
            // Close the game
            System.exit(0);
        }
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

        // Check food item collisions and remove collected items
        Iterator<FoodItem> foodIterator = level.foodItems.iterator();
        while (foodIterator.hasNext()) {
            FoodItem food = foodIterator.next();
            if (food.collidsWith(player.x, player.y, player.w, player.h)) {
                scoreManager.addPoints(food.value);
                foodIterator.remove();
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
            scoreManager.updateHighScoreIfNeeded();
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
        
        // if game over, render an overlay message with buttons
        if (gameOver) {
            renderEndScreen(false);
        }

        // if game won, render a victory message with buttons
        if (gameWon) {
            renderEndScreen(true);
        }
    }
    
    private void renderEndScreen(boolean isWon) {
        // Load button images (static so they load once)
        if (newGameImage == null) loadButtonImages();
        
        // Semi-transparent overlay
        gc.setFill(new Color(0, 0, 0, 0.6));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        double centerX = canvas.getWidth() / 2.0;
        double centerY = canvas.getHeight() / 2.0;
        
        // Title
        gc.setFill(isWon ? Color.GOLD : Color.RED);
        gc.setFont(javafx.scene.text.Font.font(60));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        String title = isWon ? "LEVEL COMPLETE!" : "GAME OVER";
        gc.fillText(title, centerX, centerY - 60);
        
        // Score info
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(30));
        gc.fillText("Score: " + scoreManager.getCurrentScore(), centerX, centerY);
        gc.fillText("Best: " + scoreManager.getHighScore(), centerX, centerY + 40);
        
        // Buttons with images
        double buttonY = centerY + 80;
        double buttonW = 100;
        double buttonH = 100;
        double newGameX = centerX - 120;
        double exitX = centerX + 20;
        
        // New Game button with image
        if (newGameImage != null) {
            gc.drawImage(newGameImage, newGameX, buttonY, buttonW, buttonH);
        } else {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(newGameX, buttonY, buttonW, buttonH);
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font(150));
            gc.fillText("NEW GAME", newGameX + buttonW / 2.0, buttonY + buttonH / 2.0);
        }
        
        // Exit button with image
        if (exitImage != null) {
            gc.drawImage(exitImage, exitX, buttonY, buttonW, buttonH);
        } else {
            gc.setFill(Color.LIGHTCORAL);
            gc.fillRect(exitX, buttonY, buttonW, buttonH);
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font(100));
            gc.fillText("EXIT", exitX + buttonW / 2.0, buttonY + buttonH / 2.0);
        }
    }
    
    private Image newGameImage = null;
    private Image exitImage = null;
    
    private void loadButtonImages() {
        try {
            java.io.File newGameFile = new java.io.File("res/newgame.png");
            if (newGameFile.exists()) {
                newGameImage = new Image(newGameFile.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Could not load newgame.png: " + e.getMessage());
        }
        
        try {
            java.io.File exitFile = new java.io.File("res/exit.png");
            if (exitFile.exists()) {
                exitImage = new Image(exitFile.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Could not load exit.png: " + e.getMessage());
        }
    }
}
