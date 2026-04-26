package entity;

import util.Constants;
import java.awt.*;

public class Food extends Entity {

    public enum Type { TRASH, ENDANGEREDSPECIES };

    private Type    type;
    private boolean collected;
    private float   pulse;
    private boolean pulseDir = true;
    private float   rotation;

    public Food(float x, float y, Type type) {
        super(x, y, Constants.FOOD_SIZE, 0);
        this.type = type;
    }

    @Override
    public void update() {
        if (collected) return;

        // Pulse animation
        if (pulseDir) { pulse += 0.06f; if (pulse >= 1f) pulseDir = false; }
        else           { pulse -= 0.06f; if (pulse <= 0f) pulseDir = true;  }

        if (type == Type.ENDANGEREDSPECIES) rotation += 2f;
    }

    @Override
    public void render(Graphics2D g) {
        if (collected) return;

        int cx = (int)(x + size / 2f);
        int cy = (int)(y + size / 2f);
        int r  = size / 2 + (int)(pulse * 2);

        if (type == Type.ENDANGEREDSPECIES) {
            // Spinning star glow
            g.setColor(new Color(255, 180, 0, 50));
            g.fillOval(cx - r - 4, cy - r - 4, (r + 4) * 2, (r + 4) * 2);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(cx, cy);
            g2.rotate(Math.toRadians(rotation));
            drawStar(g2, r);
            g2.dispose();
        } else {
            // Normal food dot
            g.setColor(new Color(0, 0, 0, 60));
            g.fillOval(cx - r + 2, cy - r + 2, r * 2, r * 2);

            g.setColor(new Color(80, 240, 120));
            g.fillOval(cx - r, cy - r, r * 2, r * 2);
            g.setColor(new Color(200, 255, 220));
            g.fillOval(cx - r + 2, cy - r + 2, r / 2 + 1, r / 2 + 1);
            g.setColor(new Color(40, 180, 80));
            g.setStroke(new BasicStroke(1f));
            g.drawOval(cx - r, cy - r, r * 2, r * 2);
        }
    }

    private void drawStar(Graphics2D g, int r) {
        int points = 5;
        int outer  = r;
        int inner  = r / 2;
        int[] xs   = new int[points * 2];
        int[] ys   = new int[points * 2];

        for (int i = 0; i < points * 2; i++) {
            double angle  = Math.toRadians(i * 180.0 / points - 90);
            int    radius = (i % 2 == 0) ? outer : inner;
            xs[i] = (int)(radius * Math.cos(angle));
            ys[i] = (int)(radius * Math.sin(angle));
        }

        g.setColor(new Color(255, 215, 0));
        g.fillPolygon(xs, ys, points * 2);
        g.setColor(new Color(255, 255, 150));
        int sm = outer / 3;
        int[] xs2 = new int[points * 2], ys2 = new int[points * 2];
        for (int i = 0; i < points * 2; i++) {
            double angle = Math.toRadians(i * 180.0 / points - 90);
            int radius   = (i % 2 == 0) ? sm : sm / 2;
            xs2[i] = (int)(radius * Math.cos(angle));
            ys2[i] = (int)(radius * Math.sin(angle));
        }
        g.fillPolygon(xs2, ys2, points * 2);
        g.setColor(new Color(200, 140, 0));
        g.setStroke(new BasicStroke(1f));
        g.drawPolygon(xs, ys, points * 2);
    }

    public void collect() { this.collected = true; }
    public boolean isCollected() { return collected; }

    public int getScore() {
        return type == Type.ENDANGEREDSPECIES ? Constants.FOOD_SPECIAL_SCORE : Constants.FOOD_NORMAL_SCORE;
    }

    public boolean isSpecial() { return type == Type.ENDANGEREDSPECIES; }
    public Type getType()      { return type; }
}