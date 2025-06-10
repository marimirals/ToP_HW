package GameProcess;

import java.io.*;
import java.util.*;

public class ScoreManager {
    private static final String SCORES_FILE = "scores.txt";
    private static final int MAX_RECORDS = 5;

    // Запись рекорда в файл
    public static void saveScore(String playerName, int score, String mapName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_FILE, true))) {
            writer.println(playerName + "," + score + "," + mapName);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения рекорда: " + e.getMessage());
        }
    }

    // Получение топ-5 рекордов
    public static List<String> getTopScores() {
        List<String> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения рекордов: " + e.getMessage());
        }

        // Сортировка по убыванию очков
        scores.sort((a, b) -> {
            int scoreA = Integer.parseInt(a.split(",")[1]);
            int scoreB = Integer.parseInt(b.split(",")[1]);
            return Integer.compare(scoreB, scoreA);
        });

        // только топ-5
        return scores.size() > MAX_RECORDS ? scores.subList(0, MAX_RECORDS) : scores;
    }
}