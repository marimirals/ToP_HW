package GameProcess;

import characters.Hero;

import Save.GameState;
import characters.BlackKnight;
import characters.Enemy;
import characters.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class Field {

    private int heightOfField; // n
    private int widthOfField; // m  из GameProcess
    private Character[][] field;
    private String mapName;

    public enum ObjectType {
        EMPTY('.'), ROAD('o'), OBSTACLE('^'), GOLD('*'),
        PLAYER_AREA('$'), ENEMY_AREA('#'),
        PLAYER_CASTLE('И'), ENEMY_CASTLE('К'),
        BLACK_KNIGHT('!');

        private final char symbol;
        ObjectType(char symbol) { this.symbol = symbol; }
        public char getSymbol() { return symbol; }
    }

    public Field() {
    }
    public Field(int heightOfField, int widthOfField, String mapName) {
        this.heightOfField = heightOfField;
        this.widthOfField = widthOfField;
        this.mapName = mapName;
        this.field = new Character[heightOfField][widthOfField];
        clearField();
    }

    public String getMapName() { return mapName; }

    public void setMapName(String mapName) { this.mapName = mapName; }

    public int getHeightOfField() {
        return heightOfField;
    }

    public int getWidthOfField() {
        return widthOfField;
    }

    public Character[][] getField() {
        Character[][] copy = new Character[heightOfField][widthOfField];
        for (int i = 0; i < heightOfField; i++) {
            System.arraycopy(field[i], 0, copy[i], 0, widthOfField);
        }
        return copy;
    }


    public void setItem(int x, int y, Character item) {
        if (x >= 0 && x < heightOfField && y >= 0 && y < widthOfField) {
            field[x][y] = item;
        }
    }

    public Character getItem(int x, int y) {
        if (x >= 0 && x < heightOfField && y >= 0 && y < widthOfField) {
            return field[x][y];
        } else {
            return null;
        }
    }

    public void clearField() {
        for (int i = 0; i < heightOfField; i++) {
            for (int j = 0; j < widthOfField; j++) {
                field[i][j] = ObjectType.EMPTY.getSymbol();
            }
        }
    }

    public void displayField(Player player, Enemy enemy, BlackKnight bk) {
        // Создаем копию поля, чтобы не менять оригинал
        char[][] fieldCopy = new char[heightOfField][widthOfField];

        for (int i = 0; i < heightOfField; i++) {
            for (int j = 0; j < widthOfField; j++) {
                fieldCopy[i][j] = (field[i][j] != null) ? field[i][j] : ' ';
            }
        }

        // Размещаем героев игрока и проверяем, стоят ли они на золоте
        for (Hero hero : player.getPlayersHeroes()) {
            int x = hero.getX();
            int y = hero.getY();
            if (isWithinBounds(x, y)) {
                if (field[x][y] == '*') {
                    int collectedGold = collectGold(x, y);
                    player.setPlayersGold(player.getPlayersGold() + collectedGold);
                    System.out.println("Игрок собрал " + collectedGold + " золота!");
                }
                fieldCopy[x][y] = hero.getSymbol();
            }
        }

        // Размещаем героев врага и проверяем, стоят ли они на золоте
        for (Hero enemyHero : enemy.getHeroes()) {
            int x = enemyHero.getX();
            int y = enemyHero.getY();
            if (isWithinBounds(x, y)) {
                if (field[x][y] == '*') {
                    int collectedGold = collectGold(x, y);
                    enemy.addGold(collectedGold);
                    System.out.println("Враг собрал " + collectedGold + " золота!");
                }
                fieldCopy[x][y] = enemyHero.getSymbol();
            }

        }


        // Если Черный Рыцарь появился И ЕЩЁ НЕ ПОБЕЖДЁН, размещаем его на поле
        if (bk.blackKnightHasAppeared && !bk.isDead()) {
            int x = bk.getKnightX();
            int y = bk.getKnightY();
            if (bk.blackKnightHasAppeared && !bk.isDead() && isWithinBounds(y, x)) {
                fieldCopy[y][x] = '!';
            }

        }

        // Выводим обновленное поле
        // вывод верхних цифорок
        System.out.print("\n      ");
        for (int temp = 0; temp < widthOfField; temp++) {
            System.out.print(temp + "  ");
        }
        System.out.println("\n");
        for (int i = 0; i < heightOfField; i++) {
            System.out.print(i + "     "); // вывод левых цифорок
            for (int j = 0; j < widthOfField; j++) {
                char symbol = fieldCopy[i][j];

                // Отображаем замки
                if (i == 0 && j == 0) {
                    System.out.print("И  ");
                    continue;
                } else if (i == heightOfField - 1 && j == widthOfField - 1) {
                    System.out.print("К  ");
                    continue;
                } else if (isWithinBounds(bk.getKnightY(), bk.getKnightX())
                    && i == bk.getKnightY() && j == bk.getKnightX() && !bk.isDead()) {
                System.out.print("!  ");
                continue;
            }



            // Определяем цвет символа
                switch (symbol) {
                    case 'o':
                        System.out.print("\u001B[38;5;208m" + symbol + "  " + "\u001B[0m");
                        break;
                    case '#':
                        System.out.print("\u001B[31m" + symbol + "  " + "\u001B[0m");
                        break;
                    case '$':
                        System.out.print("\u001B[32m" + symbol + "  " + "\u001B[0m");
                        break;
                    case '^':
                        System.out.print(symbol + "  ");
                        break;
                    case '.':
                        System.out.print("\u001B[38;5;130m" + symbol + "  " + "\u001B[0m");
                        break;
                    case '*':
                        System.out.print("\u001B[33m" + symbol + "  " + "\u001B[0m");
                        break;
                    case '!':
                        System.out.print(symbol + "  "); // Черный Рыцарь
                        break;
                    default:
                        System.out.print(symbol + "  "); // Подсветка героев
                        break;
                }
            }
            System.out.println();
        }
    }

    public int collectGold(int x, int y) {
        int goldAmount = 0;
        if (field[x][y] == '*') {
            goldAmount = (int) (Math.random() * 901) + 100; // от 100 до 1000
            field[x][y] = '.';
        }
        return goldAmount;
    }

    public void setDefaultField() {
        for (int i = 0; i < heightOfField; i++) {
            for (int j = 0; j < widthOfField; j++) {
                field[i][j] = '.';
            }
        }

        // препятствия
        int placedObstacles = 0;
        int totalObstacles = (int) (widthOfField * 1.5); // Количество препятствий = ширина поля * 1.5
        while (placedObstacles < totalObstacles) {
            // Случайная строка (i) и столбец (j)
            int i = (int) (Math.random() * heightOfField); // строка
            int j = (int) (Math.random() * widthOfField); // столбец

            // Проверяем, что на клетке нет символа '0' (дорога) и нет других препятствий '^'
            if (field[i][j] != '0' && field[i][j] != '^') {
                // Размещаем символ '^' на клетке
                field[i][j] = '^';
                placedObstacles++; // Увеличиваем счётчик размещённых препятствий
            }
        }

        // голда
        int placedGold = 0;
        int totalGold = (int) (widthOfField * 0.5);
        while (placedGold < totalGold) {
            int i = (int) (Math.random() * heightOfField);
            int j = (int) (Math.random() * widthOfField);

            if (field[i][j] != '0' && field[i][j] != '*') {
                field[i][j] = '*';
                placedGold++;
            }
        }

        // область игрока
        int centerX = 0;
        int centerY = 0;
        int radiusX = heightOfField / 3;
        int radiusY = widthOfField / 3;

        for (int i = 0; i < heightOfField; i++) {
            for (int j = 0; j < widthOfField; j++) {
                // Уравнение для круга/овала: ((x - h)^2 / rX^2) + ((y - k)^2 / rY^2) <= 1
                if (((i - centerX) * (i - centerX)) / (radiusX * radiusX) + ((j - centerY) * (j - centerY)) / (radiusY * radiusY) <= 1) {
                    if (field[i][j] != 'o') {
                        field[i][j] = '$';
                    }
                }
            }
        }
        // область бота
        centerX = heightOfField - 1;
        centerY = widthOfField - 1;
        radiusX = heightOfField / 3;
        radiusY = widthOfField / 3;

        for (int i = 0; i < heightOfField; i++) {
            for (int j = 0; j < widthOfField; j++) {
                // Уравнение для круга/овала: ((x - h)^2 / rX^2) + ((y - k)^2 / rY^2) <= 1
                if (((i - centerX) * (i - centerX)) / (radiusX * radiusX) + ((j - centerY) * (j - centerY)) / (radiusY * radiusY) <= 1) {
                    if (field[i][j] != 'o') {
                        field[i][j] = '#';
                    }
                }
            }
        }

        int currentRow = 0;
        int currentCol = 0;
        field[currentRow][currentCol] = '.';

        while (currentRow != heightOfField - 1 || currentCol != widthOfField - 1) {
            int direction = (int) (Math.random() * 3);
            switch (direction) {
                case 0: // Вниз
                    if (currentRow < heightOfField - 1) currentRow++;
                    break;
                case 1: // Вправо
                    if (currentCol < widthOfField - 1) currentCol++;
                    break;
                case 2: // Диагональ вниз-вправо
                    if (currentRow < heightOfField - 1 && currentCol < widthOfField - 1) {
                        currentRow++;
                        currentCol++;
                    }
                    break;
            }

            field[currentRow][currentCol] = 'o';
        }

        setItem(0, 0, 'И');
        setItem(heightOfField - 1, widthOfField - 1, 'К');
    }

    // для сохранения и редактора карт
    public void setObject(int x, int y, ObjectType type) {
        if (x >= 0 && x < heightOfField && y >= 0 && y < widthOfField) {
            field[x][y] = type.getSymbol();
        }
    }

    public void setField(Character[][] newField) {
        if (newField.length != heightOfField || newField[0].length != widthOfField) {
            throw new IllegalArgumentException("Несовпадение размеров поля");
        }
        for (int i = 0; i < heightOfField; i++) {
            System.arraycopy(newField[i], 0, this.field[i], 0, widthOfField);
        }
    }

    public void saveToJson(String filename, Field currentField) throws IOException {
        GameState gameState = new GameState();
        gameState.setCurrentField(currentField);

        try (Writer writer = new FileWriter(filename)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(gameState.getCurrentField(), writer);
        }
    }

    public static Field loadFromJson(String filename) throws IOException {
        try (Reader reader = new FileReader(filename)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Field.class);
        }
    }

    public void displayField() {
        System.out.print("\n      ");
        for (int temp = 0; temp < widthOfField; temp++) {
            System.out.print(temp + "  ");
        }
        System.out.println("\n");

        for (int i = 0; i < heightOfField; i++) {
            System.out.print(i + "     ");
            for (int j = 0; j < widthOfField; j++) {
                char symbol = field[i][j] != null ? field[i][j] : '.';
                switch (symbol) {
                    case 'o': System.out.print("\u001B[38;5;208m" + symbol + "  " + "\u001B[0m"); break;
                    case '#': System.out.print("\u001B[31m" + symbol + "  " + "\u001B[0m"); break;
                    case '$': System.out.print("\u001B[32m" + symbol + "  " + "\u001B[0m"); break;
                    case '^': System.out.print(symbol + "  "); break;
                    case '.': System.out.print("\u001B[38;5;130m" + symbol + "  " + "\u001B[0m"); break;
                    case '*': System.out.print("\u001B[33m" + symbol + "  " + "\u001B[0m"); break;
                    case 'И': System.out.print("\u001B[34m" + symbol + "  " + "\u001B[0m"); break;
                    case 'К': System.out.print("\u001B[31m" + symbol + "  " + "\u001B[0m"); break;
                    default: System.out.print(symbol + "  ");
                }
            }
            System.out.println();
        }
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < heightOfField && y >= 0 && y < widthOfField;
    }

    public void copyFrom(Field other) {
        this.heightOfField = other.heightOfField;
        this.widthOfField = other.widthOfField;
        this.mapName = other.mapName;
        this.field = new Character[heightOfField][widthOfField];
        for (int i = 0; i < heightOfField; i++) {
            System.arraycopy(other.field[i], 0, this.field[i], 0, widthOfField);
        }
    }
}
