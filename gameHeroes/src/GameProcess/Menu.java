package GameProcess;

import characters.Enemy;
import characters.Hero;
import characters.Player;

import java.util.List;

public class Menu {
    public static void displayOptionsAndStatistics(Player player, Enemy enemy) {

        String Options = "\nМенюшка\n" +
                "1 - Войти в замок\n" +
                "2 - Передвинуть героя\n" +
                "3 - Герои игрока\n" +
                "4 - Герои бота\n" +
                "5 - Продолжить игру\n" +
                "6 - Сыграть в карты :0\n" +
                "8 - Сохранение игры\n";

        String Statistics = "\nСтатистика\n" +
                "Золото игрока: " + player.getPlayersGold() +
                "\nКоличество героев игрока: " + player.getPlayersNumOfHeroes() +
                "\nЗолото бота: " + enemy.getEnemyGold() +
                "\nКоличество героев бота: " + enemy.getEnemyNumOfHeroes();

        String[] optionsColumn = Options.split("\n");
        String[] statisticsColumn = Statistics.split("\n");

        int maxLeftLength = 40;
        int padding = maxLeftLength + 5;

        for (int i = 0; i < Math.max(optionsColumn.length, statisticsColumn.length); i++) {
            String leftLine = (i < optionsColumn.length) ? optionsColumn[i] : "";
            System.out.print("\u001B[38;5;130m" + leftLine + "\u001B[0m");
            int spaces = padding - leftLine.length();
            for (int j = 0; j < spaces; j++) {
                System.out.print(" ");
            }
            String rightLine = (i < statisticsColumn.length) ? statisticsColumn[i] : "";
            System.out.println("\u001B[38;5;130m" + rightLine + "\u001B[0m");
        }
        System.out.println();
    }

    public static void showInfoAboutHeroes(Player player) {
        player.displayHeroes();
    }

    public static void showInfoAboutEnemyHeroes(Enemy enemy) {
        List<Hero> enemyHeroesList = enemy.getHeroes();
        for (Hero currHero : enemyHeroesList) {
            currHero.displayArmy(currHero);
        }
    }

    public static void showHighScores() {
        System.out.println("\n=== ТОП-5 РЕКОРДОВ ===");
        List<String> topScores = ScoreManager.getTopScores();
        if (topScores.isEmpty()) {
            System.out.println("Рекордов пока нет!");
        } else {
            for (int i = 0; i < topScores.size(); i++) {
                String[] parts = topScores.get(i).split(",");
                System.out.printf("%d. %s - %d очков (карта: %s)\n",
                        i + 1, parts[0], Integer.parseInt(parts[1]), parts[2]);
            }
        }
    }
    // чистка консоли
}
