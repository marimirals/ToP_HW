package test;

import characters.*;
import GameProcess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;
    private Field field;
    private Enemy enemy;
    private BlackKnight bk;

    @BeforeEach
    void setUp() {
        field = new Field(10, 10, "default");
        player = new Player("TestName", field);
        enemy = new Enemy(9, 9, field);
        bk = new BlackKnight();
    }

    @Test
    void testInitialPlayerState() {
        assertEquals(5000, player.getPlayersGold());
        assertEquals(1, player.getPlayersNumOfHeroes());
        assertFalse(player.getPlayersHeroes().isEmpty());
    }

    @Test
    void testAddHero() {
        player.addHero('b', 0, 0, field);
        assertEquals(2, player.getPlayersNumOfHeroes());
        assertNotNull(player.selectHero('b'));
    }

    @Test
    void testAddHeroWithDuplicateSymbol() {
        player.addHero('a', 0, 0, field); // 'a' уже существует
        assertEquals(1, player.getPlayersNumOfHeroes()); // Количество героев не должно измениться
    }

    @Test
    void testRemoveHero() {
        player.removeHero('a');
        assertEquals(0, player.getPlayersNumOfHeroes());
        assertNull(player.selectHero('a'));
    }

    @Test
    void testSetPlayersGold() {
        player.setPlayersGold(1000);
        assertEquals(1000, player.getPlayersGold());
    }

    @Test
    void testDecreaseCastleCaptureTimeModifier() {
        Hero hero = player.getPlayersHeroes().get(0);
        assertFalse(hero.getFastCastleCapturing()); // Изначально false
        player.decreaseCastleCaptureTimeModifier();
        assertTrue(hero.getFastCastleCapturing()); // Должно стать true
    }
}