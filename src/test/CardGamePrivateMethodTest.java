package test;

import GameProcess.LoggerConfig;
import bonjourMadame.CardGame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class CardGamePrivateMethodTest {
    private static final Logger logger = Logger.getLogger(CardGamePrivateMethodTest.class.getName());

    @BeforeAll
    static void setup() {
        LoggerConfig.setup(); // Настройка логгера
    }

    @Test
    void testClearInputBuffer() {
        try {
            CardGame game = new CardGame();

            // Получаем приватный метод через рефлексию
            Method clearInputBuffer = CardGame.class.getDeclaredMethod("clearInputBuffer");
            clearInputBuffer.setAccessible(true); // Разрешаем доступ

            // Вызываем метод
            clearInputBuffer.invoke(game);

            logger.info("Приватный метод clearInputBuffer() успешно вызван через рефлексию");
        } catch (Exception e) {
            logger.severe("Ошибка при вызове приватного метода: " + e.getMessage());
            fail("Не удалось вызвать приватный метод clearInputBuffer()");
        }
    }
}