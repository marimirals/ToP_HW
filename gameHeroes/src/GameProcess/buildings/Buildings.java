package GameProcess.buildings;

import java.util.List;
import java.util.Scanner;

import GameProcess.*;
import characters.GameUnit;
import characters.Hero;
import characters.BlackKnight;
import characters.Player;

public class Buildings {

    private String type;
    private int buildingCost;
    private int costOfUnit;
    public boolean exist = false;
    private String description;
    static Scanner scanner = new Scanner(System.in);

    // Приватный конструктор, дабы не создавать новые виды зданий
    public Buildings(String type, int buildingCost, int costOfUnit, String description) {
        exist = false;
        this.buildingCost = buildingCost;
        this.costOfUnit = costOfUnit;
        this.type = type;
        this.description = description;
    }

    public static Buildings createTavern() {
        return new Buildings("Таверна", 100, 500, "Используется для найма Героев.");
    }

    public static Buildings createStable() {
        return new Buildings("Конюшня", 100, 500, "Увеличивает дальность перемещения для всех Героев, посетивших замок.");
    }

    public static Buildings createWatchtower() {
        return new Buildings("Сторожевой пост", 100, 50, "Используется для найма юнитов 1 уровня: копейщиков.");
    }

    public static Buildings createCrossbowTower() {
        return new Buildings("Башня арбалетчиков", 100, 50, "Используется для найма юнитов 2 уровня: арбалетчиков.");
    }

    public static Buildings createArmory() {
        return new Buildings("Оружейная", 100, 50, "Используется для найма юнитов 3 уровня: мечников.");
    }

    public static Buildings createArena() {
        return new Buildings("Арена", 100, 50, "Используется для найма юнитов 4 уровня: кавалеристов.");
    }

    public static Buildings createCathedral() {
        return new Buildings("Собор", 100, 50, "Используется для найма юнитов 5 уровня: паладинов.");
    }

    public static Buildings createWizardAgency() {
        return new Buildings("Агенство Чародеев", 1000, 1000000, "Агенство Чародеев");
    }

    public static boolean handleBuildingPurchase(Buildings building, Player player) {
        if (!building.isExist()) {
            System.out.println("Вы еще не открыли здание '" + building.getType() + "'. Купить за " + building.getBuildingCost() +
                    "? (Ваше Золото: " + player.getPlayersGold() + ") (1 - да, 2 - нет)");
            int buyingABuilding;
            do {
                buyingABuilding = scanner.nextInt();
                switch (buyingABuilding) {
                    case 1:
                        player.setPlayersGold(player.getPlayersGold() - building.buildingCost);
                        building.setExist();
                        return true; // Здание куплено
                    case 2:
                        System.out.println("пока-пока");
                        return false; // Здание не куплено
                    default:
                        System.out.println("Неверная команда. Попробуйте еще раз.");
                }
            } while (buyingABuilding != 2);
        } else {
            System.out.println("Куплено!");
        }
        return true; // Здание уже существует
    }

    public static void handleTavernActions(Buildings tavern, Player player, Field field) {
        System.out.println("Вы вошли в таверну.");
        int numOfUnits;
        do {
            System.out.println("Сколько Героев хотите нанять? (0 героев - выход из таверны)\n(Ваше Золото: "
                    + player.getPlayersGold() + ")");
            numOfUnits = scanner.nextInt();
            // проверка на одинаковые имена героев
            int tempNumOfHeroes = player.getPlayersNumOfHeroes();
            if (numOfUnits * Hero.getCostOfHero() + numOfUnits * tavern.getCostOfUnit() <= player.getPlayersGold()) {
                for (int i = 0; i < numOfUnits; i++) {
                    System.out.print("Введите имя героя: ");
                    char nameOfNewHero = scanner.next().charAt(0);
                    player.addHero(nameOfNewHero, 0, 0, field);
                    if (tempNumOfHeroes == player.getPlayersNumOfHeroes()) {
                        i--; // чтобы из-за повторяшек цикл не закончился раньше времени
                    }
                }
                player.setPlayersGold(player.getPlayersGold() - Hero.getCostOfHero() * numOfUnits);
            } else {
                System.out.println("Недостаточно золота. Герой нанимается вместе со своим друганом Копейщиком (+10 золота).");
                System.out.println("Сколько героев хотите нанять?");
            }
        } while (numOfUnits != 0);

    }

    public static void handleStableActions(Buildings stable, Player player) {
        if (player.getPlayersGold() < stable.getCostOfUnit()) {
            System.out.println("Можно было бы увеличить дальность перемещения всех юнитов. " +
                    "Но у Вас не хватает Золота. До свидания.");
        } else {
            System.out.println("Вы вошли в конюшню.");
            int stableAction;
            do {
                System.out.println("Можно увеличить дальность перемещения всех юнитов на 2. (За " + stable.getCostOfUnit() + " голды) " +
                        "Улучшаем? (1 - да, 2 - нет) (Ваше Золото: " + player.getPlayersGold() + ")");
                stableAction = scanner.nextInt();
                switch (stableAction) {
                    case 1:
                        player.setPlayersGold(player.getPlayersGold() - stable.getCostOfUnit());
                        for (Hero currHero : player.getPlayersHeroes()) {
                            List<GameUnit> currHeroArmy = currHero.getArmy();
                            for (GameUnit currGameUnit : currHeroArmy) {
                                currGameUnit.setMovement(currGameUnit.getMovement() + 2);
                            }
                        }
                        break;
                    case 2:
                        break;
                    default:
                        System.out.println("Неправильная команда. Попробуйте еще раз.");
                }
            } while (stableAction != 2);
        }
    }

    public static void handleWatchtowerActions(Buildings watchtower, Player player) {
        System.out.println("Вы вошли в Сторожевой пост.");
        int watchtowerAction;
        do {
            System.out.println("\nМожно нанять Копейщиков. (Ваше Золото: " +
                    player.getPlayersGold() + ")\n" + "Показать хар-ки Копейщиков (1), Нанять Копейщиков (2), Выйти (3)");
            watchtowerAction = scanner.nextInt();
            switch (watchtowerAction) {
                case 1:
                    GameUnit tempUnit = GameUnit.createSpearman();
                    System.out.println(tempUnit.toString());
                    break;
                case 2:
                    System.out.println("Введите имя героя, которому хотите добавить Копейщика: ");
                    Hero currHeroName = player.selectHero(scanner.next().charAt(0));
                    if (!currHeroName.isInCastle()) {
                        System.out.println("Герой не может пополнить армию вне замка!");
                        break;
                    } else {
                        currHeroName.addUnit(GameUnit.createSpearman());
                        player.setPlayersGold(player.getPlayersGold() - watchtower.getCostOfUnit());
                        System.out.println("Готово! Герой '" + currHeroName.getSymbol() + "' теперь дружит с Копейщиком.");
                        player.setPlayersGold(player.getPlayersGold() - watchtower.getCostOfUnit());
                    }
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Неправильная команда. Попробуйте еще раз.");
            }
        } while (watchtowerAction != 3);
    }

    public static void handleCrossbowTowerActions(Buildings crossbowTower, Player player) {
        System.out.println("Вы вошли в башню Арбалетчиков.");
        int crossbowTowerAction;
        do {
            System.out.println("\nМожно нанять Арбалетчиков. (Ваше Золото: " +
                    player.getPlayersGold() + ")\n" + "Показать хар-ки Арбалетчиков (1), Нанять Арбалетчиков (2), Выйти (3)\n");
            crossbowTowerAction = scanner.nextInt();
            switch (crossbowTowerAction) {
                case 1:
                    GameUnit tempUnit = GameUnit.createCrossbowman();
                    tempUnit.toString();
                    break;
                case 2:
                    System.out.println("Введите имя героя, которому хотите добавить Арбалетчика: ");
                    Hero currHeroName = player.selectHero(scanner.next().charAt(0));
                    if (!currHeroName.isInCastle()) {
                        System.out.println("Герой не может пополнить армию вне замка!");
                        break;
                    }
                    currHeroName.addUnit(GameUnit.createCrossbowman());
                    player.setPlayersGold(player.getPlayersGold() - crossbowTower.getCostOfUnit());
                    System.out.println("Готово! Герой '" + currHeroName.getSymbol() + "' теперь дружит с Арбалетчиком   .");
                    player.setPlayersGold(player.getPlayersGold() - crossbowTower.getCostOfUnit());
                    break;
                default:
                    System.out.println("Неправильная команда. Попробуйте еще раз.");
            }
        } while (crossbowTowerAction != 3);
    }

    public static void handleArmoryActions(Buildings armory, Player player) {
        System.out.println("Вы вошли в оружейную.");
        int armoryAction;
        do {
            System.out.println("\nМожно нанять Мечников. (Ваше Золото: " +
                    player.getPlayersGold() + ")\n" + "Показать хар-ки Мечников (1), Нанять Мечников (2), Выйти (3)\n");
            armoryAction = scanner.nextInt();
            switch (armoryAction) {
                case 1:
                    GameUnit tempUnit = GameUnit.createSwordsman();
                    tempUnit.toString();
                    break;
                case 2:
                    System.out.println("Введите имя героя, которому хотите добавить Мечника: ");
                    Hero currHeroName = player.selectHero(scanner.next().charAt(0));
                    if (!currHeroName.isInCastle()) {
                        System.out.println("Герой не может пополнить армию вне замка!");
                        break;
                    }
                    currHeroName.addUnit(GameUnit.createSwordsman());
                    player.setPlayersGold(player.getPlayersGold() - armory.getCostOfUnit());
                    player.setPlayersGold(player.getPlayersGold() - armory.getCostOfUnit());
                    break;
                default:
                    System.out.println("Неправильная команда. Попробуйте еще раз.");
            }
        } while (armoryAction != 3);
    }

    public static void handleArenaActions(Buildings arena, Player player) {
        System.out.println("Вы вошли на Арену.");
        int arenaAction;
        do {
            System.out.println("\nМожно нанять Кавалеристов. (Ваше Золото: " +
                    player.getPlayersGold() + ")\n" + "Показать хар-ки Кавалеристов (1), Нанять Кавалеристов (2), Выйти (3)\n");
            arenaAction = scanner.nextInt();
            switch (arenaAction) {
                case 1:
                    GameUnit tempUnit = GameUnit.createCavalryman();
                    tempUnit.toString();
                    break;
                case 2:
                    System.out.println("Введите имя героя, которому хотите добавить Кавалериста: ");
                    Hero currHeroName = player.selectHero(scanner.next().charAt(0));
                    if (!currHeroName.isInCastle()) {
                        System.out.println("Герой не может пополнить армию вне замка!");
                        break;
                    }
                    currHeroName.addUnit(GameUnit.createCavalryman());
                    player.setPlayersGold(player.getPlayersGold() - arena.getCostOfUnit());
                    player.setPlayersGold(player.getPlayersGold() - arena.getCostOfUnit());
                    break;
                default:
                    System.out.println("Неправильная команда. Попробуйте еще раз.");
            }
        } while (arenaAction != 3);
    }

    public static void handleCathedralActions(Buildings cathedral, Player player) {
        System.out.println("Вы вошли в Собор.");
        int cathedralAction;
        do {
            System.out.println("\nМожно нанять Паладинов. (Ваше Золото: " +
                    player.getPlayersGold() + ")\n" + "Показать хар-ки Паладинов (1), Нанять Паладинов (2), Выйти (3)\n");
            cathedralAction = scanner.nextInt();
            switch (cathedralAction) {
                case 1:
                    GameUnit tempUnit = GameUnit.createPaladin();
                    tempUnit.toString();
                    break;
                case 2:
                    System.out.println("Введите имя героя, которому хотите добавить Паладина: ");
                    Hero currHeroName = player.selectHero(scanner.next().charAt(0));
                    if (!currHeroName.isInCastle()) {
                        System.out.println("Герой не может пополнить армию вне замка!");
                        break;
                    }
                    currHeroName.addUnit(GameUnit.createPaladin());
                    player.setPlayersGold(player.getPlayersGold() - cathedral.getCostOfUnit());
                    player.setPlayersGold(player.getPlayersGold() - cathedral.getCostOfUnit());
                    break;
                default:
                    System.out.println("Неправильная команда. Попробуйте еще раз.");
            }
        } while (cathedralAction != 3);
    }

    public static void handleWizardAgencyActions(Buildings wizardAgency, Player player, BlackKnight bk) {
        System.out.println("Вы вошли в Агенство Чародеев.");
        int wizardAgencyAction;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("\nТекущий шанс появления Черного Рыцаря: " + bk.getBlackKnightSpawnChance() * 100 + "%");
            System.out.println("Текущий выкуп Черного Рыцаря: " + bk.getBlackKnightRansom() + " золота");
            System.out.println("Ваше золото: " + player.getPlayersGold());
            System.out.println("\nВыберите действие:\n" +
                    "1. Уменьшить шанс появления Черного Рыцаря (стоимость: 500 золота)\n" +
                    "2. Уменьшить выкуп Черного Рыцаря (стоимость: 300 золота)\n" +
                    "3. Выйти\n");

            wizardAgencyAction = scanner.nextInt();

            switch (wizardAgencyAction) {
                case 1:
                    // Уменьшение шанса появления Черного Рыцаря
                    if (player.getPlayersGold() >= 500) {
                        bk.decreaseBlackKnightSpawnChance(0.05); // Уменьшаем шанс на 5%
                        player.setPlayersGold(player.getPlayersGold() - 500); // Снимаем золото
                        System.out.println("Шанс появления Черного Рыцаря уменьшен на 5%!");
                    } else {
                        System.out.println("У вас недостаточно золота!");
                    }
                    break;

                case 2:
                    // Уменьшение выкупа Черного Рыцаря
                    if (player.getPlayersGold() >= 300) {
                        bk.setBlackKnightRansom(bk.getBlackKnightRansom() - 50); // Уменьшаем выкуп на 50
                        player.setPlayersGold(player.getPlayersGold() - 300); // Снимаем золото
                        System.out.println("Выкуп Черного Рыцаря уменьшен на 50 золота!");
                    } else {
                        System.out.println("У вас недостаточно золота!");
                    }
                    break;

                case 3:
                    System.out.println("Вы вышли из Агенства Чародеев.");
                    break;

                default:
                    System.out.println("Неправильная команда. Попробуйте еще раз.");
            }
        } while (wizardAgencyAction != 3);
    }

    public String getType() {
        return type;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist() {
        this.exist = true;
    }

    // Метод для отображения информации о здании
    @Override
    public String toString() {
        return type + ": " + description;
    }

    public int getBuildingCost() {
        return buildingCost;
    }

    public int getCostOfUnit() {
        return costOfUnit;
    }
}