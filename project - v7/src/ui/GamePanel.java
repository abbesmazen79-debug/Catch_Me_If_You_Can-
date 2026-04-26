package ui;

import entity.*;
import game.Game;
import game.GameState;
import logic.FoodManager;
import map.GameMap;
import util.Constants;
//import game.GameLoop;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class GamePanel extends JPanel {

    private static final Font F_TITLE = new Font("Arial", Font.BOLD,  42);
    private static final Font F_BODY  = new Font("Arial", Font.BOLD, 14);
    private static final Font F_HINT  = new Font("Arial", Font.BOLD, 12);

    private final Game game;
    private final HUD  hud = new HUD();

    private BufferedImage buffer;
    private Graphics2D    bg;

    public static int i;

    public GamePanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Color.BLACK.darker());
        setFocusable(true);
        
    }

    public void render(GameMap map, Player player, List<Enemy> enemies,FoodManager food, int score, int level, GameState state, int bestscore) {
        if (buffer == null) {
            buffer = new BufferedImage(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT,BufferedImage.TYPE_INT_ARGB);
            bg = buffer.createGraphics();
            bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            bg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        }

        bg.setColor(new Color(10, 12, 20));
        bg.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        map.render(bg);
        for (Food f : food.getFoods()) f.render(bg);
        player.render(bg);
        for (Enemy e : enemies) e.render(bg);
        hud.render(bg, score, level, player, food, bestscore);

        if (state == GameState.PAUSED)    drawPause(bg);
        if (state == GameState.GAME_OVER) drawGameOver(bg, score);

        Graphics g = getGraphics();
        if (g != null) { g.drawImage(buffer, 0, 0, null); g.dispose(); }
    }

    private void drawPause(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 155));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        int cx = Constants.WINDOW_WIDTH  / 2;
        int cy = Constants.WINDOW_HEIGHT / 2;

        g.setFont(F_TITLE);
        g.setColor(Color.WHITE);
        drawC(g, "PAUSED", cx, cy - 10);

        g.setFont(F_HINT);
        g.setColor(new Color(90, 110, 160));
        drawC(g, "ESC  to resume", cx, cy + 28);
    }

    private void drawGameOver(Graphics2D g, int score) {
        

        g.setColor(new Color(0, 0, 0, 165));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        int cx = Constants.WINDOW_WIDTH  / 2;
        int cy = Constants.WINDOW_HEIGHT / 2;

        g.setFont(F_TITLE);
        g.setColor(new Color(220, 75, 75));
        drawC(g, "GAME OVER", cx, cy - 14);

        g.setFont(F_BODY);
        g.setColor(new Color(160, 175, 215));
        drawC(g, "score  " + String.format("%06d", score), cx, cy + 26);

        g.setFont(F_BODY);
        g.setColor(new Color(160, 175, 215));
        drawC(g, "Carbon Imprint  :" + String.format("%.5f", Player.GetCarbonImprint())+"KG of carbon dioxide equivalent", cx, cy + 46);

        g.setFont(F_HINT);
        g.setColor(new Color(75, 90, 135));
        drawC(g, "ENTER  play again     ESC  menu", cx, cy + 66);

        
        g.setFont(F_BODY);
        g.setColor(new Color(255, 127, 0));
        String tip = Constants.RSETIPS[i];
        int maxWidth = 700;
        FontMetrics fm = g.getFontMetrics();
        String[] words = tip.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = cy + 96;

        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxWidth) {
                drawC(g, line.toString(), cx, lineY);
                lineY += 18;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        // draw the last line
        if (line.length() > 0) drawC(g, line.toString(), cx, lineY);
        
    }


    private void drawC(Graphics2D g, String s, int cx, int y) {
        g.drawString(s, cx - g.getFontMetrics().stringWidth(s) / 2, y);
    }
}