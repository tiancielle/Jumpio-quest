package com.jumpiquest.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Obstacle {
    public enum Type { WALL, HOLE, PLATFORM }

    public final Type type;
    public final double x;
    public final double w;
    // only for wall
    public final double h;

    public Obstacle(Type type, double x, double w, double h) {
        this.type = type;
        this.x = x;
        this.w = w;
        this.h = h;
    }

    public void render(GraphicsContext gc, double groundY) {
        if (type == Type.WALL) {
            gc.setFill(Color.DIMGRAY);
            gc.fillRect(x, groundY - h, w, h);
        } else if (type == Type.HOLE) {
            // draw hole as a black rectangle cut in the ground
            gc.setFill(Color.BLACK);
            gc.fillRect(x, groundY, w, 100);
        } else if (type == Type.PLATFORM) {
            // draw a ground/platform segment
            gc.setFill(Color.DARKGREEN);
            // draw a thicker ground so it looks like the floor
            gc.fillRect(x, groundY, w, 200);
        }
    }
}
