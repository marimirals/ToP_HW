package characters;

import GameProcess.Field;
import GameProcess.buildings.Buildings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy {

    private final int MAX_HEROES = 1;
    private List<Hero> heroes;
    private int enemyNumOfHeroes = 1;
    private int enemyGold = 4900;

    // Флаги построенных зданий
    protected boolean stableBuilt = false;
    public boolean watchtowerBuilt = false;
    public boolean crossbowTowerBuilt = false;
    public boolean armoryBuilt = false;
    public boolean arenaBuilt = false;
    public boolean cathedralBuilt = false;
    protected boolean trainingFieldBuilt = false;

    public Enemy(int xForEnemyCastle, int yForEnemyCastle, Field field) {
        heroes = new ArrayList<>();
        heroes.add(new Hero('A', xForEnemyCastle, yForEnemyCastle, true, false, field));
    }

    public List<Hero> getHeroes() {
        return heroes;
    }

    public int getEnemyMAX_HEROES() {
        return MAX_HEROES;
    }

    public int getEnemyGold() {
        return enemyGold;
    }

    public int getEnemyNumOfHeroes() {
        return enemyNumOfHeroes;
    }

    public void setEnemyNumOfHeroes(int newNumOfHeroes) {
        enemyNumOfHeroes = newNumOfHeroes;
    }

    public void addGold(int amount) {
        enemyGold += amount;
        System.out.println("Enemy получает " + amount + " золота. Теперь золота: " + enemyGold);
    }

    public void minusGold(int amount) {
        enemyGold -= amount;
        System.out.println("Enemy тратит " + amount + " золота. Остаток: " + enemyGold);
    }

    public void addHero(char symbol, int x, int y, Field field) {
        if (enemyNumOfHeroes >= MAX_HEROES) {
            System.out.println("Enemy достиг максимального числа героев (" + MAX_HEROES + ").");
            return;
        }

        boolean isAnyHeroInCastle = false;
        for (Hero hero : heroes) {
            if (hero.isInCastle()) {
                isAnyHeroInCastle = true;
                break;
            }
        }

        if (!isAnyHeroInCastle) {
            System.out.println("Enemy: Невозможно нанять героя, так как нет героя в замке.");
            return;
        }

        Buildings tavern = Buildings.createTavern();
        Random random = new Random();
        int heroesToHire = random.nextInt(2) + 1;

        for (int i = 0; i < heroesToHire; i++) {
            char newHeroSymbol = (char) ('A' + enemyNumOfHeroes + i);
            if (enemyGold >= tavern.getCostOfUnit()) {
                Hero newHero = new Hero(newHeroSymbol, x, y, true, false, field);
                heroes.add(newHero);
                enemyNumOfHeroes++;
                minusGold(tavern.getCostOfUnit() + 50);
                System.out.println("Enemy нанял героя " + newHeroSymbol + " в замке (" + x + ", " + y + ").");
            } else {
                System.out.println("Enemy: Недостаточно золота для найма нового героя.");
                break;
            }
        }
    }

    public int[] findNearestGold(int currentX, int currentY, Field field) {
        int goldX = -1, goldY = -1, minDist = Integer.MAX_VALUE;
        for (int i = 0; i < field.getHeightOfField(); i++) {
            for (int j = 0; j < field.getWidthOfField(); j++) {
                Character cell = field.getItem(i, j);
                if (cell != null && cell == '*') {
                    int distance = Math.abs(currentX - i) + Math.abs(currentY - j);
                    if (distance < minDist) {
                        minDist = distance;
                        goldX = i;
                        goldY = j;
                    }
                }
            }
        }
        if (goldX == -1 || goldY == -1) {
            return null;
        }
        return new int[]{goldX, goldY};
    }

    public void buildStructure() {
        int buildingCost = 500;
        if (enemyGold < buildingCost) {
            System.out.println("Enemy: Недостаточно золота для постройки здания.");
            return;
        }

        if (!watchtowerBuilt) {
            watchtowerBuilt = true;
            minusGold(buildingCost);
            System.out.println("Enemy построил Сторожевой пост.");
        } else if (!crossbowTowerBuilt) {
            crossbowTowerBuilt = true;
            minusGold(buildingCost);
            System.out.println("Enemy построил Башню арбалетчиков.");
        } else if (!armoryBuilt) {
            armoryBuilt = true;
            minusGold(buildingCost);
            System.out.println("Enemy построил Оружейную.");
        } else if (!stableBuilt) {
            stableBuilt = true;
            minusGold(buildingCost);
            System.out.println("Enemy построил Конюшню.");
        } else if (!arenaBuilt) {
            arenaBuilt = true;
            minusGold(buildingCost);
            System.out.println("Enemy построил Арену.");
        } else if (!cathedralBuilt) {
            cathedralBuilt = true;
            minusGold(buildingCost);
            System.out.println("Enemy построил Собор.");
        } else if (!trainingFieldBuilt) {
            trainingFieldBuilt = true;
            minusGold(buildingCost);
            System.out.println("Enemy построил Поле.");
        } else {
            System.out.println("Enemy: Все здания уже построены.");
        }
    }

    public boolean isAnyHeroInCastle(Field field) {
        int castleX1 = 0;
        int castleY1 = 0;
        int castleX2 = field.getHeightOfField() - 1;
        int castleY2 = field.getWidthOfField() - 1;

        for (Hero hero : heroes) {
            if ((hero.getX() == castleX1 && hero.getY() == castleY1) ||
                    (hero.getX() == castleX2 && hero.getY() == castleY2)) {
                return true;
            }
        }
        return false;
    }

    public void buyUnitsForRandomHeroInCastle(GameUnit unit, int unitCost) {
        List<Hero> heroesInCastle = new ArrayList<>();
        for (Hero hero : heroes) {
            if (hero.isInCastle()) {
                heroesInCastle.add(hero);
            }
        }

        if (heroesInCastle.isEmpty()) {
            System.out.println("Enemy: Невозможно нанять юнитов, так как нет героев в замке.");
            return;
        }

        if (enemyGold >= unitCost) {
            Random random = new Random();
            Hero randomHero = heroesInCastle.get(random.nextInt(heroesInCastle.size()));
            randomHero.addUnit(unit);
            minusGold(unitCost);
            System.out.println("Enemy: Герой " + randomHero.getSymbol() + " получил " + unit.getType() + ".");
        } else {
            System.out.println("Enemy: Недостаточно золота для найма юнита " + unit.getType() + ".");
        }
    }
}
