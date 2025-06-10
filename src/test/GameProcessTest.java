package test;

import characters.*;
import GameProcess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class GameProcessTest {
    private GameProcess game;
    private Player player;
    private Enemy enemy;
    private BlackKnight bk;
    private Field field;

    @BeforeEach
    void setUp() {
        field = new Field(10, 10, "default");
        player = new Player("testPlayer", field);
        enemy = new Enemy(9, 9, field);
        bk = new BlackKnight();
        game = new GameProcess(player, enemy, 10, 10, bk);
    }

    @Test
    void testGameFinished_PlayerNoHeroes() {
        player.getPlayersHeroes().clear();
        player.setPlayersNumOfHeroes(0);
        assertTrue(game.checkIfGameNotFinished(),
                "Игра должна завершиться, когда у игрока нет героев");
    }

    @Test
    void testGameFinished_EnemyNoHeroes() {
        enemy.getHeroes().clear();
        enemy.setEnemyNumOfHeroes(0);
        assertTrue(game.checkIfGameNotFinished(),
                "Игра должна завершиться, когда у врага нет героев");
    }

    @Test
    void testGameFinished_EnemyInPlayerCastle() {
        // враг в замке игрока (0,0)
        Hero enemyHero = enemy.getHeroes().get(0);
        enemyHero.setX(0);
        enemyHero.setY(0);
        assertTrue(game.checkIfGameNotFinished(),
                "Игра должна завершиться, когда враг в замке игрока");
    }

    @Test
    void testGameFinished_PlayerInEnemyCastle() {
        // герой в замке врага (9,9)
        Hero playerHero = player.getPlayersHeroes().get(0);
        playerHero.setX(9);
        playerHero.setY(9);
        assertTrue(game.checkIfGameNotFinished(),
                "Игра должна завершиться, когда игрок в замке врага");
    }

    @Test
    void testFightBetweenHeroes() {
        Field testField = new Field(10, 10, "test");

        Hero playerHero = new Hero('P', 5, 5, false, false, testField);
        Hero enemyHero = new Hero('E', 5, 5, true, false, testField);

        playerHero.addUnit(new GameUnit("Копейщик", 1, 100, 10, 2, 1));
        enemyHero.addUnit(new GameUnit("Копейщик", 1, 100, 10, 2, 1));

        player.getPlayersHeroes().add(playerHero);
        player.setPlayersNumOfHeroes(player.getPlayersNumOfHeroes() + 1);

        enemy.getHeroes().add(enemyHero);
        enemy.setEnemyNumOfHeroes(enemy.getEnemyNumOfHeroes() + 1);

        int initialPlayerHeroes = player.getPlayersNumOfHeroes();
        int initialEnemyHeroes = enemy.getEnemyNumOfHeroes();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            game.fight(playerHero, enemyHero, player, enemy);

            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains("=== Начинается сражение ==="),
                    "Должно быть сообщение о начале боя");
            assertTrue(consoleOutput.contains("Герой P (игрок) vs Герой E (враг)"),
                    "Должно быть указание на участников боя");

            // герой был удален
            assertTrue(player.getPlayersNumOfHeroes() < initialPlayerHeroes ||
                            enemy.getEnemyNumOfHeroes() < initialEnemyHeroes,
                    "Один из героев должен был погибнуть");
        } finally {
            System.setOut(originalOut);
        }
    }
}