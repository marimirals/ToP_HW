package GameProcess;

import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
    public static void setup() {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler); // шобы в консоли логов не было
        }
        try {
            FileHandler fileHandler = new FileHandler("game.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Не удалось настроить логирование: " + e.getMessage());
        }
    }
}