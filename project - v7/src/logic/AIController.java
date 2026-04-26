package logic;

import entity.Enemy;
import entity.Player;

import java.util.List;

public class AIController {

    public void update(List<Enemy> enemies, Player player) {
        boolean playerPowered = player.isPowered();
        for (Enemy e : enemies) {
            e.setScared(playerPowered);
            e.aiUpdate(player);
        }
    }
}