package com.jumpiquest.engine;

import com.jumpiquest.main.ScoreManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class HUD {
    private final Player player;
    private final ScoreManager scoreManager;

    public HUD(Player player, ScoreManager scoreManager) {
        this.player = player;
        this.scoreManager = scoreManager;
    }

    public void render(GraphicsContext gc) {
        // Hearts are now managed by HeartManager (ImageView nodes). HUD only draws score.
        
        // Draw score text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(18));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Score: " + scoreManager.getCurrentScore(), 20, 80);
        gc.fillText("Best: " + scoreManager.getHighScore(), 20, 105);
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
