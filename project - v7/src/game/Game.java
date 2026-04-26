package game;

import entity.*;
import input.KeyboardHandler;
import logic.*;
import map.GameMap;
import ui.GamePanel;
import ui.MenuPanel;
import util.Constants;
import util.HighScoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private final JFrame     window;
    private final GamePanel  gamePanel;
    private final MenuPanel  menuPanel;
    private final CardLayout cardLayout;
    private final JPanel     root;
    private final GameLoop   gameLoop;

    private final KeyboardHandler keyboard = new KeyboardHandler();

    private GameMap          map;
    private Player           player;
    private List<Enemy>      enemies;
    private FoodManager      foodManager;
    private CollisionManager collision;
    private AIController     ai;

    private GameState state      = GameState.MENU;
    private int       score      = 0;
    private int       level      = 1;
    private String    difficulty = "MEDIUM";

    private boolean escWasDown = false;

    // In Game.java, add alongside the other fields:
    private final HighScoreManager highScoreManager = new HighScoreManager();
    private static int bestScore = 0;   // loaded once, updated on save

    public static int GetBestScore() {
        return bestScore;
    }
    

    // ── Constructor ───────────────────────────────────────────────────────────

    public Game() {
        cardLayout = new CardLayout();
        root       = new JPanel(cardLayout);
        gamePanel  = new GamePanel(this);
        menuPanel  = new MenuPanel(this);

        gamePanel.addKeyListener(keyboard);
        root.add(menuPanel, "MENU");
        root.add(gamePanel, "GAME");

        window = new JFrame(Constants.TITLE);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(root);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        cardLayout.show(root, "MENU");

        gameLoop = new GameLoop(this);
        gameLoop.start();
    }

    // ── Game lifecycle ────────────────────────────────────────────────────────

    public void startGame(String diff) {
        GamePanel.i = (int)(Math.random() * Constants.RSETIPS.length);
        difficulty=diff;
        bestScore = highScoreManager.loadHighScore(diff);
        score      = 0;
        level      = 1;
        init();
        state = GameState.PLAYING;
        cardLayout.show(root, "GAME");
        gamePanel.requestFocusInWindow();
    }

    private void init() {
        map       = new GameMap();
        collision = new CollisionManager();
        ai        = new AIController();

        // Player spawns at tile (1, 1) — always a floor tile
        int ts  = Constants.TILE_SIZE;
        int hud = Constants.HUD_HEIGHT;
        player = new Player(
            ts + (ts - Constants.PLAYER_SIZE) / 2f,
            hud + ts + (ts - Constants.PLAYER_SIZE) / 2f,
            keyboard, map
        );

        enemies     = new ArrayList<>();
        foodManager = new FoodManager(map);

        spawnEnemies();
        foodManager.spawnAll(player);
    }

    private void spawnEnemies() {
        float speed = switch (difficulty) {
            case "EASY" -> 1.6f;
            case "HARD" -> 3.0f;
            default     -> 2.2f;
        };
        int count = switch (difficulty) {
            case "EASY" -> 1;
            case "HARD" -> 3;
            default     -> 2;
        };

        // Spawn tiles in bottom-right area of the 40×20 map (all known floor tiles)
        //int[][] spawns   = {{37, 18}, {34, 18}, {37, 16}};

        Color[] colors   = {new Color(220, 70, 70), new Color(150, 70, 220), new Color(220, 130, 40)};
        Enemy.Behavior[] behaviors = {Enemy.Behavior.CARBON, Enemy.Behavior.FALLOUT, Enemy.Behavior.ACID};

        for (int i = 0; i < count; i++) {
            float spd = speed + i * 0.3f + (level - 1) * 0.18f;
            int[] tile = findSpawnTile(i);
            enemies.add(new Enemy(tile[0], tile[1], spd,behaviors[i] , map, colors[i]));
        }
    }

    /** Find a floor tile far from player spawn, offset by enemyIndex to spread enemies apart. */
    private int[] findSpawnTile(int enemyIndex) {
        int cols = map.getCols(), rows = map.getRows();
        int pc = 1, pr = 1; // player starts at (1,1)
        int bestC = cols - 2, bestR = rows - 2, bestDist = -1;

        // Scan from a different corner per enemy index
        //int startC = (enemyIndex % 2 == 0) ? cols - 2 : cols / 2;
        //int startR = (enemyIndex < 2)       ? rows - 2 : rows / 2;

        for (int r = rows - 2; r >= 1; r--) {
            for (int c = cols - 2; c >= 1; c--) {
                if (map.isWall(c, r)) continue;
                int dc = c - pc, dr = r - pr;
                int dist = dc * dc + dr * dr - enemyIndex * 20;
                if (dist > bestDist) {
                    bestDist = dist;
                    bestC = c; bestR = r;
                }
            }
        }
        return new int[]{bestC, bestR};
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public void update() {
        switch (state) {
            case PLAYING  -> updatePlaying();
            case PAUSED   -> updatePaused();
            case GAME_OVER-> updateGameOver();
            default       -> {}
        }
    }

    private void updatePlaying() {
        togglePauseOnEsc();

        player.update();
        ai.update(enemies, player);

        score += foodManager.checkCollection(player, collision);

        for (Enemy e : enemies) {
            if (!e.isScared() && collision.checkPlayerEnemy(player, e)) {
                state = GameState.GAME_OVER;
                return;
            }
        }

        if (foodManager.allCollected()) {
            level++;
            score += 150 * level;
            enemies.clear();
            spawnEnemies();
            foodManager.spawnAll(player);
        }
    }

    private void updatePaused() {
        togglePauseOnEsc();
        if (keyboard.enter) { state = GameState.PLAYING; keyboard.enter = false; }
    }

    private void updateGameOver() {
        if (keyboard.enter)  { 
            highScoreManager.saveHighScore(score, difficulty);
            bestScore = highScoreManager.loadHighScore(difficulty);
            startGame(difficulty); keyboard.enter  = false; 
        }
        if (keyboard.escape) { goToMenu();            keyboard.escape = false; }
    }

    private void togglePauseOnEsc() {
        if (keyboard.escape && !escWasDown)
            state = (state == GameState.PLAYING) ? GameState.PAUSED : GameState.PLAYING;
        escWasDown = keyboard.escape;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    public void render() {
        if (state == GameState.MENU) return;
            gamePanel.render(map, player, enemies, foodManager, score, level, state, bestScore);
    }

    // ── Menu ──────────────────────────────────────────────────────────────────

    public void goToMenu() {
        state = GameState.MENU;
        keyboard.reset();
        cardLayout.show(root, "MENU");
        menuPanel.repaint();
    }
}