package characters;

import GameProcess.GameTime;
import GameProcess.buildings.*;
import java.util.Random;

public class NPC {
    private final Random random = new Random();

    public void visitBarberShop(BarberShop barberShop) {
        Thread thread = new Thread(() -> {
            while (true) {
                GameTime.waitGameMinutes(random.nextInt(61) + 30); // 30–60 игровых минут
                String service = random.nextBoolean() ? "basic" : "fashion";
                barberShop.enter(service, false);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void visitCafe(Cafe cafe) {
        Thread thread = new Thread(() -> {
            while (true) {
                GameTime.waitGameMinutes(random.nextInt(41) + 20); // 20–40 игровых минут
                String service = random.nextBoolean() ? "snack" : "full";
                cafe.enter(service, false);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void visitHotel(Hotel hotel) {
        Thread thread = new Thread(() -> {
            while (true) {
                GameTime.waitGameMinutes(random.nextInt(81) + 40); // 40–80 игровых минут
                String service = random.nextBoolean() ? "short" : "long";
                hotel.enter(service, false);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
