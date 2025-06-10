package bonjourMadame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        String[] suits = {"♥", "♦", "♣", "♠"};
        String[] ranks = {"Валет", "Дама"};

        for (int i = 0; i < 3; i++) {
            for (String suit : suits) {
                for (String rank : ranks) {
                    if (rank.equals("Дама")) {
                        cards.add(new Card(suit, rank));
                        cards.add(new Card(suit, rank));
                    }
                    cards.add(new Card(suit, rank));
                }
            }
        }
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }
}