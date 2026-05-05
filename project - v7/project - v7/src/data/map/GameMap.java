package data.map;

import java.awt.*;

import data.util.Constants;

public class GameMap {

    private final Tile[][] tiles;
    private final int      cols, rows;

    public GameMap() {
        cols = Constants.MAP_COLS;
        rows = Constants.MAP_ROWS;
        tiles = new Tile[rows][cols];
        load(MapLayouts.random());
    }

    private void load(int[][] layout) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int val = (r < layout.length && c < layout[r].length) ? layout[r][c] : 1;
                tiles[r][c] = new Tile(val == 1 ? Tile.Type.WALL : Tile.Type.FLOOR);
            }
        }
    }

    public boolean isWall(int col, int row) {
        if (col < 0 || row < 0 || col >= cols || row >= rows) return true;
        return tiles[row][col].isWall();
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }

    public void render(Graphics2D g) {
        int ts  = Constants.TILE_SIZE;
        int hud = Constants.HUD_HEIGHT;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int px = c * ts;
                int py = hud + r * ts;

                if (tiles[r][c].isWall()) {
                    // Base fill
                    g.setColor(new Color(36, 40, 56));
                    g.fillRect(px, py, ts, ts);
                    // Top / left highlight
                    g.setColor(new Color(52, 58, 80));
                    g.drawLine(px, py, px + ts - 1, py);
                    g.drawLine(px, py, px, py + ts - 1);
                    // Bottom / right shadow
                    g.setColor(new Color(22, 24, 36));
                    g.drawLine(px + ts - 1, py, px + ts - 1, py + ts - 1);
                    g.drawLine(px, py + ts - 1, px + ts - 1, py + ts - 1);
                } else {
                    // Subtle alternating floor
                    g.setColor(((r + c) % 2 == 0) ? new Color(16, 18, 28) : new Color(18, 20, 32));
                    g.fillRect(px, py, ts, ts);
                }
            }
        }
    }
}