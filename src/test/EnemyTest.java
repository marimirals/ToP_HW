package test;

import characters.Enemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import GameProcess.*;

class EnemyTest {
    private Enemy enemy;
    private Field field;

    @BeforeEach
    void setUp() {
        field = new Field(10, 10, "default");
        field.setDefaultField();
        enemy = new Enemy(9, 9, field);
    }

    @Test
    void testInitialEnemyState() {
        assertEquals(4900, enemy.getEnemyGold());
        assertEquals(1, enemy.getEnemyNumOfHeroes());
        assertFalse(enemy.getHeroes().isEmpty());
    }

    @Test
    void testAddHero() {
        enemy.addHero('B', 9, 9, field);
        assertEquals(1, enemy.getEnemyNumOfHeroes());
    }

    @Test
    void testAddGold() {
        enemy.addGold(1000);
        assertEquals(5900, enemy.getEnemyGold());
    }

    @Test
    void testMinusGold() {
        enemy.minusGold(1000);
        assertEquals(3900, enemy.getEnemyGold());
    }

    @Test
    void testFindNearestGold() {
        int[] goldCoords = enemy.findNearestGold(5, 5, field);
        assertNotNull(goldCoords);
        assertEquals(2, goldCoords.length);
    }

    @Test
    void testBuildStructure() {
        enemy.buildStructure();
        assertTrue(enemy.watchtowerBuilt || enemy.crossbowTowerBuilt ||
                enemy.armoryBuilt || enemy.arenaBuilt || enemy.cathedralBuilt);
    }
}