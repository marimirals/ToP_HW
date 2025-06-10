package characters;

import java.util.List;

public class GameUnit {
    private String type;  // Тип юнита
    private int unitLevel; // Уровень юнита
    private int health;   // Здоровье
    private int damage;   // Урон
    private int movement; // Перемещение
    private int attackRange; // Дальность атаки

    public GameUnit(String type, int unitLevel, int health, int damage, int movement, int attackRange) {
        this.type = type;
        this.unitLevel = unitLevel;
        this.health = health;
        this.damage = damage;
        this.movement = movement;
        this.attackRange = attackRange;
    }

    @Override
    public String toString() {
        return type + " (Уровень: " + unitLevel + ", Здоровье: " + health + ", Урон: " + damage +
                ", Перемещение: " + movement + ", Дальность атаки: " + attackRange + ")";
    }

    public static GameUnit createSpearman() { // копейщик
        return new GameUnit("Копейщик", 1,100, 10, 2, 1);
    }

    public static GameUnit createCrossbowman() { // арбалетчик
        return new GameUnit("Арбалетчик", 1,70, 30, 2, 5);
    }

    public static GameUnit createSwordsman() { // мечник
        return new GameUnit("Мечник", 3, 150, 20, 3, 1);
    }

    public static GameUnit createCavalryman() { // кавалерист
        return new GameUnit("Кавалерист", 4, 120, 40, 5, 2);
    }

    public static GameUnit createPaladin() { // паладин
        return new GameUnit("Паладин", 5, 200, 50, 4, 1);
    }

    public void setMovement(int newMovement) {
        this.movement = newMovement;
    }

    public String getType() { return type; }

    public int getMovement() {
        return movement;
    }

    public static Hero findNearestTarget(List<Hero> heroes, int knightX, int knightY, int heightOfField) {
        Hero nearestTarget = null;
        double minDistance = Double.MAX_VALUE;
        double maxDistance = heightOfField * 0.5; // Ограничение по расстоянию

        // Проходим по всем героям
        for (Hero hero : heroes) {
            double distance = calculateDistance(knightX, knightY, hero.getX(), hero.getY());

            // Если герой находится в радиусе и ближе предыдущего кандидата, обновляем цель
            if (distance <= maxDistance && distance < minDistance) {
                minDistance = distance;
                nearestTarget = hero;
            }
        }

        return nearestTarget; // Вернёт null, если в зоне нет подходящих целей
    }

    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
        }
    }

    public boolean isDead() {
        return this.health == 0;
    }

    public int getAttack() {
        return this.damage;
    }

    public int getHealth() {
        return this.health;
    }
}