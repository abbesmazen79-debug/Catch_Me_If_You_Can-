package ui;

import gamealgorithms.game.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import data.util.Constants;

public class MenuPanel extends JPanel {

    private static final Color BG     = new Color(10, 12, 20);
    private static final Color BLUE   = new Color(80, 150, 255);
    private static final Color DIM    = new Color(60, 70, 105);

    private static final Font F_TITLE = new Font("Consolas", Font.BOLD,  64);
    private static final Font F_SUB   = new Font("Consolas", Font.PLAIN, 13);
    private static final Font F_BTN   = new Font("Consolas", Font.BOLD,  13);
    private static final Font F_HINT  = new Font("Consolas", Font.PLAIN, 11);

    private final Game     game;
    private final String[] DIFFS = {"EASY", "MEDIUM", "HARD"};
    private int selected = 1;   // default MEDIUM
    private int hovered  = -1;

    public MenuPanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(BG);
        setFocusable(true);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int h = hit(e.getX(), e.getY());
                if (h != hovered) { hovered = h; repaint(); }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int h = hit(e.getX(), e.getY());
                if (h >= 0 && h < 3) { selected = h ;repaint(); }
                else if (h == 3)     game.startGame(DIFFS[selected]);
            }
        });
    }

    // idx 0-2 = difficulty, 3 = start
    private Rectangle rect(int idx) {
        int cx = Constants.WINDOW_WIDTH  / 2;
        int cy = Constants.WINDOW_HEIGHT / 2;
        if (idx < 3) {
            int w = 100, h = 36, gap = 14;
            int totalW = 3 * w + 2 * gap;
            return new Rectangle(cx - totalW / 2 + idx * (w + gap), cy - 10, w, h);
        }
        return new Rectangle(cx - 90, cy + 52, 180, 44);
    }

    private int hit(int mx, int my) {
        for (int i = 0; i <= 3; i++) if (rect(i).contains(mx, my)) return i;
        return -1;
    }

    @Override
    protected void paintComponent(Graphics gx) {
        super.paintComponent(gx);
        Graphics2D g = (Graphics2D) gx;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int cx = Constants.WINDOW_WIDTH  / 2;
        int cy = Constants.WINDOW_HEIGHT / 2;

        // Title
        g.setFont(F_TITLE);
        g.setColor(BLUE);
        drawC(g, "CATCH ME IF YOU CAN!", cx, cy - 100);

        // Subtitle
        g.setFont(F_SUB);
        g.setColor(DIM);
        drawC(g, "collect TRASH   avoid the POLLUTING FACTORS", cx, cy - 58);

        // Difficulty label
        g.setFont(F_HINT);
        g.setColor(new Color(40, 48, 72));
        drawC(g, "- - - DIFFICULTY - - -", cx, cy - 22);

        // Difficulty buttons
        for (int i = 0; i < 3; i++) {
            Rectangle r   = rect(i);
            boolean   sel = (i == selected);
            boolean   hov = (hovered == i);

            g.setColor(sel ? new Color(25, 65, 155) : (hov ? new Color(20, 26, 44) : new Color(14, 17, 30)));
            g.fillRoundRect(r.x, r.y, r.width, r.height, 6, 6);
            g.setColor(sel ? BLUE : (hov ? new Color(50, 60, 100) : new Color(30, 38, 62)));
            g.setStroke(new BasicStroke(sel ? 1.5f : 1f));
            g.drawRoundRect(r.x, r.y, r.width, r.height, 6, 6);

            g.setFont(F_BTN);
            g.setColor(sel ? Color.WHITE : new Color(70, 85, 130));
            drawC(g, DIFFS[i], r.x + r.width / 2, r.y + r.height / 2 + 5);
        }

        // Start button
        Rectangle s   = rect(3);
        boolean   hov = (hovered == 3);
        g.setColor(hov ? new Color(45, 105, 225) : new Color(28, 78, 195));
        g.fillRoundRect(s.x, s.y, s.width, s.height, 8, 8);
        g.setColor(hov ? new Color(120, 175, 255) : BLUE);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(s.x, s.y, s.width, s.height, 8, 8);
        g.setFont(F_BTN);
        g.setColor(Color.WHITE);
        drawC(g, "START", s.x + s.width / 2, s.y + s.height / 2 + 5);

        // Footer hint
        g.setFont(F_HINT);
        g.setColor(new Color(35, 42, 65));
        drawC(g, "WASD / Arrow Keys to move", cx, Constants.WINDOW_HEIGHT - 30);
    }

    private void drawC(Graphics2D g, String s, int cx, int y) {
        g.drawString(s, cx - g.getFontMetrics().stringWidth(s) / 2, y);
    }
}