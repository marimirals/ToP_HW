package GameProcess.buildings;

import characters.*;
import GameProcess.GameTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Cafe {
    private static final int MAX_VISITORS = 12;
    private final Semaphore semaphore = new Semaphore(MAX_VISITORS, true);
    private final Player player;
    private final List<NPC> npcs = new ArrayList<>();

    public Cafe(Player player) {
        this.player = player;
        for (int i = 1; i <= 10; i++) {
            NPC npc = new NPC();
            npcs.add(npc);
            npc.visitCafe(this);
        }
    }

    public void showLoadStatus() {
        System.out.println("Текущая загрузка кафе: " +
                (MAX_VISITORS - semaphore.availablePermits()) + "/" + MAX_VISITORS);
    }

    public void enter(String serviceType, boolean isPlayer) {
        try {
            if (isPlayer) {
                if (semaphore.availablePermits() == 0) {
                    synchronized (System.in) {
                        System.out.println("Все официанты заняты! Хотите подождать? (1 - да, 2 - нет)");
                        int choice = new Scanner(System.in).nextInt();
                        if (choice != 1) return;
                    }
                }
            }

            semaphore.acquire();

            try {
                int duration = serviceType.equals("snack") ? 30 : 60; // в игровых минутах
                int startTime = GameTime.getCurrentMinute();
                int endTime = startTime + duration;

                while (GameTime.getCurrentMinute() < endTime) {
                    GameTime.waitGameMinutes(1);
                }

                if (isPlayer) {
                    synchronized (player) {
                        int bonus = serviceType.equals("snack") ? 2 : 3;
                        for (Hero hero : player.getPlayersHeroes()) {
                            hero.setDist(hero.getDist() + bonus);
                        }
                    }
                    System.out.println("Услуга '" + serviceType + "' завершена. +" +
                            (serviceType.equals("snack") ? "2" : "3") + " к перемещению.");
                }
            } finally {
                semaphore.release();
                if (isPlayer) {
                    System.out.println("Официант свободен. Свободно: " + semaphore.availablePermits());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void handleCafeActions() {
        synchronized (System.in) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Выберите услугу: 'snack' или 'full'");
            String serviceType = scanner.nextLine();
            if (serviceType.equals("snack") || serviceType.equals("full")) {
                enter(serviceType, true);
            } else {
                System.out.println("Неверный тип услуги.");
            }
        }
    }
}