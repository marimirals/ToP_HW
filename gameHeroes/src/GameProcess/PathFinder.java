package GameProcess;

import java.util.*;

public class PathFinder {

    public static List<int[]> findPath(Field field, int startX, int startY, int targetX, int targetY) {
        int height = field.getHeightOfField();
        int width = field.getWidthOfField();

        // Массив для хранения расстояний
        int[][] distances = new int[height][width];
        for (int[] row : distances) {
            Arrays.fill(row, -1); // расстояние как -1 (не посещено)
        }

        // Очередь для обхода в ширину
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        distances[startX][startY] = 0;

        // Направления для перемещения (вверх, вниз, влево, вправо)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // Обход в ширину
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            // Если достигли цели, восстанавливаем путь
            if (x == targetX && y == targetY) {
                return reconstructPath(distances, startX, startY, targetX, targetY);
            }

            // Перебираем соседние клетки
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                // Проверяем, что клетка в пределах поля и не занята препятствием
                if (newX >= 0 && newX < height && newY >= 0 && newY < width &&
                        field.getItem(newX, newY) != '^' && distances[newX][newY] == -1) {
                    distances[newX][newY] = distances[x][y] + 1;
                    queue.add(new int[]{newX, newY});
                }
            }
        }

        return null; // Путь не найден
    }

    private static List<int[]> reconstructPath(int[][] distances, int startX, int startY, int targetX, int targetY) {
        List<int[]> path = new ArrayList<>();
        int x = targetX;
        int y = targetY;

        // Восстанавливаем путь от цели к началу
        while (x != startX || y != startY) {
            path.add(new int[]{x, y});
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX >= 0 && newX < distances.length && newY >= 0 && newY < distances[0].length &&
                        distances[newX][newY] == distances[x][y] - 1) {
                    x = newX;
                    y = newY;
                    break;
                }
            }
        }

        Collections.reverse(path); // Переворачиваем путь, чтобы он шел от начала к цели
        return path;
    }
}