package GameProcess;

public class GameTime extends Thread {
    private static int currentMinute = 0;
    private static final Object lock = new Object();

    // 1 игровая минута = 100 мс реального времени
    private static final int MINUTE_DURATION_MS = 100;

    public GameTime() {
        setDaemon(true); // поток завершится при окончании программы
    }

    public static int getCurrentMinute() { // без синхронизации один поток может читать устаревшее значение.
        synchronized (lock) {
            return currentMinute;
        }
    }

    public static void waitGameMinutes(int minutes) {
        int targetMinute;
        synchronized (lock) {
            targetMinute = currentMinute + minutes;
        }

        synchronized (lock) {
            while (currentMinute < targetMinute) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(MINUTE_DURATION_MS);
            } catch (InterruptedException e) {
                return;
            }

            synchronized (lock) {
                currentMinute++;
                lock.notifyAll();
            }
        }
    }
}
