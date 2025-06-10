package GameProcess.buildings;

import characters.*;
import GameProcess.GameTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class BarberShop {
    private static final int MAX_VISITORS = 2;
    private final Semaphore semaphore = new Semaphore(MAX_VISITORS, true);
    private final Player player;
    private final List<NPC> npcs = new ArrayList<>();

    public BarberShop(Player player) {
        this.player = player;
        for (int i = 1; i <= 10; i++) {
            NPC npc = new NPC();
            npcs.add(npc);
            npc.visitBarberShop(this);
        }
    }

    public void showLoadStatus() {
        System.out.println("Текущая загрузка барбершопа: " +
                (MAX_VISITORS - semaphore.availablePermits()) + "/" + MAX_VISITORS);
    }

    public void enter(String serviceType, boolean isPlayer) {
        try {

            if (isPlayer) {
                if (semaphore.availablePermits() == 0) {
                    synchronized (System.in) {
                        System.out.println("Все мастера заняты! Хотите подождать? (1 - да, 2 - нет)");
                        int choice = new Scanner(System.in).nextInt();
                        if (choice != 1) return;
                    }
                }
            }

            semaphore.acquire(); // если есть возможность работы, то делает работу (если есть свободные разрешения)

            try {
                int duration = serviceType.equals("basic") ? 30 : 60; // в игровых минутах
                int startTime = GameTime.getCurrentMinute();
                int endTime = startTime + duration;

                while (GameTime.getCurrentMinute() < endTime) {
                    GameTime.waitGameMinutes(1);
                }

                if (serviceType.equals("fashion") && isPlayer) {
                    synchronized (player) {
                        player.decreaseCastleCaptureTimeModifier();
                    }
                    System.out.println("Модная стрижка завершена! Захват замка теперь занимает 1 ход.");
                }
            } finally {
                semaphore.release(); // конец работы потока
                if (isPlayer) {
                    System.out.println("Мастер свободен. Свободно: " + semaphore.availablePermits());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void handleBarberShopActions() {
        synchronized (System.in) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Выберите услугу: 'basic' (обычная стрижка) или 'fashion' (ускоряет захват)");
            String serviceType = scanner.nextLine();

            if (serviceType.equals("basic") || serviceType.equals("fashion")) {
                enter(serviceType, true);
            } else {
                System.out.println("Неверный тип услуги.");
            }
        }
    }
}