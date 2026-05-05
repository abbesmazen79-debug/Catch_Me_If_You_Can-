package gamealgorithms.logic;

import java.util.List;

import data.entity.Enemy;
import data.entity.Player;

public class AIController {

    public void update(List<Enemy> enemies, Player player) {
        boolean playerPowered = player.isPowered();
        for (Enemy e : enemies) {
            e.setScared(playerPowered);
            e.aiUpdate(player);
        }
    }
}