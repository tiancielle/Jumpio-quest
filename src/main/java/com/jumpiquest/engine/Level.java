package com.jumpiquest.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class Level {
    public final List<Obstacle> obstacles = new ArrayList<>();
    // shorter, playable level length requested by the user
    public final int levelWidth = 5000;
    public final int levelHeight = 720;
    public final List<Obstacle> platforms = new ArrayList<>();
    public final List<Obstacle> walls = new ArrayList<>();
    public Point2D startPosition;
    public Point2D endPosition;
    private final double groundY = 500;
    public final double spawnX = 100;
    public final double spawnY = groundY - 60; // player height 60

    public Level() {
        // generate the level procedurally
        generateLevel();
    }

    /**
     * Procedurally generate platforms, holes and walls across the horizontal level.
     * Populates `platforms`, `walls` and `obstacles` (obstacles holds walls + holes
     * for compatibility with existing collision code).
     */
    public void generateLevel() {
        platforms.clear();
        walls.clear();
        obstacles.clear();

        startPosition = new Point2D(spawnX, spawnY);
        endPosition = new Point2D(levelWidth - 100, groundY - 60);

        double currentX = 0.0;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        while (currentX < levelWidth) {
            // create a platform segment
            // moderate platform segments to allow natural progression
            double platformLen = rnd.nextDouble(250, 500); // 250-500 px segments
            if (currentX + platformLen > levelWidth) {
                platformLen = levelWidth - currentX;
            }
            Obstacle platform = new Obstacle(Obstacle.Type.PLATFORM, currentX, platformLen, 0);
            platforms.add(platform);

            // choose a hole size from one of the ranges: 80-150, 150-300, 300-450
            // Make holes small enough to be jumpable by the player.
            // Conservative horizontal jump budget ~180 px; keep holes below that and often smaller.
            double holeSize = rnd.nextDouble(50, 140); // 50-140 px

            double holeX = currentX + platformLen;
            // if the hole would exceed level, clamp and break
            if (holeX >= levelWidth) break;
            if (holeX + holeSize > levelWidth) {
                holeSize = Math.max(0, levelWidth - holeX);
            }

            if (holeSize > 0) {
                Obstacle hole = new Obstacle(Obstacle.Type.HOLE, holeX, holeSize, 0);
                obstacles.add(hole);
            }

            // optionally place a wall somewhere on the platform we just created
            // ensure wall sits on the platform (not inside the hole)
            // reduce probability and heights to avoid unjumpable walls
            if (platformLen > 140 && rnd.nextDouble() < 0.55) { // medium chance to add walls
                double wallW = 30; // slightly narrower wall
                // keep wall heights low so player can reach/clear them when jumping
                double wallH = rnd.nextDouble(40, 90); // 40-90 px
                // place wall away from platform edges to allow approach and landing
                double minX = currentX + Math.max(60, platformLen * 0.15);
                double maxX = currentX + Math.max(60, platformLen - Math.max(60, platformLen * 0.15));
                if (minX < maxX) {
                    double wallX = rnd.nextDouble(minX, maxX);
                    Obstacle wall = new Obstacle(Obstacle.Type.WALL, wallX, wallW, wallH);
                    walls.add(wall);
                    obstacles.add(wall);
                }
            }

            // advance currentX: move past hole and then add a random gap before next platform
            // gaps between segments should be manageable horizontally
            // keep gaps small to medium so player can cross with normal jump
            double gapShort = rnd.nextDouble(30, 60);
            double gapMedium = rnd.nextDouble(60, 100);
            double gapLong = rnd.nextDouble(100, 140);
            int gapKind = rnd.nextInt(3);
            double extraGap = (gapKind == 0) ? gapShort : (gapKind == 1 ? gapMedium : gapLong);

            currentX = holeX + holeSize + extraGap;
        }

        // ensure obstacles list also contains walls (holes already added) - already done
    }

    public double getGroundY() {
        return groundY;
    }

    public void render(GraphicsContext gc) {
        // draw ground segments and obstacles across the entire level width
        // the Engine applies camera translation, so we render world coordinates
        // draw a background ground layer for the entire level width
        gc.setFill(javafx.scene.paint.Color.DARKGREEN);
        gc.fillRect(0, groundY, levelWidth, 200);

        // draw obstacles (walls and holes) at their world positions
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
