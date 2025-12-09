package com.jumpiquest.engine;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Set;

public class Engine {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Player player;
    private final Set<KeyCode> keys = new HashSet<>();
    private AnimationTimer timer;
    private long lastNs = 0;

    public Engine(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.player = new Player(100, 400);
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
        boolean left = keys.contains(KeyCode.LEFT) || keys.contains(KeyCode.A);
        boolean right = keys.contains(KeyCode.RIGHT) || keys.contains(KeyCode.D);
        boolean jump = keys.contains(KeyCode.SPACE) || keys.contains(KeyCode.W) || keys.contains(KeyCode.UP);
        player.update(dt, left, right, jump);
    }

    private void render() {
        gc.setFill(Color.DARKSLATEGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // ground
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 500, canvas.getWidth(), canvas.getHeight() - 500);

        player.render(gc);
    }
}
