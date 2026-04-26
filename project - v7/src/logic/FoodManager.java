package logic;

import entity.Food;
import entity.Player;
import map.GameMap;
import util.Constants;

import java.util.ArrayList;
import java.util.List;

public class FoodManager {

    private List<Food> foods = new ArrayList<>();
    private GameMap    map;

    public FoodManager(GameMap map) {
        this.map = map;
    }

    public void spawnAll(Player player) {
        foods.clear();
        int spawned = 0, special = 0;
        int attempts = 0;

        while ((spawned < Constants.FOOD_COUNT) && attempts < 5000) {
            attempts++;
            int col = 1 + (int)(Math.random() * (map.getCols() - 2));
            int row = 1 + (int)(Math.random() * (map.getRows() - 2));
            if (map.isWall(col, row)) continue;

            float fx = col * Constants.TILE_SIZE + (Constants.TILE_SIZE - Constants.FOOD_SIZE) / 2f;
            float fy = Constants.HUD_HEIGHT + row * Constants.TILE_SIZE + (Constants.TILE_SIZE - Constants.FOOD_SIZE) / 2f;

            // Keep distance from player start
            float dx = fx - player.getCenterX();
            float dy = fy - player.getCenterY();
            if (Math.sqrt(dx * dx + dy * dy) < 60) continue;

            // No duplicate tiles
            boolean dup = false;
            for (Food f : foods) {
                if (Math.abs(f.getX() - fx) < 4 && Math.abs(f.getY() - fy) < 4) { dup = true; break; }
            }
            if (dup) continue;

            Food.Type type = (special < Constants.SPECIAL_FOOD_COUNT && Math.random() < 0.25) ? Food.Type.ENDANGEREDSPECIES : Food.Type.TRASH;
            if (type == Food.Type.ENDANGEREDSPECIES) special++;

            foods.add(new Food(fx, fy, type));
            spawned++;
        }
    }

    public void update() {
        for (Food f : foods) f.update();
    }

    public int checkCollection(Player player, CollisionManager collision) {
        int gained = 0;
        for (Food f : foods) {
            if (!f.isCollected() && collision.checkPlayerFood(player, f)) {
                f.collect();
                gained += f.getScore();
                if (f.isSpecial()) player.activatePower();
            }
        }
        return gained;
    }

    public boolean allCollected() {
        return foods.stream().allMatch(Food::isCollected);
    }

    public int remaining() {
        return (int) foods.stream().filter(f -> !f.isCollected()).count();
    }

    public List<Food> getFoods() { return foods; }
}