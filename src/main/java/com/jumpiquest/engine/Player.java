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

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double dt, boolean left, boolean right, boolean jump) {
        double ax = 0;
        if (left) ax = -speed;
        if (right) ax = speed;
        vx = ax;

        x += vx * dt;

        vy += gravity * dt;
        y += vy * dt;

        double groundY = 500 - h;
        if (y > groundY) {
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

        if (x < 0) x = 0;
        if (x > 800 - w) x = 800 - w;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.CORNFLOWERBLUE);
        gc.fillRect(x, y, w, h);
    }
}
