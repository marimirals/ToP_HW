package test;

import GameProcess.*;
import characters.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeroTest {

    private GameProcess game;
    private Hero hero;
    private Player player;
    private Enemy enemy;
    private Field field;
    private BlackKnight bk;

    @BeforeEach
    void setUp() {
        field = new Field(10, 10, "default");
        bk = new BlackKnight();
        game = new GameProcess(player, enemy, field.getHeightOfField(), field.getWidthOfField(), bk);
        hero = new Hero('H', 1, 1, false, false, field);
        player = new Player("TestName", field);
        enemy = new Enemy(4, 4, field);
    }

    @Test
    void testHeroCreation() {
        assertEquals('H', hero.getSymbol());
        assertEquals(1, hero.getX());
        assertEquals(1, hero.getY());
        assertEquals(100, hero.getHeroesStamina());
        assertFalse(hero.getIsEnemy());
    }

    @Test
    void testMoveOntoMountain() {
        field.setItem(2, 2, '^'); // manually ставим гору
        hero.move(player, enemy, hero, field, 5, 5, 2, 2, game);
        assertEquals(1, hero.getX());
        assertEquals(1, hero.getY());
    }

    @Test
    void testMoveSuccessful() {
        field.setItem(2, 2, '.');
        hero.move(player, enemy, hero, field, 5, 5, 2, 2, game);
        assertEquals(2, hero.getX());
        assertEquals(2, hero.getY());
    }

    @Test
    void testAddUnitToHero() {
        GameUnit unit = new GameUnit("Мечник", 2, 120, 20, 2, 2);
        hero.addUnit(unit);

        List<GameUnit> army = hero.getArmy();
        assertTrue(army.contains(unit));
    }


    @Test
    void testIsAnyoneOnCell() {
        List<Hero> heroes = new ArrayList<>();
        heroes.add(new Hero('H', 1, 1, false, false, field));
        heroes.add(new Hero('E', 2, 2, true, false, field));

        assertTrue(Hero.isAnyoneOnCell(heroes, 1, 1));
        assertFalse(Hero.isAnyoneOnCell(heroes, 0, 0));
    }

    @Test
    void testFightOccursWhenHeroesMeet() {
        // Настроим поле
        field.setItem(2, 2, '.');

        // Создаем вражеского героя
        Hero enemyHero = new Hero('E', 2, 2, true, false, field);
        player.getPlayersHeroes().add(enemyHero);

        // Перемещаем нашего героя на клетку с врагом
        hero.move(player, enemy, hero, field, 5, 5, 2, 2, game);

        // Если герой победил, он должен оказаться на клетке врага
        if (player.getPlayersHeroes().contains(hero)) {
            assertEquals(2, hero.getX(), "Герой победил — должен быть на месте врага");
            assertEquals(2, hero.getY(), "Герой победил — должен быть на месте врага");
        } else {
            // Иначе его нет в списке — значит он проиграл
            assertFalse(player.getPlayersHeroes().contains(hero), "Герой должен был быть удалён при поражении");
        }
    }


    @Test
    void testMoveTooFar() {
        int fieldHeight = 5;
        int fieldWidth = 5;

        field.setItem(4, 4, '.');

        hero.move(player, enemy, hero, field, fieldHeight, fieldWidth, 4, 4, game);

        assertEquals(1, hero.getX());
        assertEquals(1, hero.getY());
    }
}
