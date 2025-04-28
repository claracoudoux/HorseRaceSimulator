import java.util.*;
import javax.swing.*;

public class Race {
    private int raceLength;
    private String trackShape;
    private String weatherCondition;
    private JTextArea outputArea;
    private List<Horse> horses;
    private Map<Horse, HorseStatistics> statistics;
    private BetManager betManager;

    public Race(int raceLength, String trackShape, String weatherCondition, JTextArea outputArea) {
        this.raceLength = raceLength;
        this.trackShape = trackShape;
        this.weatherCondition = weatherCondition;
        this.outputArea = outputArea;
        this.horses = new ArrayList<>();
        this.statistics = new HashMap<>();
        this.betManager = new BetManager();
    }

    public void addHorse(Horse horse) {
        horses.add(horse);
        statistics.put(horse, new HorseStatistics());
    }

    public void startRace() {
        Map<Horse, Integer> positions = new HashMap<>();
        for (Horse horse : horses) {
            positions.put(horse, 0);
        }

        boolean raceFinished = false;
        Horse winner = null;
        Random rand = new Random();

        while (!raceFinished) {
            StringBuilder sb = new StringBuilder();
            sb.append("Track: ").append(trackShape).append(", Weather: ").append(weatherCondition).append("\n");

            for (Horse horse : horses) {
                int move = rand.nextInt(3) + 1;

                // Track Shape Impact
                if (trackShape.equals("Figure-Eight")) {
                    move -= 1; // tighter turns
                } else if (trackShape.equals("Zigzag")) {
                    if (rand.nextBoolean()) move -= 1;
                }

                // Weather Impact
                if (weatherCondition.equals("Muddy")) {
                    move -= 1; // slower
                } else if (weatherCondition.equals("Icy")) {
                    if (rand.nextInt(5) == 0) {
                        horse.fall();
                    }
                }

                // Ensure move minimum
                if (move < 1) move = 1;
                if (horse.hasFallen()) move = 0;

                int currentPos = positions.get(horse);
                currentPos += move;
                positions.put(horse, currentPos);

                // Visualize
                for (int i = 0; i < currentPos; i++) {
                    sb.append(" ");
                }
                sb.append(horse.getSymbol()).append("\n");

                if (currentPos >= raceLength && winner == null) {
                    winner = horse;
                    raceFinished = true;
                }
            }
            outputArea.setText(sb.toString());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        outputArea.append("\nWinner: " + winner.getName() + " (" + winner.getSymbol() + ")!\n");

        // Update statistics
        for (Horse horse : horses) {
            boolean won = (horse == winner);
            HorseStatistics stat = statistics.get(horse);
            stat.recordRace(raceLength, won);
        }

        // Resolve bets
        betManager.resolveBets(winner);
    }

    public List<Horse> getHorses() {
        return horses;
    }

    public HorseStatistics getStatistics(Horse horse) {
        return statistics.get(horse);
    }

    public int getRaceLength() {
        return raceLength;
    }

    public BetManager getBetManager() {
        return betManager;
    }
}
