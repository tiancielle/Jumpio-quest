package com.jumpiquest.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HUD {
    private final Player player;

    public HUD(Player player) {
        this.player = player;
    }

    public void render(GraphicsContext gc) {
        // draw 3 hearts at (20,20), each 30x30, spaced by 40 px
        double startX = 20;
        double startY = 20;
        double size = 30;
        double spacing = 40;

        int lives = player.getLives();
        for (int i = 0; i < 3; i++) {
            double x = startX + i * spacing;
            if (i < lives) {
                drawHeartFull(gc, x, startY, size, Color.RED);
            } else {
                drawHeartEmpty(gc, x, startY, size);
            }
        }
    }

    private void drawHeartFull(GraphicsContext gc, double x, double y, double size, Color color) {
        gc.setFill(color);
        drawHeartShape(gc, x, y, size, true);
    }

    private void drawHeartEmpty(GraphicsContext gc, double x, double y, double size) {
        gc.setFill(Color.GRAY);
        drawHeartShape(gc, x, y, size, true);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(x, y + size*0.15, size*0.5, size*0.5);
        gc.strokeOval(x + size*0.5, y + size*0.15, size*0.5, size*0.5);
        // simple outline using stroke of combined shapes
        gc.setStroke(Color.BLACK);
        drawHeartShape(gc, x, y, size, false);
    }

    private void drawHeartShape(GraphicsContext gc, double x, double y, double size, boolean fill) {
        // simple heart made from two circles and a triangle-ish bottom
        double r = size * 0.25;
        double cx1 = x + r;
        double cy1 = y + r;
        double cx2 = x + r*3;
        double cy2 = y + r;
        double triX1 = x;
        double triY1 = y + r;
        double triX2 = x + size;
        double triY2 = y + r;
        double triX3 = x + size*0.5;
        double triY3 = y + size;

        if (fill) {
            gc.fillOval(cx1 - r, cy1 - r, r*2, r*2);
            gc.fillOval(cx2 - r, cy2 - r, r*2, r*2);
            double[] xs = { triX1, triX3, triX2 };
            double[] ys = { triY1, triY3, triY2 };
            gc.fillPolygon(xs, ys, 3);
        } else {
            gc.strokeOval(cx1 - r, cy1 - r, r*2, r*2);
            gc.strokeOval(cx2 - r, cy2 - r, r*2, r*2);
            double[] xs = { triX1, triX3, triX2 };
            double[] ys = { triY1, triY3, triY2 };
            gc.strokePolygon(xs, ys, 3);
        }
    }
}
