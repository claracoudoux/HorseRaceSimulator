import java.util.HashMap;
import java.util.Map;

public class BetManager {
    private Map<Horse, Double> bets = new HashMap<>();

    public void placeBet(Horse horse, double amount) {
        bets.put(horse, amount);
    }

    public void resolveBets(Horse winner) {
        for (Map.Entry<Horse, Double> entry : bets.entrySet()) {
            Horse horse = entry.getKey();
            double amount = entry.getValue();
            double odds = getOdds(horse);
            if (horse.equals(winner)) {
                System.out.println("You won! You earned: $" + (amount * odds));
            } else {
                System.out.println("You lost your bet of: $" + amount);
            }
        }
        bets.clear();
    }

    public double getOdds(Horse horse) {
        // Lower confidence = higher odds, higher confidence = lower odds
        double confidence = horse.getConfidence();
        double odds = 2.0 + (1.0 - confidence) * 5.0;
        return Math.round(odds * 10) / 10.0;
    }
}
