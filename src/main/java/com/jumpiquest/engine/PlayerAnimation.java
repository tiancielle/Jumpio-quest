package com.jumpiquest.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.File;

/**
 * Manages player sprite animations and states.
 * Handles running left, running right, idle, and jumping animations.
 */
public class PlayerAnimation {
    private Image[] runRightSprites = new Image[2];    // sprite1, sprite2
    private Image[] runLeftSprites = new Image[2];     // sprite5, sprite6
    private Image idleSprite;                          // sprite3
    private Image jumpSprite;                          // sprite4
    
    // Scaled dimensions (calculated to maintain aspect ratio)
    private double scaledWidth;
    private double scaledHeight;
    
    // Target height for all sprites (width will be calculated to preserve ratio)
    private static final double TARGET_HEIGHT = 150.0;
    
    private enum AnimationState {
        IDLE, RUN_RIGHT, RUN_LEFT, JUMP
    }
    
    private AnimationState currentState = AnimationState.IDLE;
    private AnimationState nextState = AnimationState.IDLE;
    private int currentFrameIndex = 0;
    private double animationTimer = 0.0;
    private double frameDuration = 0.1; // 100ms per frame for smooth animation
    
    private static final String[] SPRITE_PATHS = {
        "res/sprite/sprite1.png",  // 0
        "res/sprite/sprite2.png",  // 1
        "res/sprite/sprite3.png",  // 2 - idle
        "res/sprite/sprite4.png",  // 3 - jump
        "res/sprite/sprite5.png",  // 4
        "res/sprite/sprite6.png"   // 5
    };

    public PlayerAnimation() {
        loadAllSprites();
    }

    /**
     * Load all sprite images from res/sprite/ folder.
     * Calculate scaled dimensions based on original aspect ratio.
     */
    private void loadAllSprites() {
        // Load run right sprites (sprite1, sprite2)
        for (int i = 0; i < 2; i++) {
            try {
                File f = new File(SPRITE_PATHS[i]);
                if (f.exists()) {
                    runRightSprites[i] = new Image(f.toURI().toString());
                    System.out.println("Loaded: " + SPRITE_PATHS[i]);
                } else {
                    System.out.println("Sprite not found: " + f.getAbsolutePath());
                }
            } catch (Exception e) {
                System.out.println("Error loading " + SPRITE_PATHS[i] + ": " + e.getMessage());
            }
        }
        
        // Load idle sprite (sprite3)
        try {
            File f = new File(SPRITE_PATHS[2]);
            if (f.exists()) {
                idleSprite = new Image(f.toURI().toString());
                System.out.println("Loaded: " + SPRITE_PATHS[2]);
            } else {
                System.out.println("Sprite not found: " + f.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Error loading " + SPRITE_PATHS[2] + ": " + e.getMessage());
        }
        
        // Load jump sprite (sprite4)
        try {
            File f = new File(SPRITE_PATHS[3]);
            if (f.exists()) {
                jumpSprite = new Image(f.toURI().toString());
                System.out.println("Loaded: " + SPRITE_PATHS[3]);
            } else {
                System.out.println("Sprite not found: " + f.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Error loading " + SPRITE_PATHS[3] + ": " + e.getMessage());
        }
        
        // Load run left sprites (sprite5, sprite6)
        for (int i = 0; i < 2; i++) {
            try {
                File f = new File(SPRITE_PATHS[4 + i]);
                if (f.exists()) {
                    runLeftSprites[i] = new Image(f.toURI().toString());
                    System.out.println("Loaded: " + SPRITE_PATHS[4 + i]);
                } else {
                    System.out.println("Sprite not found: " + f.getAbsolutePath());
                }
            } catch (Exception e) {
                System.out.println("Error loading " + SPRITE_PATHS[4 + i] + ": " + e.getMessage());
            }
        }
        
        // Calculate scaled dimensions based on idle sprite aspect ratio
        calculateScaledDimensions();
    }
    
    /**
     * Calculate scaled dimensions preserving aspect ratio.
     * Uses sprite1 (runRightSprites[0]) as the reference for uniform sizing.
     * ALL sprites will be displayed at the same size for visual consistency.
     */
    private void calculateScaledDimensions() {
        double originalWidth = 64.0;  // Default fallback
        double originalHeight = 64.0; // Default fallback
        
        // Use sprite1 (runRightSprites[0]) as the reference for consistent sizing
        // This ensures idle, jump, and all animations have the same visual size
        if (runRightSprites[0] != null && runRightSprites[0].getWidth() > 0) {
            originalWidth = runRightSprites[0].getWidth();
            originalHeight = runRightSprites[0].getHeight();
        } else if (idleSprite != null && idleSprite.getWidth() > 0) {
            // Fallback to idle if sprite1 not available
            originalWidth = idleSprite.getWidth();
            originalHeight = idleSprite.getHeight();
        }
        
        // Calculate aspect ratio and scale to target height
        double aspectRatio = originalWidth / originalHeight;
        scaledHeight = TARGET_HEIGHT;
        scaledWidth = TARGET_HEIGHT * aspectRatio;
        
        System.out.println("Reference sprite size: " + originalWidth + "x" + originalHeight);
        System.out.println("Aspect ratio: " + aspectRatio + 
            " | Uniform scaled size: " + scaledWidth + "x" + scaledHeight);
    }

    /**
     * Update animation based on elapsed time.
     * Called once per frame.
     */
    public void update(double dt) {
        // Handle state transitions
        if (!currentState.equals(nextState)) {
            currentState = nextState;
            currentFrameIndex = 0;
            animationTimer = 0.0;
        }

        // Update animation timer
        animationTimer += dt;

        // Update frame based on animation state
        if (currentState == AnimationState.RUN_RIGHT || currentState == AnimationState.RUN_LEFT) {
            // Loop through 2 frames for running animation
            if (animationTimer >= frameDuration) {
                animationTimer -= frameDuration;
                currentFrameIndex = (currentFrameIndex + 1) % 2;
            }
        } else if (currentState == AnimationState.JUMP) {
            // Jump is single frame, no looping
            currentFrameIndex = 0;
        } else { // IDLE
            // Idle is single frame
            currentFrameIndex = 0;
        }
    }

    /**
     * Render the current sprite on the canvas.
     * Automatically scales to preserve aspect ratio.
     * @param gc GraphicsContext to draw on
     * @param x Player X position
     * @param y Player Y position
     */
    public void render(GraphicsContext gc, double x, double y) {
        Image spriteToRender = getCurrentSprite();
        if (spriteToRender != null) {
            gc.drawImage(spriteToRender, x, y, scaledWidth, scaledHeight);
        }
    }

    /**
     * Get the current sprite based on animation state and frame.
     */
    private Image getCurrentSprite() {
        switch (currentState) {
            case RUN_RIGHT:
                return runRightSprites[currentFrameIndex];
            case RUN_LEFT:
                return runLeftSprites[currentFrameIndex];
            case JUMP:
                return jumpSprite;
            case IDLE:
            default:
                return idleSprite;
        }
    }

    // Animation state setters
    public void setStateRunRight() {
        nextState = AnimationState.RUN_RIGHT;
    }

    public void setStateRunLeft() {
        nextState = AnimationState.RUN_LEFT;
    }

    public void setStateJump() {
        nextState = AnimationState.JUMP;
    }

    public void setStateIdle() {
        nextState = AnimationState.IDLE;
    }

    /**
     * Return to idle state after jump.
     */
    public void resetToIdle() {
        nextState = AnimationState.IDLE;
    }

    public AnimationState getCurrentState() {
        return currentState;
    }
    
    /**
     * Get the scaled width of the player sprite.
     */
    public double getScaledWidth() {
        return scaledWidth;
    }
    
    /**
     * Get the scaled height of the player sprite.
     */
    public double getScaledHeight() {
        return scaledHeight;
    }
}
