package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import GameProcess.*;

class FieldTest {
    private Field field;

    @BeforeEach
    void setUp() {
        field = new Field(10, 10, "default");
        field.setDefaultField();
    }

    @Test
    void testFieldInitialization() {
        assertEquals(10, field.getHeightOfField());
        assertEquals(10, field.getWidthOfField());
    }

    @Test
    void testSetAndGetItem() {
        field.setItem(5, 5, 'X');
        assertEquals('X', field.getItem(5, 5));
    }

    @Test
    void testCollectGold() {
        field.setItem(3, 3, '*');
        int gold = field.collectGold(3, 3);
        assertTrue(gold >= 100 && gold <= 1000);
        assertEquals('.', field.getItem(3, 3));
    }

    @Test
    void testSetDefaultField() {
        assertEquals('И', field.getItem(0, 0));
        assertEquals('К', field.getItem(9, 9));

        boolean hasObstacles = false;
        boolean hasGold = false;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (field.getItem(i, j) == '^') hasObstacles = true;
                if (field.getItem(i, j) == '*') hasGold = true;
            }
        }
        assertTrue(hasObstacles);
        assertTrue(hasGold);
    }
}