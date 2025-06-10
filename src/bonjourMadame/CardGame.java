package bonjourMadame;

import java.io.IOException;

public class CardGame {
    private final Deck deck = new Deck();
    private Card lastQueen = null;
    private int player1Score = 0;
    private int player2Score = 0;
    private final int falseSlapPenalty = 1;

    public void start() {
        System.out.println("🎴 **Bonjour Madame!**");
        System.out.println("Правила:");
        System.out.println("1. Хлопайте (нажмите 1 или 9 — с Enter), когда две дамы одной масти подряд");
        System.out.println("2. Первый нажавший получает очко");
        System.out.println("3. Ложный хлопок = штраф " + falseSlapPenalty + " очко");
        System.out.println("------------------------------------------");

        while (true) {
            Card card = deck.drawCard();
            if (card == null) {
                endGame();
                break;
            }

            boolean isQueenPair = (lastQueen != null &&
                    card.getRank().equals("Дама") &&
                    lastQueen.getSuit().equals(card.getSuit()));

            System.out.println("\nВыпала карта: " + card);
            System.out.println("Очки: Игрок 1 - " + player1Score + " | Игрок 2 - " + player2Score);

            if (isQueenPair) {
                System.out.println("🛑 ХЛОПАТЬ! Две дамы " + card.getSuit() + " подряд!");
            }

            int player = getSlapInput(1500);

            if (player > 0) {
                if (isQueenPair) {
                    System.out.println("✅ Игрок " + player + " победил в этом раунде!");
                    if (player == 1) player1Score++;
                    else player2Score++;
                } else {
                    System.out.println("❌ Ложный хлопок от Игрока " + player + "!");
                    if (player == 1) player1Score -= falseSlapPenalty;
                    else player2Score -= falseSlapPenalty;
                }
            } else if (isQueenPair) {
                System.out.println("❌ Никто не успел!");
            }

            lastQueen = card.getRank().equals("Дама") ? card : null;
        }
    }

    private int getSlapInput(int timeoutMillis) {
        long endTime = System.currentTimeMillis() + timeoutMillis;
        try {
            while (System.currentTimeMillis() < endTime) {
                if (System.in.available() > 0) { // если в буфере ввода есть данные
                    int input = System.in.read();
                    clearInputBuffer();
                    if (input == '1') return 1;
                    if (input == '9') return 2;
                }
                Thread.sleep(30);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return 0;
    }

    private void clearInputBuffer() {
        try {
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endGame() {
        System.out.println("\nКарты закончились! Игра окончена.");
        System.out.println("Финальный счёт:");
        System.out.println("Игрок 1: " + player1Score);
        System.out.println("Игрок 2: " + player2Score);

        if (player1Score > player2Score) {
            System.out.println("🏆 Победил Игрок 1!");
        } else if (player2Score > player1Score) {
            System.out.println("🏆 Победил Игрок 2!");
        } else {
            System.out.println("🤝 Ничья!");
        }
    }
}
