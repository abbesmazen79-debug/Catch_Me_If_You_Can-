package entity;

import input.KeyboardHandler;
import map.GameMap;
import util.Constants;

import java.awt.*;

public class Player extends Entity {

    private boolean isDashing;
    private long nextDashAvailable;
    private long DashEndTime;
    private static double CarbonImprint ;


    private KeyboardHandler keyboard;
    private GameMap         map;

    private boolean powered;
    private long    powerEndTime;

    // Visual pulse
    private float   pulse;
    private boolean pulseDir = true;

    // --- Grid-locked movement ---
    // Current tile the player occupies
    private int tileCol, tileRow;

    // Pixel position of the tile's top-left corner (where we are heading / where we are)
    private float targetX, targetY;

    // Are we currently sliding between two tiles?
    private boolean moving;

    // Queued next direction from keyboard (buffered one step ahead)
    private int queuedDCol, queuedDRow;

    // Pixels per game-tick while sliding
    private static final float SLIDE_SPEED = 6f; // must divide TILE_SIZE evenly (32 / 4 = 8 ticks)


    public float getDashCooldownProgress() {
        long now = System.currentTimeMillis();
        if (now >= nextDashAvailable) return 1.0f; // Ready
        
        long cooldownStartedAt = nextDashAvailable - Constants.DASH_COOLDOWN;
        long elapsed = now - cooldownStartedAt;
        return (float) elapsed / Constants.DASH_COOLDOWN;
    }

    public Player(float x, float y, KeyboardHandler keyboard, GameMap map) {
        super(x, y, Constants.PLAYER_SIZE, Constants.PLAYER_SPEED);
        this.keyboard = keyboard;
        this.map      = map;

        // Snap to the tile that contains the spawn point
        int ts  = Constants.TILE_SIZE;
        int hud = Constants.HUD_HEIGHT;
        tileCol = (int)(x / ts);
        tileRow = (int)((y - hud) / ts);
        snapToTile();
    }

    /** Position pixel origin so the entity is centred in its tile */
    private void snapToTile() {
        int ts  = Constants.TILE_SIZE;
        int hud = Constants.HUD_HEIGHT;
        targetX = tileCol * ts + (ts - size) / 2f;
        targetY = hud + tileRow * ts + (ts - size) / 2f;
        x = targetX;
        y = targetY;
        moving = false;
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        // Read keyboard into queued direction (last pressed wins, allows buffering)
        if (keyboard.left)  { queuedDCol = -1; queuedDRow =  0; }
        if (keyboard.right) { queuedDCol =  1; queuedDRow =  0; }
        if (keyboard.up)    { queuedDCol =  0; queuedDRow = -1; }
        if (keyboard.down)  { queuedDCol =  0; queuedDRow =  1; }
        
        if (!moving) {
            // Try queued direction first, then do nothing if blocked
            if (queuedDCol != 0 || queuedDRow != 0) {
                int nextCol = tileCol + queuedDCol;
                int nextRow = tileRow + queuedDRow;
                if (!map.isWall(nextCol, nextRow)) {
                    tileCol = nextCol;
                    tileRow = nextRow;
                    int ts  = Constants.TILE_SIZE;
                    int hud = Constants.HUD_HEIGHT;
                    targetX = tileCol * ts + (ts - size) / 2f;
                    targetY = hud + tileRow * ts + (ts - size) / 2f;
                    moving  = true;
                }
            }
        }

        if (moving) {
            // Slide toward targetX / targetY
            float currentSlideSpeed = isDashing ? Constants.DASH_SLIDE_SPEED : Constants.NORMAL_SLIDE_SPEED;
            CarbonImprint += isDashing ? Constants.dashing_carbon_imprint : Constants.normal_carbon_imprint;
            float dx = targetX - x;
            float dy = targetY - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist <= currentSlideSpeed) {
                x = targetX;
                y = targetY;
                moving = false;
            } else {
                x += (dx / dist) * currentSlideSpeed;
                y += (dy / dist) * currentSlideSpeed;
            }
        }

        // Power-up expiry
        if (powered && System.currentTimeMillis() > powerEndTime) {
            powered = false;
        }

        
        if (keyboard.espace && now >= nextDashAvailable && !isDashing) {
            isDashing = true;
            DashEndTime = now + 1000;
            nextDashAvailable = now + 7000;

        }

        if (isDashing && now >= DashEndTime) {
            isDashing = false;
        }

        // Pulse animation
        if (pulseDir) { pulse += 0.08f; if (pulse >= 1f) pulseDir = false; }
        else           { pulse -= 0.08f; if (pulse <= 0f) pulseDir = true;  }
    }

    @Override
    public void render(Graphics2D g) {
        int cx = (int)(x + size / 2f);
        int cy = (int)(y + size / 2f);
        int r  = size / 2;

        if (powered) {
            // Glowing ring
            int glow = (int)(4 + pulse * 6);
            g.setColor(new Color(255, 200, 0, 60));
            g.fillOval(cx - r - glow, cy - r - glow, (r + glow) * 2, (r + glow) * 2);
        }
        
        if (isDashing) {
            g.setColor(new Color(255, 255, 255, 100));
            g.drawOval(cx - r - 2, cy - r - 2, r * 2 + 4, r * 2 + 4);
        }

        // Shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(cx - r + 3, cy - r + 4, r * 2, r * 2);

        // Body gradient simulation (two arcs)
        Color base = powered ? new Color(255, 220, 50) : new Color(80, 160, 255);
        Color lite = powered ? new Color(255, 255, 180) : new Color(160, 210, 255);
        g.setColor(base);
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(lite);
        g.fillOval(cx - r + 2, cy - r + 2, r - 2, r - 2);

        // Outline
        g.setColor(powered ? new Color(255, 180, 0) : new Color(40, 100, 200));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.setStroke(new BasicStroke(1f));

        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(cx - 5, cy - 5, 5, 5);
        g.fillOval(cx + 1,  cy - 5, 5, 5);
        g.setColor(new Color(20, 20, 40));
        g.fillOval(cx - 4, cy - 4, 3, 3);
        g.fillOval(cx + 2,  cy - 4, 3, 3);
    }

    public int getTileCol() { return tileCol; }
    public int getTileRow() { return tileRow; }
    public boolean isMoving() { return moving; }

    public void activatePower() {
        powered      = true;
        powerEndTime = System.currentTimeMillis() + Constants.POWER_DURATION;
    }

    public boolean isPowered()  { return powered; }
    public long getPowerEndTime() { return powerEndTime; }

    public static double GetCarbonImprint(){
        return CarbonImprint;
    }
}