package characters;

import GameProcess.Field;
import GameProcess.GameProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Player {

    private final int MAX_HEROES = 3;
    private List<Hero> heroes;
    private int playersNumOfHeroes;
    private int playersGold;
    private String Name;

    public Player(String name, Field field) {
        this.heroes = new ArrayList<>();
        this.playersNumOfHeroes = 0;
        this.playersGold = 5000;
        this.Name = name;
        addHero('a', 0, 0, field);
    }

    public void setName(String newName) {
        this.Name = newName;
    }

    public String getName() {
        return Name;
    }

    public int getPlayersNumOfHeroes() {
        return playersNumOfHeroes;
    }

    public void setPlayersNumOfHeroes(int newNumberOfHeroes) {
        this.playersNumOfHeroes = newNumberOfHeroes;
    }

    public List<Hero> getPlayersHeroes() {
        return heroes;
    }

    public int getPlayersGold() {
        return playersGold;
    }

    public void setPlayersGold(int newGold) {
        this.playersGold = newGold;
    }

    public void addHero(char symbol, int x, int y, Field field) {
        if (playersNumOfHeroes >= MAX_HEROES) {
            System.out.println("Максимальное количество героев (" + MAX_HEROES + ") уже достигнуто.");
            return;
        }
        for (Hero hero : heroes) {
            if (hero.getSymbol() == symbol) {
                System.out.println("Герой с символом " + symbol + " уже существует.");
                return;
            }
        }
        Hero newHero = new Hero(symbol, x, y, false, false, field);
        heroes.add(newHero);
        playersNumOfHeroes++;
        System.out.println("Герой " + symbol + " добавлен.");
    }

    public Hero selectHero(char symbol) {
        for (Hero hero : heroes) {
            if (hero.getSymbol() == symbol) {
                return hero;
            }
        }
        System.out.println("Герой с именем '" + symbol + "' не найден.");
        return null;
    }

    public void removeHero(char symbol) {
        Hero toRemove = null;
        for (Hero hero : heroes) {
            if (hero.getSymbol() == symbol) {
                toRemove = hero;
                break;
            }
        }
        if (toRemove != null) {
            heroes.remove(toRemove);
            playersNumOfHeroes--;
            System.out.println("Герой '" + symbol + "' удален.");
        } else {
            System.out.println("Герой с символом '" + symbol + "' не найден.");
        }
    }

    public void displayHeroes() {
        System.out.println("Текущие герои:");
        if (heroes.isEmpty()) {
            System.out.println("Героев нема.");
        } else {
            for (Hero hero : heroes) {
                System.out.println("\u001B[32m" + "Герой '" + hero.getSymbol() + "' находится в позиции (" +
                        hero.getX() + ", " + hero.getY() + ")" + "\u001B[0m");
                System.out.println("Выносливость героя " +
                        hero.getSymbol() + ": " + hero.getHeroesStamina());
                hero.displayArmy(hero);
            }
        }
        System.out.println();
    }

    public void moveHero(Player player, Enemy enemy, Field field, int heightOfField, int widthOfField, char symbol, int newX, int newY, GameProcess gp) {
        Logger logger = Logger.getLogger(Player.class.getName());
        logger.info("Попытка перемещения героя " + symbol + " в координаты (" + newX + ", " + newY + ")");

        Hero hero = selectHero(symbol);
        if (hero != null && !hero.isMoveIsMade()) {
            hero.move(player, enemy, hero, field, heightOfField, widthOfField, newX, newY, gp);
        } else if (hero != null && hero.isMoveIsMade()) {
            System.out.println("Герой уже переместился на этом ходе.");
        }
    }

    public void decreaseCastleCaptureTimeModifier() {
        for (Hero h : heroes) {
            h.setFastCastleCapturing(true);
        }
    }
}
