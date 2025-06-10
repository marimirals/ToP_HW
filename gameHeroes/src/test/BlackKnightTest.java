package test;

import characters.BlackKnight;
import characters.Hero;
import characters.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import GameProcess.*;

import java.util.ArrayList;

class BlackKnightTest {
    private BlackKnight bk;
    private Player player;
    private Field field;
    private String mapName = "testMap";

    @BeforeEach
    void setUp() {
        bk = new BlackKnight();
        field = new Field(10, 10, mapName);
        player = new Player("TestPlayer", field);
        field.setDefaultField();
    }

    @Test
    void testInitialState() {
        assertEquals(1.0, bk.getBlackKnightSpawnChance(), 0.001);
        assertEquals(350, bk.getBlackKnightRansom());
        assertFalse(bk.blackKnightHasAppeared);
        assertFalse(bk.isDead());
    }

    @Test
    void testDecreaseSpawnChance() {
        bk.decreaseBlackKnightSpawnChance(0.1);
        assertEquals(0.9, bk.getBlackKnightSpawnChance(), 0.001);
    }

    @Test
    void testSetRansom() {
        bk.setBlackKnightRansom(200); // выкуп
        assertEquals(200, bk.getBlackKnightRansom());
    }

    @Test
    void testSpawnBlackKnight() {
        ArrayList<Hero> heroes = new ArrayList<>();
        heroes.add(new Hero('a', 0, 0, false, false, field));

        bk.spawnBlackKnight(player, heroes, field);
        assertTrue(bk.getKnightX() >= 0 && bk.getKnightX() < 10);
        assertTrue(bk.getKnightY() >= 0 && bk.getKnightY() < 10);
    }

    @Test
    void testBanKnightAction() {
        try {
            assertNotNull(bk.getClass().getDeclaredMethod("banKnightAction", Player.class)); // получаем класс обьекта и ищем там метод
        } catch (NoSuchMethodException e) {
            fail("Method banKnightAction not found");
        }
    }
}