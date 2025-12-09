package com.jumpiquest.engine;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class Level {
    public final List<Obstacle> obstacles = new ArrayList<>();
    private final double groundY = 500;
    public final double spawnX = 100;
    public final double spawnY = groundY - 60; // player height 60

    public Level() {
        // Create a series of uniform walls (50 pixels tall, 40 pixels wide) with 200-pixel spacing
        // This allows the player to practice jumping at consistent intervals
        int wallHeight = 75;
        int wallWidth = 40;
        int spacing = 200; // distance between start of one wall to next
        double x = 300;
        
        // Add 4 walls evenly spaced across the level
        for (int i = 0; i < 4; i++) {
            obstacles.add(new Obstacle(Obstacle.Type.WALL, x, wallWidth, wallHeight));
            x += spacing;
        }
        
        // Add a few holes for extra challenge
        obstacles.add(new Obstacle(Obstacle.Type.HOLE, 150, 80, 0));
        obstacles.add(new Obstacle(Obstacle.Type.HOLE, 750, 50, 0));
    }

    public double getGroundY() {
        return groundY;
    }

    public void render(GraphicsContext gc) {
        // draw ground first
        gc.setFill(javafx.scene.paint.Color.DARKGREEN);
        gc.fillRect(0, groundY, 800, 200);

        // draw obstacles (walls and holes)
        for (Obstacle o : obstacles) {
            o.render(gc, groundY);
        }
    }

    public boolean isHoleAt(double centerX) {
        for (Obstacle o : obstacles) {
            if (o.type == Obstacle.Type.HOLE) {
                if (centerX >= o.x && centerX <= o.x + o.w) return true;
            }
        }
        return false;
    }

    public void handleWallCollisions(Player p) {
        for (Obstacle o : obstacles) {
            if (o.type != Obstacle.Type.WALL) continue;
            double wallLeft = o.x;
            double wallRight = o.x + o.w;
            double wallTop = groundY - o.h;
            double playerBottom = p.y + p.h;
            if (playerBottom > wallTop && p.y < groundY && p.x + p.w > wallLeft && p.x < wallRight) {
                // collision
                if (p.vx > 0) {
                    p.x = wallLeft - p.w;
                } else if (p.vx < 0) {
                    p.x = wallRight;
                }
                p.vx = 0;
            }
        }
    }
}
