package com.jumpiquest.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    double x, y;
    double vx, vy;
    // Dimensions for rendering (visual sprite size)
    double w, h;
    // Hitbox dimensions (smaller than visual size, for collision detection)
    private double hitboxWidth;
    private double hitboxHeight;
    // Hitbox scale factor: hitbox = sprite * scale
    private static final double HITBOX_SCALE = 0.65;  // 65% of sprite size
    final double speed = 240;
    final double jumpSpeed = 600;
    final double gravity = 1600;
    boolean onGround = false;

    // Lives / invincibility
    private int lives = 3;
    private static final int MAX_LIVES = 3;
    private boolean invincible = false;
    private double invincibilityTimer = 0.0; // seconds remaining
    private double blinkTimer = 0.0; // toggles visibility
    private boolean visible = true; // used for blinking while invincible
    
    // Animation system
    private PlayerAnimation animation;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.animation = new PlayerAnimation();
        // Set player visual dimensions from scaled animation
        this.w = animation.getScaledWidth();
        this.h = animation.getScaledHeight();
        // Calculate reduced hitbox (65% of sprite size, centered on player)
        this.hitboxWidth = w * HITBOX_SCALE;
        this.hitboxHeight = h * HITBOX_SCALE;
    }

    public void update(double dt, boolean left, boolean right, boolean jump, Level level) {
        double ax = 0;
        if (left) ax = -speed;
        if (right) ax = speed;
        vx = ax;

        x += vx * dt;

        vy += gravity * dt;
        y += vy * dt;

        double groundY = level != null ? level.getGroundY() - h : 500 - h;
        double centerX = x + w / 2.0;
        boolean overHole = level != null && level.isHoleAt(centerX);

        if (!overHole && y > groundY) {
            y = groundY;
            vy = 0;
            onGround = true;
        } else {
            onGround = false;
        }

        if (jump && onGround) {
            vy = -jumpSpeed;
            onGround = false;
        }

        // keep player within world bounds when a level is provided
        if (x < 0) x = 0;
        if (level != null) {
            double maxX = Math.max(0, level.levelWidth - w);
            if (x > maxX) x = maxX;
        } else {
            if (x > 800 - w) x = 800 - w;
        }

        // update invincibility / blinking
        if (invincible) {
            invincibilityTimer -= dt;
            blinkTimer += dt;
            if (blinkTimer >= 0.1) {
                visible = !visible;
                blinkTimer = 0.0;
            }
            if (invincibilityTimer <= 0) {
                invincible = false;
                visible = true;
                invincibilityTimer = 0.0;
                blinkTimer = 0.0;
            }
        } else {
            visible = true;
        }
        
        // Update animation based on player state
        updateAnimation(left, right, jump);
        
        // Update animation frames
        animation.update(dt);
    }
    
    /**
     * Update animation state based on player input and status.
     */
    private void updateAnimation(boolean left, boolean right, boolean jump) {
        if (!onGround) {
            // Player is jumping/falling
            animation.setStateJump();
        } else if (left) {
            // Moving left
            animation.setStateRunLeft();
        } else if (right) {
            // Moving right
            animation.setStateRunRight();
        } else {
            // Idle
            animation.setStateIdle();
        }
    }

    public void render(GraphicsContext gc) {
        if (!visible) return;
        
        // Draw sprite using animation system (scaled automatically)
        animation.render(gc, x, y);
    }

    // --- Life system methods ---
    public int getLives() {
        return lives;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void resetLives() {
        lives = MAX_LIVES;
        invincible = false;
        invincibilityTimer = 0.0;
        visible = true;
    }

    public void addLife() {
        if (lives < MAX_LIVES) lives++;
    }

    public void takeDamage() {
        if (invincible) return;
        lives--;
        // small recoil
        vx = -150;
        // start invincibility
        invincible = true;
        invincibilityTimer = 2.0;
        blinkTimer = 0.0;
        visible = false; // start with invisible to show blink
        if (lives <= 0) {
            die();
        }
    }

    public void die() {
        // Disable player movement / simple game over handling
        vx = 0;
        vy = 0;
        // Engine can check getLives() == 0 for game over
    }
    
    /**
     * Get the left edge of the hitbox (centered on player).
     */
    public double getHitboxLeft() {
        return x + (w - hitboxWidth) / 2.0;
    }
    
    /**
     * Get the right edge of the hitbox (centered on player).
     */
    public double getHitboxRight() {
        return getHitboxLeft() + hitboxWidth;
    }
    
    /**
     * Get the top edge of the hitbox (centered on player).
     */
    public double getHitboxTop() {
        return y + (h - hitboxHeight) / 2.0;
    }
    
    /**
     * Get the bottom edge of the hitbox (centered on player).
     */
    public double getHitboxBottom() {
        return getHitboxTop() + hitboxHeight;
    }
    
    /**
     * Get the hitbox width.
     */
    public double getHitboxWidth() {
        return hitboxWidth;
    }
    
    /**
     * Get the hitbox height.
     */
    public double getHitboxHeight() {
        return hitboxHeight;
    }
}
