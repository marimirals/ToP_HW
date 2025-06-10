package GameProcess;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

import GameProcess.buildings.Buildings;
import GameProcess.buildings.Castle;
import Save.GameState;
import characters.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GameProcess {

    private int gameScore = 0; // Общее количество очков
    private long startTime; // Время начала игры
    private int unitsDefeated = 0; // Количество побежденных юнитов

    private int heightOfField;
    private int widthOfField;

    private Castle myCastle;
    private transient Random random = new Random();
    private boolean isGameFinished = false;

    private Player player;
    private Enemy enemy;
    private BlackKnight bk;
    private MapEditor mapEditor = new MapEditor();
    private Field currentField;
    private String currentMapName;
    private static final String SAVES_DIR = "saves/";

    public GameProcess(Player player, Enemy enemy, int heightOfField, int widthOfField, BlackKnight bk) {
        this.player = player;
        this.enemy = enemy;
        this.myCastle = new Castle(player);
        this.heightOfField = heightOfField;
        this.widthOfField = widthOfField;
        this.bk = bk;
        startTime = System.currentTimeMillis(); //  время начала
        gameScore = 0;
        unitsDefeated = 0;

    }

    // Добавляем очки за победу
    public void addScoreForVictory() {
        long gameDuration = (System.currentTimeMillis() - startTime) / 1000;
        gameScore += 1000 - (int) gameDuration;
        gameScore += unitsDefeated * 50; // Бонус за побежденных юнитов
    }

    // При победе
    public void onGameWin(String mapName) {
        addScoreForVictory();
        ScoreManager.saveScore(player.getName(), gameScore, mapName);
        System.out.println("🏆 Ваш счет: " + gameScore);
    }

    public void startGame(String mapName) {
        GameTime gameTime = new GameTime();
        gameTime.start();

        Logger logger = Logger.getLogger(GameProcess.class.getName());
        logger.info("Начало игры на карте: " + mapName);

        this.currentMapName = mapName;

        try {
            currentField = Field.loadFromJson("maps/" + mapName + ".json");
            System.out.println("Карта '" + mapName + "' загружена!");
            logger.info("Карта '" + mapName + "' загружена!");

            this.heightOfField = currentField.getHeightOfField();
            this.widthOfField = currentField.getWidthOfField();
        } catch (IOException e) {
            logger.warning("Ошибка загрузки карты: " + e.getMessage());
            logger.info("Создание новой карты '" + mapName + "'");
            System.out.println("Создаем новую карту '" + mapName + "'");
            currentField = new Field(heightOfField, widthOfField, mapName);
            currentField.setDefaultField();
        }

        while (!isGameFinished) {
            currentField.displayField(player, enemy, bk);
            Menu.displayOptionsAndStatistics(player, enemy);

            logger.fine("Ход игрока");
            playerTurn(currentField, bk);
            logger.fine("Ход бота");
            botTurn(currentField);
            isGameFinished = checkIfGameNotFinished();

            autoSave();
        }
    }

    public void playerTurn(Field field, BlackKnight bk) {
        boolean playerTurnFinished = false;
        setHeroesStatus();

        Scanner scanner = new Scanner(System.in);
        int command;

        do {
            System.out.print("Ваш ход: ");
            command = scanner.nextInt();

            switch (command) {
                case 1:
                    boolean heroInCastle = false;
                    for (Hero hero : player.getPlayersHeroes()) {
                        if (hero.getX() == 0 && hero.getY() == 0) {
                            myCastle.enterCastle();
                            System.out.println("Вы в замке!");
                            myCastle.choiceInCastle(player, bk, field);
                            heroInCastle = true;
                            break;
                        }
                    }

                    if (!heroInCastle) {
                        System.out.println("Ни одного героя в замке нет... Не получится никого нанять.");
                    }
                    break;

                case 2:
                    if (player.getPlayersNumOfHeroes() == 0) {
                        System.out.println("А героев то нема");
                        break;
                    }
                    System.out.print("Введите имя героя и новые координаты: ");
                    char herosName = scanner.next().charAt(0);
                    int moveToX = scanner.nextInt();
                    int moveToY = scanner.nextInt();

                    player.moveHero(player, enemy, field, heightOfField, widthOfField, herosName, moveToX, moveToY, this);
                    field.displayField(player, enemy, bk);
                    Menu.displayOptionsAndStatistics(player, enemy);
                    if (Hero.isAnyoneOnCell(player.getPlayersHeroes(), bk.getKnightY(), bk.getKnightX())) {
                        bk.banKnightAction(player);
                    }
                    break;

                case 3:
                    Menu.showInfoAboutHeroes(player);
                    break;

                case 4:
                    Menu.showInfoAboutEnemyHeroes(enemy);
                    break;

                case 5:
                    System.out.println("Ход завершён.");
                    playerTurnFinished = true;
                    break;

                case 6:
                    break;

                case 8:
                    System.out.print("Введите название сохранения: ");
                    String saveName = scanner.next();
                    try {
                        saveGame(saveName);
                    } catch (IOException e) {
                        System.out.println("Ошибка сохранения: " + e.getMessage());
                    }
                    break;

                default:
                    System.out.println("Неверная команда. Попробуйте снова.");
                    break;
            }
        } while (!playerTurnFinished);

        double testRandom = random.nextDouble();
        if (!bk.blackKnightHasAppeared &&
                !bk.isDead() &&
                testRandom < bk.getBlackKnightSpawnChance()) {
            bk.spawnBlackKnight(player, player.getPlayersHeroes(), field);
        }

        checkIfInOpponentsCastle(0, 0, heightOfField, widthOfField);
    }

    public void botTurn(Field field) {
        System.out.println("\nХод противника:");
        hireHeroesIfPossible(field);
        buildStructuresIfPossible(field);
        buyUnitsIfPossible(field);
        moveEnemyHeroes(field);
    }

    private void hireHeroesIfPossible(Field field) {
        if (enemy.getEnemyNumOfHeroes() < enemy.getEnemyMAX_HEROES() && enemy.isAnyHeroInCastle(field)) {
            int maxGoldToSpend = enemy.getEnemyGold() / 2;
            Buildings tavern = Buildings.createTavern();
            int costPerHero = tavern.getCostOfUnit() + 50;
            int heroesToHire = Math.min(maxGoldToSpend / costPerHero,
                    enemy.getEnemyMAX_HEROES() - enemy.getEnemyNumOfHeroes());

            for (int i = 0; i < heroesToHire; i++) {
                char newHeroSymbol = (char) ('A' + enemy.getEnemyNumOfHeroes());
                enemy.addHero(newHeroSymbol, heightOfField - 1, widthOfField - 1, field);
            }
        }
    }

    private void buildStructuresIfPossible(Field field) {
        if (enemy.isAnyHeroInCastle(field) &&
                (!enemy.watchtowerBuilt || !enemy.crossbowTowerBuilt || !enemy.armoryBuilt
                        || !enemy.arenaBuilt || !enemy.cathedralBuilt)) {

            int buildingsToBuild = random.nextInt(3) + 1;
            for (int i = 0; i < buildingsToBuild; i++) {
                if (enemy.getEnemyGold() >= 500) {
                    enemy.buildStructure();
                } else {
                    System.out.println("Недостаточно золота для постройки зданий.\n");
                    break;
                }
            }
        }
    }

    private void buyUnitsIfPossible(Field field) {
        if (enemy.getEnemyGold() >= 50 && enemy.isAnyHeroInCastle(field)) {
            int unitsToHire = random.nextInt(5) + 1;
            for (int i = 0; i < unitsToHire; i++) {
                int unitType = random.nextInt(5);
                switch (unitType) {
                    case 0:
                        if (enemy.watchtowerBuilt)
                            enemy.buyUnitsForRandomHeroInCastle(GameUnit.createSpearman(), Buildings.createWatchtower().getCostOfUnit());
                        break;
                    case 1:
                        if (enemy.crossbowTowerBuilt)
                            enemy.buyUnitsForRandomHeroInCastle(GameUnit.createCrossbowman(), Buildings.createCrossbowTower().getCostOfUnit());
                        break;
                    case 2:
                        if (enemy.armoryBuilt)
                            enemy.buyUnitsForRandomHeroInCastle(GameUnit.createSwordsman(), Buildings.createArmory().getCostOfUnit());
                        break;
                    case 3:
                        if (enemy.arenaBuilt)
                            enemy.buyUnitsForRandomHeroInCastle(GameUnit.createCavalryman(), Buildings.createArena().getCostOfUnit());
                        break;
                    case 4:
                        if (enemy.cathedralBuilt)
                            enemy.buyUnitsForRandomHeroInCastle(GameUnit.createPaladin(), Buildings.createCathedral().getCostOfUnit());
                        break;
                }
            }
        }
    }

    private void moveEnemyHeroes(Field field) {
        List<Hero> heroes = enemy.getHeroes();
        for (int i = 0; i < heroes.size(); i++) {
            Hero hero = heroes.get(i);

            boolean wasInCastle = hero.isInCastle();
            boolean nowInCastle = (hero.getX() == heightOfField - 1 && hero.getY() == widthOfField - 1);
            hero.setInCastle(nowInCastle);

            if (wasInCastle && !nowInCastle) continue;

            if (i % 3 == 0) {
                moveHeroToPlayerCastle(hero, field);
            } else {
                moveHeroToGoldOrPatrol(hero, field);
            }
        }
    }

    private void moveHeroToPlayerCastle(Hero hero, Field field) {
        List<int[]> path = PathFinder.findPath(field, hero.getX(), hero.getY(), 0, 0);
        if (path != null && !path.isEmpty()) {
            int[] nextStep = path.get(0);
            hero.move(player, enemy, hero, field, heightOfField, widthOfField, nextStep[0], nextStep[1], this);
        }
    }

    private void moveHeroToGoldOrPatrol(Hero hero, Field field) {
        int[] goldCoords = enemy.findNearestGold(hero.getX(), hero.getY(), field);
        if (goldCoords != null) {
            List<int[]> path = PathFinder.findPath(field, hero.getX(), hero.getY(), goldCoords[0], goldCoords[1]);
            if (path != null && !path.isEmpty()) {
                int[] nextStep = path.get(0);
                hero.move(player, enemy, hero, field, heightOfField, widthOfField, nextStep[0], nextStep[1], this);
                return;
            }
        }
        List<int[]> patrolPath = PathFinder.findPath(field, hero.getX(), hero.getY(), heightOfField - 1, widthOfField - 1);
        if (patrolPath != null && !patrolPath.isEmpty()) {
            int[] nextStep = patrolPath.get(0);
            hero.move(player, enemy, hero, field, heightOfField, widthOfField, nextStep[0], nextStep[1], this);
        }
    }

    public Field getFieldOfGameProcess() {
        return this.currentField;
    }

    public MapEditor getMapEditor() {
        return this.mapEditor;
    }

    public void setHeroesStatus() {
        for (Hero hero : player.getPlayersHeroes()) {
            hero.setInCastle(hero.getX() == 0 && hero.getY() == 0);
            if (!hero.isMoveIsMade()) {
                hero.restoreStamina();
            }
            hero.setMoveIsMade(false);
            hero.setInOpponentsCastle(false);
        }
    }

    public void setCurrentField(Field field) {
        this.currentField = field;
    }

    public void setHeightOfField(int h) {
        this.heightOfField = h;
    }

    public void setWidthOfField(int h) {
        this.widthOfField = h;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public void setBlackKnight(BlackKnight bk) {
        this.bk = bk;
    }

    public boolean checkIfInOpponentsCastle(int playerCastleX, int playerCastleY, int enemyCastleX, int enemyCastleY) {
        for (Hero playersHero : player.getPlayersHeroes()) {
            if (!playersHero.getIsEnemy() && playersHero.getX() == enemyCastleX && playersHero.getY() == enemyCastleY) {
                return true;
            }
        }

        for (Hero enemyHero : enemy.getHeroes()) {
            if (enemyHero.getX() == playerCastleX && enemyHero.getY() == playerCastleY) {
                return true;
            }
        }

        return false;
    }

    public boolean checkIfGameNotFinished() {
        int playerCastleX = 0;
        int playerCastleY = 0;
        int enemyCastleX = heightOfField - 1;
        int enemyCastleY = widthOfField - 1;

        if (player.getPlayersNumOfHeroes() == 0) {
            System.out.println("Вы проиграли! У вас не осталось героев.");
            return true;
        } else if (enemy.getEnemyNumOfHeroes() == 0) {
            onGameWin(currentMapName); // Добавляем очки за победу
            System.out.println("Вы победили! У врага не осталось героев.");
            return true;
        }

        for (Hero enemyHero : enemy.getHeroes()) {
            if ((enemyHero.getX() == playerCastleX && enemyHero.getY() == playerCastleY) ||
                    (enemyHero.getFastCastleCapturing() && enemyHero.isInCastle())) {
                System.out.println("Вы проиграли! Вражеский герой в вашем замке.");
                return true;
            }
        }

        for (Hero playerHero : player.getPlayersHeroes()) {
            if ((playerHero.getX() == enemyCastleX && playerHero.getY() == enemyCastleY) ||
                    playerHero.getFastCastleCapturing() && playerHero.isInCastle()) {
                System.out.println("Вы победили! Ваш герой в замке врага.");
                return true;
            }
        }

        return false;
    }

    public void fight(Hero playerHero, Hero enemyHero, Player player, Enemy enemy) {
        System.out.println("\n=== Начинается сражение ===");
        System.out.println("Герой " + playerHero.getSymbol() + " (игрок) vs Герой " + enemyHero.getSymbol() + " (враг)");

        List<GameUnit> playerArmy = new ArrayList<>(playerHero.getArmy());
        List<GameUnit> enemyArmy = new ArrayList<>(enemyHero.getArmy());

        int round = 1;

        while (!playerArmy.isEmpty() && !enemyArmy.isEmpty()) {
            System.out.println("\nРаунд " + round + ":");

            GameUnit playerUnit = playerArmy.get(0);
            GameUnit enemyUnit = enemyArmy.get(0);

            System.out.println(playerUnit.getType() + " (игрок) атакует " + enemyUnit.getType() + " (враг) с силой " + playerUnit.getAttack());
            enemyUnit.takeDamage(playerUnit.getAttack());
            System.out.println("Здоровье вражеского юнита: " + enemyUnit.getHealth());

            if (enemyUnit.isDead()) {
                System.out.println("Вражеский " + enemyUnit.getType() + " погиб!");
                enemyArmy.remove(0);
            }

            if (!enemyArmy.isEmpty()) {
                enemyUnit = enemyArmy.get(0);
                System.out.println(enemyUnit.getType() + " (враг) атакует " + playerUnit.getType() + " (игрок) с силой " + enemyUnit.getAttack());
                playerUnit.takeDamage(enemyUnit.getAttack());
                System.out.println("Здоровье вашего юнита: " + playerUnit.getHealth());

                if (playerUnit.isDead()) {
                    System.out.println("Ваш " + playerUnit.getType() + " погиб!");
                    playerArmy.remove(0);
                }
            }

            round++;
        }

        if (playerArmy.isEmpty()) {
            System.out.println("\n=== Герой " + playerHero.getSymbol() + " проиграл и погибает! ===");
            player.removeHero(playerHero.getSymbol());  // Удаляем героя, если армия пуста
        } else {
            System.out.println("\n=== Герой " + enemyHero.getSymbol() + " проиграл и погибает! ===");
            enemy.getHeroes().remove(enemyHero);
            enemy.setEnemyNumOfHeroes(enemy.getEnemyNumOfHeroes() - 1);
        }

        if (!playerArmy.isEmpty()) {
            playerHero.restoreStamina();
        } else if (!enemyArmy.isEmpty()) {
            enemyHero.restoreStamina();
        }

        if (playerArmy.isEmpty()) {
            System.out.println("\n=== Герой " + playerHero.getSymbol() + " проиграл и погибает! ===");
            player.removeHero(playerHero.getSymbol());
        } else {
            System.out.println("\n=== Герой " + enemyHero.getSymbol() + " проиграл и погибает! ===");
            enemy.getHeroes().remove(enemyHero);
            enemy.setEnemyNumOfHeroes(enemy.getEnemyNumOfHeroes() - 1);
            unitsDefeated++; // Увеличиваем счетчик побежденных юнитов
        }

    }

    // сохранения
    private void autoSave() {
        try {
            saveGame("autosave_" + System.currentTimeMillis());
            currentField.saveToJson("maps/" + currentMapName + "_autosave.json",  currentField);
        } catch (IOException e) {
            System.out.println("Ошибка автосохранения: " + e.getMessage());
        }
    }

    public void saveGame(String saveName) throws IOException {
        GameState gameState = new GameState();
        gameState.setPlayer(player);
        gameState.setEnemy(enemy);
        gameState.setBlackKnight(bk);
        gameState.setCurrentField(currentField);
        gameState.setCurrentMapName(currentMapName);

        new File(SAVES_DIR).mkdirs();
        String filename = SAVES_DIR + player.getName() + "_" + saveName + ".json";

        try (Writer writer = new FileWriter(filename)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(gameState, writer);
            System.out.println("Игра сохранена как: " + filename);
        }
    }

    public static GameProcess loadGame(String filename) throws IOException {
        try (Reader reader = new FileReader(filename)) {
            Gson gson = new Gson();
            GameState gameState = gson.fromJson(reader, GameState.class);

            Field savedField = gameState.getCurrentField();

            GameProcess game = new GameProcess(
                    gameState.getPlayer(),
                    gameState.getEnemy(),
                    savedField.getHeightOfField(),
                    savedField.getWidthOfField(),
                    gameState.getBlackKnight()
            );

            game.currentField = savedField;
            game.currentMapName = gameState.getCurrentMapName();

            return game;
        }
    }

    public static void loadGameMenu(String playersName) {
        File dir = new File("saves/");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json")&& name.startsWith(playersName + "_"));

        if (files == null || files.length == 0) {
            System.out.println("Нет доступных сохранений!");
            return;
        }

        System.out.println("\nДоступные сохранения для " + playersName + ": ");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i+1) + ". " + files[i].getName());
        }

        System.out.print("Выберите сохранение для загрузки: (-2 - выход)");
        int choice = new Scanner(System.in).nextInt();

        if (choice == -2) {
            return;
        }

        if (choice > 0 && choice <= files.length) {
            try {
                GameProcess game = GameProcess.loadGame(files[choice-1].getPath());
                game.startGame(game.currentMapName);
            } catch (IOException e) {
                System.out.println("Ошибка загрузки: " + e.getMessage());
            }
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    public String getCurrentMapName() {
        return currentMapName;
    }
}
