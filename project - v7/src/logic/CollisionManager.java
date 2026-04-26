package logic;

import entity.*;

public class CollisionManager {

    public boolean checkPlayerEnemy(Player player, Enemy enemy) {
        // Tile-level collision: same tile = caught
        return player.getTileCol() == enemy.getTileCol()
            && player.getTileRow() == enemy.getTileRow();
    }

    public boolean checkPlayerFood(Player player, Food food) {
        if (food.isCollected()) return false;
        return circleCollide(
            player.getCenterX(), player.getCenterY(), player.getSize() / 2f,
            food.getCenterX(),   food.getCenterY(),   food.getSize()   / 2f
        );
    }

    private boolean circleCollide(float ax, float ay, float ar, float bx, float by, float br) {
        float dx   = ax - bx;
        float dy   = ay - by;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        return dist < ar + br;
    }
}