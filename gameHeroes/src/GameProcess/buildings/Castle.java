package GameProcess.buildings;

import GameProcess.*;
import characters.BlackKnight;
import characters.Player;

import java.util.Scanner;

public class Castle {

    private Buildings playersTavern;
    private Buildings playersStable;
    private Buildings playersWatchtower;
    private Buildings playersCrossbowTower;
    private Cafe castleCafe;
    private Hotel castleHotel;
    private BarberShop castleBarberShop;
    private Buildings playersWizardAgency;

    Scanner scanner = new Scanner(System.in);

    public Castle(Player player) {
        this.playersTavern = Buildings.createTavern();
        this.playersStable = Buildings.createStable();
        this.playersWatchtower = Buildings.createWatchtower();
        this.playersCrossbowTower = Buildings.createCrossbowTower();
        this.castleCafe = new Cafe(player);
        this.castleHotel = new Hotel(player);
        this.castleBarberShop = new BarberShop(player);
        this.playersWizardAgency = Buildings.createWizardAgency();
    }

    // Вход в замок
    public void enterCastle() {
        String tavernStatus = (this.playersTavern.isExist()) ? "\u001B[32m" : "\u001B[31m";
        String stableStatus = (this.playersStable.isExist()) ? "\u001B[32m" : "\u001B[31m";
        String watchtowerStatus = (this.playersWatchtower.isExist()) ? "\u001B[32m" : "\u001B[31m";
        String crossbowtowerStatus = (this.playersCrossbowTower.isExist()) ? "\u001B[32m" : "\u001B[31m";
        String castleCafeStatus = "\u001B[32m";
        String arenaStatus = "\u001B[32m";
        String cathedralStatus = "\u001B[32m";
        String trainingfieldStatus = (this.playersWizardAgency.isExist()) ? "\u001B[32m" : "\u001B[31m";
        System.out.println(
                "\u001B[31m" + "(/// - здания недоступны, " + "\u001B[32m" + "/// - здания доступны.)\n\n" +
                        tavernStatus + "Таверна  " +
                        stableStatus + "\tКонюшня  " +
                        watchtowerStatus + "\tСторожевой пост  " +
                        crossbowtowerStatus + "\tБашня арбалетчиков" +
                        castleCafeStatus + "\n\nКафе          " +
                        arenaStatus + "\t\t\t\t\t\t\t\t\t\tОтель" +
                        cathedralStatus + "\n\nБарбишоп\t\t\t\t\t\t" +
                        "\u001B[33m" + "|\\/\\/|\t   " +
                        trainingfieldStatus + "\t\tАгенство чародеев\n" + "\u001B[0m");
    }

    public void choiceInCastle(Player player, BlackKnight bk, Field field) {
        int choice;
        do {
            System.out.println("Купить здание (1), Войти в здание (2), Показать характеристики зданий (3), Выйти из замка (4)?");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    buyABuilding(player);
                    break;
                case 2:
                    enterTheBuilding(player, bk, field);
                    break;
                case 3:
                    System.out.println("Пока в разработке");
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Неверная команда. Попробуйте еще раз.");
            }
        } while (choice != 4);
    }

    public void enterTheBuilding(Player player, BlackKnight bk, Field field) {
        int chooseBuilding;
        do {
            System.out.println("В какое здание идем?\n1 - Таверна\n2 - Конюшня\n3 - Сторожевой пост\n" +
                    "4 - Башня арбалетчиков\n5 - Кафе\n6 - Отель\n7 - Барбишоп\n8 - Агенство чародеев\n9 - Передумали и не идём.");
            chooseBuilding = scanner.nextInt();
            switch (chooseBuilding) {
                case 1:
                    if (Buildings.handleBuildingPurchase(playersTavern, player)) {
                        Buildings.handleTavernActions(playersTavern, player, field);
                    }
                    break;
                case 2:
                    if (Buildings.handleBuildingPurchase(playersStable, player)) {
                        Buildings.handleStableActions(playersStable, player);
                    }
                    break;
                case 3:
                    if (Buildings.handleBuildingPurchase(playersWatchtower, player)) {
                        Buildings.handleWatchtowerActions(playersWatchtower, player);
                    }
                    break;
                case 4:
                    if (Buildings.handleBuildingPurchase(playersCrossbowTower, player)) {
                        Buildings.handleCrossbowTowerActions(playersCrossbowTower, player);
                    }
                    break;
                case 5:
                    castleCafe.showLoadStatus();
                    castleCafe.handleCafeActions();
                    break;
                case 6:
                    castleHotel.showLoadStatus();
                    castleHotel.handleHotelActions();
                    break;
                case 7:
                    castleBarberShop.showLoadStatus();
                    castleBarberShop.handleBarberShopActions();
                    break;
                case 8:
                    if (Buildings.handleBuildingPurchase(playersWizardAgency, player)) {
                        Buildings.handleWizardAgencyActions(playersWizardAgency, player, bk);
                    }
                    break;
                case 9:
                    enterCastle();
                    break;
                default:
                    System.out.println("Неверная команда. Попробуйте еще раз.");
            }
        } while (chooseBuilding != 9);
    }

    public void buyABuilding(Player player) {
        int buyABuildingChoice;
        do {
            System.out.println("Какое здание покупаем?\n1 - Таверна, 2 - Конюшня, 3 - Сторожевой пост, " +
                    "4 - Башня арбалетчиков, 8 - Агенство ародеев, 9 - Передумали, не покупаем.");
            buyABuildingChoice = scanner.nextInt();
            switch (buyABuildingChoice) {
                case 1:
                    Buildings.handleBuildingPurchase(playersTavern, player);
                    break;
                case 2:
                    Buildings.handleBuildingPurchase(playersStable, player);
                    break;
                case 3:
                    Buildings.handleBuildingPurchase(playersWatchtower, player);
                    break;
                case 4:
                    Buildings.handleBuildingPurchase(playersCrossbowTower, player);
                    break;
                case 8:
                    Buildings.handleBuildingPurchase(playersWizardAgency, player);
                    break;
                case 9:
                    enterCastle();
                    break;
            }
        } while (buyABuildingChoice != 9);
    }

}
