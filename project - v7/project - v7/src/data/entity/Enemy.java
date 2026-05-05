package data.entity;

import java.awt.*;
import java.util.*;
import data.map.GameMap;
import data.util.Constants;

public class Enemy extends Entity {

    public enum Behavior { CARBON, ACID, FALLOUT }

    private final GameMap  map;
    private final Behavior behavior;
    private final Color    color;
    private final float    baseSpeed;

    private boolean scared;
    private float   slideSpeed;

    // Grid position
    private int   tileCol, tileRow;
    private float targetX, targetY;
    private boolean moving;

    // Bob animation
    private float   bob;
    private boolean bobUp = true;

    public Enemy(int tileCol, int tileRow, float speed, Behavior behavior, GameMap map, Color color) {
        super(0, 0, Constants.ENEMY_SIZE, speed);
        this.map        = map;
        this.behavior   = behavior;
        this.baseSpeed  = speed;
        this.slideSpeed = speed;
        this.color      = color;
        this.tileCol    = tileCol;
        this.tileRow    = tileRow;
        snapToTile();
    }

    // ── Grid helpers ─────────────────────────────────────────────────────────

    private void snapToTile() {
        int ts  = Constants.TILE_SIZE;
        int hud = Constants.HUD_HEIGHT;
        targetX = tileCol * ts + (ts - size) / 2f;
        targetY = hud + tileRow * ts + (ts - size) / 2f;
        x = targetX;
        y = targetY;
        moving = false;
    }

    private void updateTarget() {
        int ts  = Constants.TILE_SIZE;
        int hud = Constants.HUD_HEIGHT;
        targetX = tileCol * ts + (ts - size) / 2f;
        targetY = hud + tileRow * ts + (ts - size) / 2f;
    }

    // ── AI update ────────────────────────────────────────────────────────────

    @Override
    public void update() { /* driven by AIController */ }

    public void aiUpdate(Player player) {
        slideSpeed = scared ? baseSpeed * 0.55f : baseSpeed;

        if (!moving) {
            int[] goal = pickGoalTile(player);
            int[] step = bfsStep(tileCol, tileRow, goal[0], goal[1]);
            if (step != null) {
                tileCol = step[0];
                tileRow = step[1];
                updateTarget();
                moving = true;
            }
        }

        if (moving) {
            float dx   = targetX - x;
            float dy   = targetY - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist <= slideSpeed) {
                x = targetX;
                y = targetY;
                moving = false;
            } else {
                x += dx / dist * slideSpeed;
                y += dy / dist * slideSpeed;
            }
        }

        if (bobUp) { bob += 0.06f; if (bob >= 1f) bobUp = false; }
        else        { bob -= 0.06f; if (bob <= 0f) bobUp = true;  }
    }

    // ── Goal selection ───────────────────────────────────────────────────────

    private int[] pickGoalTile(Player player) {
        int pc = player.getTileCol();
        int pr = player.getTileRow();

        if (scared) {
            return new int[]{
                clamp(tileCol * 2 - pc, 1, map.getCols() - 2),
                clamp(tileRow * 2 - pr, 1, map.getRows() - 2)
            };
        }

        return switch (behavior) {
            case CARBON -> new int[]{pc, pr};

            case ACID -> {
                int dc = Integer.signum(pc - tileCol);
                int dr = Integer.signum(pr - tileRow);
                if (dc == 0 && dr == 0) dc = 1;
                yield new int[]{
                    clamp(pc + dc * 4, 1, map.getCols() - 2),
                    clamp(pr + dr * 4, 1, map.getRows() - 2)
                };
            }

            case FALLOUT -> {
                int dc = Integer.signum(pc - tileCol);
                int dr = Integer.signum(pr - tileRow);
                yield new int[]{
                    clamp(pc - dr * 3, 1, map.getCols() - 2),
                    clamp(pr + dc * 3, 1, map.getRows() - 2)
                };
            }
        };
    }

    private int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

    // ── BFS: one step toward goal ─────────────────────────────────────────────

    private int[] bfsStep(int sc, int sr, int tc, int tr) {
        if (sc == tc && sr == tr) return null;

        int cols = map.getCols(), rows = map.getRows();
        int total = rows * cols;
        int[] prevCol = new int[total];
        int[] prevRow = new int[total];
        Arrays.fill(prevCol, -1);

        boolean[] visited = new boolean[total];
        visited[sr * cols + sc] = true;

        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sc, sr});

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int cc = cur[0], cr = cur[1];

            if (cc == tc && cr == tr) {
                // Walk parent chain back to the first step
                int pc = cc, pr = cr;
                while (prevCol[pr * cols + pc] != sc || prevRow[pr * cols + pc] != sr) {
                    int nc = prevCol[pr * cols + pc];
                    int nr = prevRow[pr * cols + pc];
                    pc = nc; pr = nr;
                }
                return new int[]{pc, pr};
            }

            for (int[] d : dirs) {
                int nc = cc + d[0], nr = cr + d[1];
                int nid = nr * cols + nc;
                if (nc >= 0 && nr >= 0 && nc < cols && nr < rows
                        && !visited[nid] && !map.isWall(nc, nr)) {
                    visited[nid] = true;
                    prevCol[nid] = cc;
                    prevRow[nid] = cr;
                    q.add(new int[]{nc, nr});
                }
            }
        }
        return null;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(Graphics2D g) {
        int cx = (int)(x + size / 2f);
        int cy = (int)(y + size / 2f) + (int)(bob * 2);
        int r  = size / 2;

        Color body    = scared ? new Color(70, 90, 200)   : color;
        Color lite    = scared ? new Color(140, 160, 255)  : color.brighter().brighter();
        Color outline = scared ? new Color(40, 60, 160)   : color.darker();

        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(cx - r + 3, cy - r + 5, r * 2, r * 2);

        g.setColor(body);
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(lite);
        g.fillOval(cx - r + 2, cy - r + 2, r - 1, r - 1);

        g.setColor(outline);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.setStroke(new BasicStroke(1f));

        g.setColor(Color.WHITE);
        g.fillOval(cx - 5, cy - 4, 5, 5);
        g.fillOval(cx + 1,  cy - 4, 5, 5);
        g.setColor(new Color(15, 15, 30));
        int ex = scared ? -2 : 1;
        g.fillOval(cx - 4 + ex, cy - 3, 3, 3);
        g.fillOval(cx + 2  + ex, cy - 3, 3, 3);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public void setScared(boolean v) { scared = v; }
    public boolean isScared()  { return scared; }
    public int getTileCol()    { return tileCol; }
    public int getTileRow()    { return tileRow; }
}