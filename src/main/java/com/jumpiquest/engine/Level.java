package com.jumpiquest.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class Level {
    public final List<Obstacle> obstacles = new ArrayList<>();
    public final List<MobileObstacle> mobileObstacles = new ArrayList<>();
    public final List<FoodItem> foodItems = new ArrayList<>();
    public int levelWidth; // dynamically set based on difficulty
    public final int levelHeight = 720;
    public final List<Obstacle> platforms = new ArrayList<>();
    public final List<Obstacle> walls = new ArrayList<>();
    public Point2D startPosition;
    public Point2D endPosition;
    public EndHouse house = null;
    private final double groundY = 500;
    public final double spawnX = 100;
    public final double spawnY = groundY - 80; // player height 60

    // Generation parameters (initialized per difficulty)
    private double platformMin = 250, platformMax = 500;
    private double holeMin = 50, holeMax = 140;
    private double holeSpawnChance = 0.8; // probability to spawn a hole after platform
    private double wallChance = 0.55;
    private double wallHMin = 40, wallHMax = 90;
    private double gapShortMin = 30, gapShortMax = 60;
    private double gapMediumMin = 60, gapMediumMax = 100;
    private double gapLongMin = 100, gapLongMax = 140;

    public Level() {
        // Set levelWidth based on difficulty
        com.jumpiquest.main.GameSettings.Difficulty diff = com.jumpiquest.main.GameSettings.getDifficulty();
        if (diff == com.jumpiquest.main.GameSettings.Difficulty.FACILE) {
            this.levelWidth = 5000;
        } else if (diff == com.jumpiquest.main.GameSettings.Difficulty.DIFFICILE) {
            this.levelWidth = 8000;
        } else {
            this.levelWidth = 6000; // MOYEN
        }

        // Initialize generation parameters according to difficulty
        initDifficultySettings(diff);

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

        // generation parameters already initialized by initDifficultySettings in constructor

        while (currentX < levelWidth) {
            // If we are within the final safe zone (last 600px), create a final platform to the end and stop
            double remaining = levelWidth - currentX;
            if (remaining <= 600) {
                double finalLen = Math.max(0, remaining);
                if (finalLen > 0) {
                    Obstacle finalPlatform = new Obstacle(Obstacle.Type.PLATFORM, currentX, finalLen, 0);
                    platforms.add(finalPlatform);
                }
                break; // no more obstacles in safe zone
            }

            // create a platform segment
            double platformLen = rnd.nextDouble(platformMin, platformMax);
            if (currentX + platformLen > levelWidth) {
                platformLen = levelWidth - currentX;
            }
            Obstacle platform = new Obstacle(Obstacle.Type.PLATFORM, currentX, platformLen, 0);
            platforms.add(platform);

            // choose a hole size with adaptive spawn chance
            double holeSize = 0;
            double holeX = currentX + platformLen;
            
            // Only spawn hole if random chance passes and hole start is not in final safe zone
            if (rnd.nextDouble() < holeSpawnChance && holeX < levelWidth - 600) {
                holeSize = rnd.nextDouble(holeMin, holeMax);
                // if the hole would exceed level, clamp
                if (holeX >= levelWidth) {
                    holeSize = 0;
                } else if (holeX + holeSize > levelWidth) {
                    holeSize = Math.max(0, levelWidth - holeX);
                }

                if (holeSize > 0) {
                    Obstacle hole = new Obstacle(Obstacle.Type.HOLE, holeX, holeSize, 0);
                    obstacles.add(hole);
                }
            }

            // optionally place a wall somewhere on the platform we just created
            // avoid placing walls in the final safe zone
            if (platformLen > 140 && rnd.nextDouble() < wallChance) {
                double wallW = 60; // hitbox width (small for jumping)
                double wallH = 60; // hitbox height (small for jumping)
                double minX = currentX + Math.max(60, platformLen * 0.15);
                double maxX = currentX + Math.max(60, platformLen - Math.max(60, platformLen * 0.15));
                if (minX < maxX) {
                    double wallX = rnd.nextDouble(minX, maxX);
                    if (wallX >= levelWidth - 600) {
                        // skip wall placement if it would be inside final safe zone
                        // simply skip adding this wall
                    } else {
                    Obstacle wall = new Obstacle(Obstacle.Type.WALL, wallX, wallW, wallH);
                    walls.add(wall);
                    obstacles.add(wall);
                    }
                }
            }

            // advance currentX with variable gaps
            double gapShort = rnd.nextDouble(gapShortMin, gapShortMax);
            double gapMedium = rnd.nextDouble(gapMediumMin, gapMediumMax);
            double gapLong = rnd.nextDouble(gapLongMin, gapLongMax);
            int gapKind = rnd.nextInt(3);
            double extraGap = (gapKind == 0) ? gapShort : (gapKind == 1 ? gapMedium : gapLong);

            currentX = holeX + holeSize + extraGap;
        }

        // Generate mobile obstacles at specific positions
        generateMobileObstacles();
        
        // Generate food items along the level
        generateFoodItems();

        // place an end-house at a fixed X based on difficulty (so it's always at the intended world coordinate)
        double houseW = 256;
        double houseH = 256;
        double targetX;
        com.jumpiquest.main.GameSettings.Difficulty diff2 = com.jumpiquest.main.GameSettings.getDifficulty();
        if (diff2 == com.jumpiquest.main.GameSettings.Difficulty.FACILE) targetX = 5000;
        else if (diff2 == com.jumpiquest.main.GameSettings.Difficulty.DIFFICILE) targetX = 8000;
        else targetX = 6000; // MOYEN
        // clamp so house fits inside level bounds
        double hx = Math.max(0, Math.min(targetX, levelWidth - (houseW + 20)));
        double hy = groundY - houseH;
        house = new EndHouse(hx, hy, houseW, houseH);
        // set endPosition near house center so legacy checks work
        endPosition = new javafx.geometry.Point2D(hx + houseW / 2.0, hy);
    }

    private void generateMobileObstacles() {
        mobileObstacles.clear();
        // Spawn sheep obstacles at various positions along the level.
        // SheepObstacle slides left with a sprite and will respawn to the right
        // when it leaves the screen.
        double[] positions = {1200, 2000, 3000, 4000, 5200, 6500, 7200};
        double mobileY = groundY - 40; // ground level for mobile obstacles (40 is their height)

        for (double pos : positions) {
            // avoid spawning mobile obstacles inside the final safe zone
            if (pos < levelWidth - 600) {
                mobileObstacles.add(new SheepObstacle(pos, mobileY, levelWidth));
            }
        }
    }

    private void generateFoodItems() {
        foodItems.clear();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        
        // Determine number of food items based on difficulty
        int foodCount = 15; // default for MOYEN
        com.jumpiquest.main.GameSettings.Difficulty diff = com.jumpiquest.main.GameSettings.getDifficulty();
        if (diff == com.jumpiquest.main.GameSettings.Difficulty.FACILE) {
            foodCount = 20; // more items for easy mode
        } else if (diff == com.jumpiquest.main.GameSettings.Difficulty.DIFFICILE) {
            foodCount = 12; // fewer items for hard mode
        }
        
        // Generate food items at regular intervals with slight randomness
        double spacing = (double) levelWidth / foodCount;
        for (int i = 0; i < foodCount; i++) {
            double baseX = spacing * i + 200; // start at 200 to avoid immediate spawn
            double foodX = baseX + rnd.nextDouble(-80, 80); // randomize position within ±80px
            
            // Clamp to level bounds
            foodX = Math.max(100, Math.min(levelWidth - 50, foodX));
            
            // Randomize Y position: slightly above ground or on platforms
            double foodY = groundY - 30 - rnd.nextDouble(0, 100); // between -30 and -130 from ground
            
            foodItems.add(new FoodItem(foodX, foodY));
        }
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
        
        // draw mobile obstacles
        for (MobileObstacle mob : mobileObstacles) {
            mob.render(gc);
        }
        
        // draw food items
        for (FoodItem food : foodItems) {
            food.render(gc);
        }

        // draw end house if present
        if (house != null) {
            house.render(gc);
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
            // shrink collision hitbox relative to visual hitbox so walls are easier to pass
            double insetX = o.w * 0.20; // 20% inset on each horizontal side
            double hitW = Math.max(8, o.w - insetX * 2);
            double wallLeft = o.x + insetX;
            double wallRight = wallLeft + hitW;
            // reduce vertical collision height slightly so small head overlaps don't block
            double heightInset = o.h * 0.25; // ignore top 25% of wall for collisions
            double wallTop = groundY - (o.h - heightInset);
            
            // Use hitbox for collision detection instead of full sprite dimensions
            double hitboxLeft = p.getHitboxLeft();
            double hitboxRight = p.getHitboxRight();
            double hitboxTop = p.getHitboxTop();
            double hitboxBottom = p.getHitboxBottom();
            
            if (hitboxBottom > wallTop && hitboxTop < groundY && hitboxRight > wallLeft && hitboxLeft < wallRight) {
                // collision
                if (p.vx > 0) {
                    p.x = wallLeft - (hitboxRight - p.x); // push player left based on hitbox right edge
                } else if (p.vx < 0) {
                    p.x = wallRight + (p.x - hitboxLeft); // push player right based on hitbox left edge
                }
                p.vx = 0;
            }
        }
    }

    /**
     * Initialize generation parameters according to difficulty.
     */
    private void initDifficultySettings(com.jumpiquest.main.GameSettings.Difficulty diff) {
        // Defaults (MEDIUM)
        platformMin = 250;
        platformMax = 500;
        holeMin = 60;
        holeMax = 140;
        holeSpawnChance = 0.75;
        wallChance = 0.50;
        wallHMin = 40;
        wallHMax = 90;
        gapShortMin = 30;
        gapShortMax = 60;
        gapMediumMin = 60;
        gapMediumMax = 100;
        gapLongMin = 100;
        gapLongMax = 140;

        if (diff == com.jumpiquest.main.GameSettings.Difficulty.FACILE) {
            // Easy: longer platforms, smaller holes, fewer obstacles, wider gaps
            platformMin = 350;
            platformMax = 650;
            holeMin = 40;
            holeMax = 80;
            holeSpawnChance = 0.50;
            wallChance = 0.20;
            wallHMin = 25;
            wallHMax = 60;
            gapShortMin = 20;
            gapShortMax = 50;
            gapMediumMin = 50;
            gapMediumMax = 80;
            gapLongMin = 80;
            gapLongMax = 120;
        } else if (diff == com.jumpiquest.main.GameSettings.Difficulty.DIFFICILE) {
            // Hard: shorter platforms, but holes kept within reachable bounds
            platformMin = 180;
            platformMax = 380;
            // reduce extreme hole sizes so jumps remain possible
            holeMin = 80;
            holeMax = 160;
            // slightly lower spawn chance to avoid impossible sequences
            holeSpawnChance = 0.85;
            wallChance = 0.70;
            wallHMin = 60;
            wallHMax = 120;
            gapShortMin = 40;
            gapShortMax = 80;
            gapMediumMin = 80;
            gapMediumMax = 130;
            gapLongMin = 130;
            gapLongMax = 180;
            // make platforms slightly longer on hard to leave landing spots
            platformMin *= 1.05;
            platformMax *= 1.05;
        }
    }
}
