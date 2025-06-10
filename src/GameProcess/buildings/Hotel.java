package GameProcess.buildings;

import characters.*;
import GameProcess.GameTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Hotel {
    private static final int MAX_VISITORS = 5;
    private final Semaphore semaphore = new Semaphore(MAX_VISITORS, true);
    private final Player player;
    private final List<NPC> npcs = new ArrayList<>();

    public Hotel(Player player) {
        this.player = player;
        for (int i = 1; i <= 10; i++) {
            NPC npc = new NPC();
            npcs.add(npc);
            npc.visitHotel(this);
        }
    }

    public void showLoadStatus() {
        System.out.println("Текущая загрузка отеля: " +
                (MAX_VISITORS - semaphore.availablePermits()) + "/" + MAX_VISITORS);
    }

    public void enter(String serviceType, boolean isPlayer) {
        try {
            if (isPlayer) {
                if (semaphore.availablePermits() == 0) {
                    synchronized (System.in) {
                        System.out.println("Отель переполнен! Хотите подождать? (1 - да, 2 - нет)");
                        int choice = new Scanner(System.in).nextInt();
                        if (choice != 1) return;
                    }
                }
            }

            semaphore.acquire();

            try {
                int duration = serviceType.equals("short") ? 30 : 90; // в игровых минутах
                int startTime = GameTime.getCurrentMinute();
                int endTime = startTime + duration;

                while (GameTime.getCurrentMinute() < endTime) {
                    GameTime.waitGameMinutes(1);
                }

                if (isPlayer) {
                    synchronized (player) {
                        int bonus = serviceType.equals("short") ? 2 : 3;
                        for (Hero hero : player.getPlayersHeroes()) {
                            hero.setHealth(hero.getHealth() + bonus);
                        }
                    }
                    System.out.println("Услуга '" + serviceType + "' завершена. +" +
                            (serviceType.equals("short") ? "2" : "3") + " HP.");
                }
            } finally {
                semaphore.release();
                if (isPlayer) {
                    System.out.println("Номер освобожден. Свободно: " + semaphore.availablePermits());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void handleHotelActions() {
        synchronized (System.in) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Выберите услугу: 'short' (1 день, +2 HP) или 'long' (3 дня, +3 HP)");
            String serviceType = scanner.nextLine();

            if (serviceType.equals("short") || serviceType.equals("long")) {
                enter(serviceType, true);
            } else {
                System.out.println("Неверный тип услуги.");
            }
        }
    }
}