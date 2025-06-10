package characters;

import GameProcess.*;

import java.util.ArrayList;
import java.util.List;

public class Hero {

    private char symbol; // Символ героя
    private List<GameUnit> army;
    private int x; // Координата X на карте
    private int y; // Координата Y на карте
    private int heroesStamina; // Выносливость героя
    private static int costOfHero = 500;
    private boolean isInCastle = true;
    private boolean isInOpponentsCastle = true;
    private boolean moveIsMade = false;
    private boolean isEnemy; // Новое поле: является ли герой врагом
    private int health;
    private int dist;
    private boolean fastCastleCapturing = false;

    public boolean getFastCastleCapturing() {
        return fastCastleCapturing;
    }

    public void setFastCastleCapturing(boolean x) {
        this.fastCastleCapturing = x;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int x) {
        this.dist = x;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int x) {
        this.health = x;
    }

    public static int getCostOfHero() {
        return costOfHero;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeroesStamina() {
        return heroesStamina;
    }

    public Hero(char symbol, int x, int y, boolean isEnemy, boolean isInOpponentsCastle, Field field) {
        this.symbol = symbol;
        this.army = new ArrayList<>();
        this.army.add(new GameUnit("Копейщик", 1, 100, 10, 2, 1));
        this.x = x;
        this.y = y;
        this.heroesStamina = 100;
        this.isEnemy = isEnemy;
        this.health = 100;
        this.dist = field.getWidthOfField()/2;
    }

    public List<GameUnit> getArmy() {
        return new ArrayList<>(army);
    }

    public void addUnit(GameUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Юнит не может быть null.");
        }
        army.add(unit);
    }

    public void displayArmy(Hero currHero) {
        System.out.println("Армия героя " + currHero.getSymbol() + ":");
        for (int i = 0; i < army.size(); i++) {
            System.out.println((i + 1) + ". " + army.get(i));
        }
    }

    // Перемещение героя
    public void move(Player player, Enemy enemy, Hero hero, Field field, int heightOfField, int widthOfField, int newX, int newY, GameProcess gp) {
        // Проверка на выход за границы поля
        if (newX < 0 || newY < 0 || newX >= heightOfField || newY >= widthOfField) {
            System.out.println("А там мир закончился... Идите-ка в другое место.");
            return;
        }

        // Проверка на максимальное расстояние перемещения
        if (Math.abs(newX - x) > (dist) || Math.abs(newY - y) > (dist)) {
            System.out.println("Герой не может переместиться более чем на " + heightOfField / 2 + " клеток.");
            return;
        }

        // Получаем символ клетки, на которую перемещается герой
        Character cell = field.getItem(newX, newY);
        if (cell == null) {
            System.out.println("Невозможно переместиться на эту клетку.");
            return;
        }

        // Проверка на клетку "^" (нельзя ходить)
        if (cell == '^') {
            System.out.println("Ты шо, брат, куда на гору идёш, нельзя тудаво");
            return;
        }

        // Проверяем, достаточно ли выносливости для перемещения
        int staminaCost = getStaminaCost(cell);
        if (heroesStamina >= staminaCost) {
            // Обновляем статус нахождения в замке
            if ((newX == heightOfField - 1 && newY == widthOfField - 1) || (newX == 0 && newY == 0)) {
                isInCastle = true;
            } else {
                isInCastle = false;
            }

            // Проверяем, есть ли на новой клетке герой противника
            for (Hero otherHero : (this.isEnemy ? player.getPlayersHeroes() : enemy.getHeroes())) {
                if (otherHero.getX() == newX && otherHero.getY() == newY) {
                    gp.fight(this, otherHero, player, enemy);
                    return; // Завершаем перемещение, так как началось сражение
                }
            }

            this.x = newX;
            this.y = newY;
            moveIsMade = true;
            heroesStamina -= staminaCost; // Снимаем выносливость
            System.out.println("Герой " + hero.getSymbol() + " перемещен в позицию (" + x + ", " + y + ")");
            System.out.println("Выносливость героя: " + heroesStamina);
        } else {
            System.out.println("Недостаточно выносливости для перемещения.");
            restoreStamina();
        }
    }

    private int getStaminaCost(char cell) {
        if (isEnemy) {
            // Логика для врага
            switch (cell) {
                case '#':
                    return 3;
                case '$':
                    return 10;
                case '.':
                    return 4;
                case 'o':
                    return 1;
                default:
                    return 0;
            }
        } else {
            // Логика для игрока
            switch (cell) {
                case '#':
                    return 10;
                case '$':
                    return 3;
                case '.':
                    return 4;
                case 'o':
                    return 1;
                default:
                    return 0;
            }
        }
    }

    // Восстановление выносливости
    public void restoreStamina() {
        if (heroesStamina < 100) {
            int newStamina = (heroesStamina+10 <= 100) ? 10 : 100;
            heroesStamina = newStamina; // Восстанавливаем 10 единиц выносливости
            System.out.println("Выносливость героя восстановлена: " + heroesStamina);
        }
    }

    public boolean isInCastle() {
        return isInCastle;
    }

    public void setInCastle(boolean inCastle) {
        isInCastle = inCastle;
    }

    public void setMoveIsMade(boolean moveIsMade) {
        this.moveIsMade = moveIsMade;
    }

    public boolean isMoveIsMade() {
        return moveIsMade;
    }

    public boolean getIsEnemy() { return isEnemy; }

    public void setInOpponentsCastle(boolean inOpponentsCastle) {
        isInOpponentsCastle = inOpponentsCastle;
    }

    public static boolean isAnyoneOnCell(List<Hero> heroes, int x, int y) {
        for (Hero hero : heroes) {
            if (hero.getY() == y && hero.getX() == x) {
                return true;
            }
        }
        return false;
    }

    public void setHeroesStamina(int i) {
        heroesStamina = i;
    }
}