import GameProcess.*;
import bonjourMadame.*;
import characters.BlackKnight;
import characters.Enemy;
import characters.Player;

import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {

        LoggerConfig.setup();
        Logger logger = Logger.getLogger(Main.class.getName());
        logger.info("Запуск игры...");

        System.out.println("\u001B[32m" + "Heroes of IUIII");
        System.out.println("Жмякай 'Enter', чтобы начать, или 'Q', чтобы выйти" + "\u001B[0m");

        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Введите имя игрока:");
        String currName = scanner.nextLine();

        int tempHeight = 10;
        int tempWidth = 10;

        Field tempField = new Field(tempHeight, tempWidth, "default");
        tempField.setDefaultField();
        Player player = new Player(currName, tempField);
        GameProcess gameProcess = new GameProcess(player, null, tempHeight, tempWidth, null);

        do {
            command = scanner.nextLine();
            switch (command.toUpperCase()) {
                case "":
                    System.out.println("(1): Сохранения \n(2): Создать свою карту\n" +
                            "(3): Создать карту автоматически\n(4): Играть в 'Бонжур, Мадам!'\n(5): Глянуть рекорды" +
                            "\n(6): Выбрать карту");
                    int command2 = scanner.nextInt();
                    if (command2 == 1) {
                        GameProcess.loadGameMenu(player.getName());
                    }
                    else if (command2 == 2) {
                        MapEditor me = gameProcess.getMapEditor();
                        me.start(gameProcess);
                    }
                    else if (command2 == 4) {
                        StartCardGame.main();
                    }
                    else if (command2 == 5) {
                        Menu.showHighScores();
                    }
                    else if (command2 == 6) {
                        MapEditor me = gameProcess.getMapEditor();

                        Field selectedMap = me.selectMapToPlay();
                        if (selectedMap == null) {
                            continue;
                        }

                        gameProcess.setCurrentField(selectedMap);
                        gameProcess.setHeightOfField(selectedMap.getHeightOfField());
                        gameProcess.setWidthOfField(selectedMap.getWidthOfField());

                        Enemy enemy = new Enemy(selectedMap.getHeightOfField() - 1,
                                selectedMap.getWidthOfField() - 1,
                                gameProcess.getFieldOfGameProcess());
                        BlackKnight bk = new BlackKnight();

                        gameProcess.setPlayer(player);
                        gameProcess.setEnemy(enemy);
                        gameProcess.setBlackKnight(bk);

                        gameProcess.startGame(selectedMap.getMapName());
                    }
                    else {
                        Field tempFieldAgain = new Field(tempHeight, tempWidth, "default");
                        tempFieldAgain.setDefaultField();

                        // Then create the enemy with this field
                        Enemy enemy = new Enemy(tempHeight - 1, tempWidth - 1, tempFieldAgain);
                        BlackKnight bk = new BlackKnight();

                        gameProcess.setPlayer(player);
                        gameProcess.setEnemy(enemy);
                        gameProcess.setBlackKnight(bk);
                        gameProcess.setCurrentField(tempFieldAgain);  // Make sure to set the current field

                        gameProcess.startGame("default");

                        System.out.println("Конец игры получается.");
                        command = "Q";
                    }
                    break;
                case "Q":
                    break;
                default:
                    System.out.println("Please type a real command.");
                    break;
            }
        } while (!command.equalsIgnoreCase("Q"));

        scanner.close();
        System.out.println("Спасибо за игру! До свидания!");
    }
}