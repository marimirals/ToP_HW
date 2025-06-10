package characters;

import GameProcess.Field;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BlackKnight {

    private double blackKnightSpawnChance = 1.0;
    private int blackKnightRansom = 350;
    public boolean blackKnightHasAppeared = false;
    private boolean isBlackKnightDead = false;
    private int threeMoveLeft = 3;

    private int knightX = -1;
    private int knightY = -1;

    private final transient Random random = new Random();

    public double getBlackKnightSpawnChance() {
        return blackKnightSpawnChance;
    }

    public void decreaseBlackKnightSpawnChance(double amount) {
        blackKnightSpawnChance = Math.max(0, blackKnightSpawnChance - amount);
    }

    public int getBlackKnightRansom() {
        return blackKnightRansom;
    }

    public void setBlackKnightRansom(int ransom) {
        this.blackKnightRansom = ransom;
    }

    public void setHasAppeared(boolean state) {
        this.blackKnightHasAppeared = state;
    }

    public boolean isDead() {
        return isBlackKnightDead;
    }

    public void setDead(boolean dead) {
        isBlackKnightDead = dead;
    }

    public int getKnightX() {
        return knightX;
    }

    public int getKnightY() {
        return knightY;
    }

    public boolean banKnightAction(Player player) {
        System.out.println("Можно забанить рыцаря навсегда за " + getBlackKnightRansom() + " золота. Делаем? (1 - да, 2 - нет)");

        Scanner scanner = new Scanner(System.in);
        int banKnightChoice = scanner.nextInt();

        if (banKnightChoice == 1 && player.getPlayersGold() >= getBlackKnightRansom()) {
            player.setPlayersGold(player.getPlayersGold() - getBlackKnightRansom());
            setHasAppeared(true);
            setDead(true);
            System.out.println("Черный Рыцарь не появился! Вы потратили " + getBlackKnightRansom() + " золота.");
            return true;
        } else if (banKnightChoice == 1) {
            System.out.println("Недостаточно золота!");
        } else {
            System.out.println("ну ок, тогда ждем расстрел");
        }

        return false;
    }

    public void spawnBlackKnight(Player player, List<Hero> heroes, Field field) {
        if (knightY == -1 && knightX == -1 && !blackKnightHasAppeared && !isBlackKnightDead) {
            knightX = random.nextInt(field.getWidthOfField());
            knightY = random.nextInt(field.getHeightOfField());

            if (field.getItem(knightX, knightY) == '^') {
                knightX = -1;
                knightY = -1;
                spawnBlackKnight(player, heroes, field);
                return;
            }

            System.out.println("Черный рыцарь появится на (" + knightX + ", " + knightY + ") через 3 хода!");
        }

        Hero target = GameUnit.findNearestTarget(heroes, knightX, knightY, field.getHeightOfField());

        if (threeMoveLeft > 0) {
            System.out.println("Черный рыцарь угрожает нам бомбардировкой! Расстрел через " + threeMoveLeft + " хода.");
            threeMoveLeft--;
            return;
        }

        BlackKnightActions(player, heroes, field, target);
    }

    public void BlackKnightActions(Player player, List<Hero> heroes, Field field, Hero target) {
        if (target != null) {
            System.out.println("Черный Рыцарь появился и атакует " + target.getSymbol() + "!");
            if (attackTarget(target, player)) {
                System.out.println("Черный Рыцарь убил цель и исчезает!");
                setHasAppeared(true);
                setDead(true);
                knightX = -1;
                knightY = -1;
            }
        } else {
            System.out.println("Черный Рыцарь появился, но цели не найдены.");
        }
    }

    private boolean attackTarget(Hero target, Player player) {
        player.removeHero(target.getSymbol());
        return true;
    }
}
