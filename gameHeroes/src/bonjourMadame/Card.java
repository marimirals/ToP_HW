package bonjourMadame;

public class Card {
    private final String suit; // Масть
    private final String rank; // Значение

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() { return suit; }
    public String getRank() { return rank; }

    @Override
    public String toString() {
        return rank + " " + suit;
    }
}
