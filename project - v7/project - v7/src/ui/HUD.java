package ui;

import gamealgorithms.logic.FoodManager;

import java.awt.Graphics2D;

import data.entity.Player;
import data.util.Constants;

import java.awt.*;

public class HUD {

    // Crisp font stack: try Consolas, fall back to monospaced
    private static final Font F_LABEL = new Font("Arial", Font.BOLD, 11);
    private static final Font F_VALUE = new Font("Arial", Font.BOLD,  18);
    private static final Font F_POWER = new Font("Arial", Font.BOLD,  11);

    private static final Color BG  = new Color(10, 12, 20);
    private static final Color SEP = new Color(30, 35, 55);


    public void render(Graphics2D g, int score, int level, Player player, FoodManager food,int bestScore) {
        int W = Constants.WINDOW_WIDTH;
        int H = Constants.HUD_HEIGHT;

        g.setColor(BG);
        g.fillRect(0, 0, W, H);
        g.setColor(SEP);
        g.setStroke(new BasicStroke(1f));
        g.drawLine(0, H - 1, W, H - 1);

        // Stats — evenly spaced on the left
        stat(g, "SCORE", String.format("%06d", score), new Color(80, 215, 135), 28);
        stat(g, "LEVEL", String.valueOf(level),         new Color(255, 200, 65), 160);
        stat(g, "FOOD",  String.valueOf(food.remaining()), new Color(80, 200, 220), 260);
        stat(g, "Dash",  player.getDashCooldownProgress() >= 1.0f ? "READY" : "", new Color(80, 200, 220), 320);
        stat(g, "BEST", String.format("%06d", bestScore), new Color(80, 200, 220), 400);
        


        // Power bar — centre
        if (player.isPowered()) {
            long rem  = player.getPowerEndTime() - System.currentTimeMillis();
            float pct = Math.max(0f, rem / (float) Constants.POWER_DURATION);
            int bx = 480, bw = 180, by = 17, bh = 10;

            g.setFont(F_POWER);
            g.setColor(new Color(255, 200, 50));
            g.drawString("POWER", bx, by - 2);

            g.setColor(new Color(35, 28, 5));
            g.fillRoundRect(bx, by+5, bw, bh, 4, 4);
            g.setColor(new Color(255, 195, 40));
            g.fillRoundRect(bx, by+5, (int)(bw * pct), bh, 4, 4);
            g.setColor(new Color(180, 130, 10));
            g.drawRoundRect(bx, by+5, bw, bh, 4, 4);
        }
    }

    private void stat(Graphics2D g, String label, String value, Color valueColor, int x) {
        g.setFont(F_LABEL);
        g.setColor(new Color(55, 65, 100));
        g.drawString(label, x, 16);
        g.setFont(F_VALUE);
        g.setColor(valueColor);
        g.drawString(value, x, 36);
    }
}