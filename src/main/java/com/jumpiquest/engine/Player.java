package com.jumpiquest.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    double x, y;
    double vx, vy;
    final double w = 40, h = 60;
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

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
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
    }

    public void render(GraphicsContext gc) {
        if (!visible) return;
        gc.setFill(Color.CORNFLOWERBLUE);
        gc.fillRect(x, y, w, h);
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
}
