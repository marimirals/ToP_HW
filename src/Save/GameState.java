package Save;

import GameProcess.*;
import characters.BlackKnight;
import characters.Enemy;
import characters.Player;

import java.io.Serializable;

public class GameState implements Serializable {
    private Player player;
    private Enemy enemy;
    private BlackKnight blackKnight;
    private Field currentField;
    private String currentMapName;

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public Enemy getEnemy() { return enemy; }
    public void setEnemy(Enemy enemy) { this.enemy = enemy; }

    public BlackKnight getBlackKnight() { return blackKnight; }
    public void setBlackKnight(BlackKnight blackKnight) { this.blackKnight = blackKnight; }

    public Field getCurrentField() { return currentField; }
    public void setCurrentField(Field currentField) { this.currentField = currentField; }

    public String getCurrentMapName() { return currentMapName; }
    public void setCurrentMapName(String currentMapName) { this.currentMapName = currentMapName; }

}