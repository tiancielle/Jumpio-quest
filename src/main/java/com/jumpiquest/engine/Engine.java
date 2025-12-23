package com.jumpiquest.engine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jumpiquest.main.ScoreManager;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
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
    // Last checkpoint X position (updated while player is on ground)
    private double lastCheckpointX = 0;
    private Image backgroundImage;
    private Stage stage; // reference to primary stage
    private Pane rootPane = null; // UI root to add ImageView hearts
    private HeartManager heartManager = null;
    private int lastLives = -1;
    // House / entry state
    private boolean blockInput = false;
    private boolean enteringHouse = false;
    private double enterTimer = 0.0;
    private double playerAlpha = 1.0;

    public Engine(Canvas canvas, ScoreManager scoreManager, Stage stage) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.player = new Player(100, 400);
        // initialize checkpoint to player's starting X
        this.lastCheckpointX = this.player.x;
        this.level = new Level();
        this.scoreManager = scoreManager;
        this.stage = stage;
        this.hud = new HUD(player, scoreManager);
        loadBackgroundImage();
    }

    /**
     * Overloaded constructor that accepts a root Pane so UI ImageView hearts can be added.
     */
    public Engine(Canvas canvas, ScoreManager scoreManager, Stage stage, Pane root) {
        this(canvas, scoreManager, stage);
        this.rootPane = root;
        try {
            this.heartManager = new HeartManager(player, rootPane);
            this.lastLives = player.getLives();
        } catch (Exception e) {
            System.out.println("Could not initialize HeartManager: " + e.getMessage());
        }
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
        // Button positions and sizes (centered, bottom) - MUST MATCH renderEndScreen
        double centerX = canvas.getWidth() / 2.0;
        double buttonW = 150;
        double buttonH = 50;
        double spacing = 20;
        double totalWidth = buttonW * 2 + spacing;
        double startX = centerX - totalWidth / 2.0;
        double newGameX = startX;
        double exitX = startX + buttonW + spacing;
        double buttonY = canvas.getHeight() - 200; // MUST MATCH renderEndScreen

        // New Game button (left)
        if (mouseX >= newGameX && mouseX <= newGameX + buttonW &&
            mouseY >= buttonY && mouseY <= buttonY + buttonH) {
            // Go back to main menu
            stop();
            com.jumpiquest.main.MainMenu.show(stage, new com.jumpiquest.main.ScoreManager());
            return;
        }

        // Exit button (right)
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
            
            // Check collision with player hitbox (not full sprite size)
            if (mob.collidsWith(player.getHitboxLeft(), player.getHitboxTop(), 
                                player.getHitboxWidth(), player.getHitboxHeight())) {
                player.takeDamage();
                if (player.getLives() <= 0) {
                    // stop game and show end screen (loss)
                    showEndScreen(false);
                    return; // bail out of update
                } else {
                    // respawn the player at last checkpoint minus 100px (safety), clamp >= 0
                    double respawnX = Math.max(0, lastCheckpointX - 100.0);
                    player.x = respawnX;
                    // place player on the ground at the respawn X
                    player.y = level.getGroundY() - player.h;
                    player.vx = 0;
                    player.vy = 0;
                    player.onGround = true;
                    // reposition camera to keep player visible
                    cameraX = Math.max(0, player.x - canvas.getWidth() / 3.0);
                }
                break; // only take damage once per frame
            }
        }

        // Check food item collisions and remove collected items
        Iterator<FoodItem> foodIterator = level.foodItems.iterator();
        while (foodIterator.hasNext()) {
            FoodItem food = foodIterator.next();
            if (food.collidsWith(player.getHitboxLeft(), player.getHitboxTop(), 
                                 player.getHitboxWidth(), player.getHitboxHeight())) {
                scoreManager.addPoints(food.value);
                foodIterator.remove();
            }
        }

        // Check for entering the end-house (if present)
        try {
            if (level.house != null) {
                javafx.geometry.Rectangle2D houseBounds = level.house.getBounds();
                double phLeft = player.getHitboxLeft();
                double phTop = player.getHitboxTop();
                double phW = player.getHitboxWidth();
                double phH = player.getHitboxHeight();

                if (!enteringHouse && houseBounds.intersects(phLeft, phTop, phW, phH)) {
                    // begin entering sequence
                    enteringHouse = true;
                    blockInput = true;
                    enterTimer = 0.0;
                    player.vx = 60; // gentle push into house
                    player.vy = 0;
                }

                if (enteringHouse) {
                    // gently advance player toward center of the house
                    double targetHitboxLeft = level.house.x + level.house.width / 2.0 - phW / 2.0;
                    double advanceSpeed = 90.0;
                    double maxAdvance = advanceSpeed * dt;
                    if (player.getHitboxLeft() < targetHitboxLeft) {
                        player.x = Math.min(player.x + maxAdvance, targetHitboxLeft);
                    }
                    enterTimer += dt;
                    playerAlpha = Math.max(0.0, 1.0 - enterTimer / 1.2);

                    // when player sufficiently inside or timer expired, finish level
                    if (player.getHitboxRight() >= level.house.x + level.house.width - 8 || enterTimer > 1.5) {
                        showEndScreen(true);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            // defensive: ignore house detection errors
        }

        // detect falling into hole: if player hitbox center goes below ground level while over a hole
        double centerX = player.getHitboxLeft() + player.getHitboxWidth() / 2.0;
        if (level.isHoleAt(centerX) && player.getHitboxBottom() > level.getGroundY()) {
            // player fell into a hole
            player.takeDamage();
            if (player.getLives() <= 0) {
                // show loss end screen
                showEndScreen(false);
                return;
            } else {
                // respawn the player at last checkpoint minus 100px (safety), clamp >= 0
                double respawnX = Math.max(0, lastCheckpointX - 100.0);
                player.x = respawnX;
                player.y = level.getGroundY() - player.h;
                player.vx = 0;
                player.vy = 0;
                player.onGround = true;
                // reposition camera to keep player visible
                cameraX = Math.max(0, player.x - canvas.getWidth() / 3.0);
            }
        }

        // check for level completion: player reached the end (use hitbox and Level.endPosition)
        try {
            if (level.endPosition != null) {
                double endX = level.endPosition.getX();
                // allow a small offset (~10 px) so reaching slightly before counts
                double offset = 10.0;
                if (player.getHitboxRight() >= endX - offset) {
                    // unified end screen for victory
                    showEndScreen(true);
                    return; // ensure update loop does not continue
                }
            }
        } catch (Exception ex) {
            System.out.println("Error checking level completion: " + ex.getMessage());
            ex.printStackTrace();
        }

        // If HeartManager exists, keep hearts in sync when lives change
        if (heartManager != null) {
            int curLives = player.getLives();
            if (curLives != lastLives) {
                heartManager.updateHearts();
                lastLives = curLives;
            }
        }

        // Save checkpoint while player is on a stable ground position.
        // Only advance checkpoint when player moves forward to avoid regressing it.
        try {
            if (player.onGround && player.x > lastCheckpointX) {
                lastCheckpointX = player.x;
            }
        } catch (Exception e) {
            // defensive: ignore if player state inaccessible
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

        // render player (apply fade during entering sequence)
        if (enteringHouse) {
            gc.setGlobalAlpha(playerAlpha);
            player.render(gc);
            gc.setGlobalAlpha(1.0);
        } else {
            player.render(gc);
        }

        // restore graphics state (undo translation for HUD)
        gc.restore();

        // render HUD on top-left (always visible, not affected by camera)
        hud.render(gc);
        // The end house is rendered as part of the level (world coordinates) and
        // should not be re-positioned here. Do not add an ImageView that follows
        // the camera; the Level.render() draws the house at its world X so it
        // appears at the end of the level when the camera reaches it.
        
        // end screen is shown via EndScreen; do not draw overlay here
    }
    
    private void showEndScreen(boolean win) {
        // mark flags and stop the timer to halt game updates/physics
        gameWon = win;
        gameOver = !win;
        if (timer != null) timer.stop();

        // Persist the current score asynchronously and update high score if needed.
        // Use async write to avoid blocking the JavaFX thread / game loop.
        try {
            scoreManager.saveCurrentScoreAsync();
            scoreManager.updateHighScoreIfNeeded();
        } catch (Exception e) {
            System.out.println("Error saving score on end: " + e.getMessage());
        }
        // show the unified EndScreen on JavaFX thread
        Platform.runLater(() -> {
            try {
                com.jumpiquest.main.EndScreen.showEndScreen(stage, scoreManager, win);
            } catch (Exception e) {
                System.out.println("Error showing EndScreen: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
