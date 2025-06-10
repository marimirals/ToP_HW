package GameProcess;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapEditor {
    private List<Field> maps;
    private Scanner scanner;
    private static final String MAPS_DIR = "maps/";

    public MapEditor() {
        this.maps = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        new File(MAPS_DIR).mkdirs();
    }

    public void start(GameProcess gameProcess) {
        System.out.println("Редактор карт:");
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: createNewMap(); break;
                case 2: editMap(gameProcess); break;
                case 3: deleteMap(); break;
                case 4: saveMaps(); break;
                case 5: loadMaps(); break;
                case 6: return;
                default: System.out.println("Неверный выбор!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n1. Создать новую карту");
        System.out.println("2. Редактировать существующую карту");
        System.out.println("3. Удалить карту");
        System.out.println("4. Сохранить все карты");
        System.out.println("5. Загрузить карты");
        System.out.println("6. Выход");
        System.out.print("Выберите действие 1: ");
    }

    public Field selectMapToPlay() {
        loadMaps();
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт! Создайте новую карту сначала.");
            return null;
        }

        System.out.println("\nВыберите карту для игры:");

        for (int i = 0; i < maps.size(); i++) {
            Field map = maps.get(i);
            System.out.printf(i+1 + "." + map.getMapName() + "\n");
        }

        System.out.print("Выберите карту (1-" + maps.size() + "): ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice > 0 && choice <= maps.size()) {
                Field selectedMap = maps.get(choice-1);
                System.out.println("\nВыбрана карта: " + selectedMap.getMapName());
                System.out.println("Размер: " + selectedMap.getHeightOfField() + "x" + selectedMap.getWidthOfField());
                return selectedMap;
            } else {
                System.out.println("Неверный выбор! Попробуйте снова.");
                return selectMapToPlay(); // рекурсивный вызов при ошибке
            }
        } catch (Exception e) {
            scanner.nextLine(); // очистка буфера
            System.out.println("Ошибка ввода! Введите число.");
            return selectMapToPlay(); // рекурсивный вызов при ошибке
        }
    }

    private void createNewMap() {
        System.out.print("Введите название карты: ");
        String name = scanner.nextLine();

        if (mapExists(name)) {
            System.out.println("Карта с таким именем уже существует!");
            return;
        }

        System.out.print("Введите высоту карты: ");
        int height = scanner.nextInt();
        System.out.print("Введите ширину карты: ");
        int width = scanner.nextInt();
        scanner.nextLine();

        Field newMap = new Field(height, width, name);
        maps.add(newMap);
        System.out.println("Карта '" + name + "' создана!");
        editMap(newMap);
        saveMaps();
    }

    private boolean mapExists(String name) {
        File file = new File(MAPS_DIR + name + ".json");
        return file.exists();
    }

    private void editMap(GameProcess gameProcess) {
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт для редактирования!");
            return;
        }

        System.out.println("\nСписок карт:");
        for (int i = 0; i < maps.size(); i++) {
            System.out.println((i+1) + ". " + maps.get(i).getMapName());
        }

        System.out.print("Выберите карту для редактирования: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice > 0 && choice <= maps.size()) {
            Field selectedMap = maps.get(choice-1);
            editMap(selectedMap);

            // Если редактируется текущая карта игры - обновляем ее
            if (gameProcess != null && selectedMap.getMapName().equals(gameProcess.getCurrentMapName())) {
                gameProcess.setCurrentField(selectedMap);
                System.out.println("Текущая карта игры обновлена!");
            }
        } else {
            System.out.println("Неверный выбор!");
        }
    }
    private void editMap(Field map) {
        System.out.println("\nРедактирование карты: " + map.getMapName());
        while (true) {
            map.displayField();

            System.out.println("\n1. Добавить объект");
            System.out.println("2. Удалить объект");
            System.out.println("3. Заполнить случайно (как в оригинале)");
            System.out.println("4. Очистить карту");
            System.out.println("5. Сохранить и выйти");
            System.out.print("Выберите действие 2: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addObject(map); break;
                case 2: removeObject(map); break;
                case 3: map.setDefaultField(); break;
                case 4: map.clearField(); break;
                case 5:
                    return;
                default: System.out.println("Неверный выбор!");
            }
            saveMaps();
        }
    }

    private void addObject(Field map) {
        System.out.println("\nТипы объектов:");
        for (Field.ObjectType type : Field.ObjectType.values()) {
            System.out.println(type.ordinal() + ". " + type.name() + " (" + type.getSymbol() + ")");
        }

        System.out.print("Выберите тип объекта: ");
        int typeIdx = scanner.nextInt();
        System.out.print("Введите координату X: ");
        int x = scanner.nextInt();
        System.out.print("Введите координату Y: ");
        int y = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (typeIdx >= 0 && typeIdx < Field.ObjectType.values().length) {
            map.setObject(x, y, Field.ObjectType.values()[typeIdx]);
            System.out.println("Объект добавлен!");
        } else {
            System.out.println("Неверный тип объекта!");
        }
    }

    private void removeObject(Field map) {
        System.out.print("Введите координату X для удаления: ");
        int x = scanner.nextInt();
        System.out.print("Введите координату Y для удаления: ");
        int y = scanner.nextInt();
        scanner.nextLine(); // consume newline

        map.setObject(x, y, Field.ObjectType.EMPTY);
        System.out.println("Объект удален!");
    }

    private void saveMaps() {
        try {
            for (Field map : maps) {
                String filename = MAPS_DIR + map.getMapName() + ".json";
                map.saveToJson(filename, map);
                System.out.println("Карта '" + map.getMapName() + "' сохранена в " + filename);
            }
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    private void loadMaps() {
        maps.clear();
        File dir = new File(MAPS_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try {
                    Field map = Field.loadFromJson(file.getPath());
                    maps.add(map);
                    System.out.println("Загружена карта: " + map.getMapName());
                } catch (IOException e) {
                    System.out.println("Ошибка загрузки " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        System.out.println("Всего загружено карт: " + maps.size());
    }

    private void deleteMap() {
        if (maps.isEmpty()) {
            System.out.println("Нет карт для удаления!");
            return;
        }

        System.out.println("Список карт:");
        for (int i = 0; i < maps.size(); i++) {
            System.out.println((i+1) + ". " + maps.get(i).getMapName());
        }

        System.out.print("Выберите карту для удаления: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= maps.size()) {
            String mapName = maps.get(choice-1).getMapName();
            File file = new File(MAPS_DIR + mapName + ".json");
            if (file.delete()) {
                maps.remove(choice-1);
                System.out.println("Карта '" + mapName + "' удалена!");
            } else {
                System.out.println("Не удалось удалить файл карты!");
            }
        } else {
            System.out.println("Неверный выбор!");
        }
    }
}