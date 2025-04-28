import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class HorseRacingGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Horse Racing Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());

        // TRACK SETTINGS
        JPanel trackSettingsPanel = new JPanel(new GridLayout(4, 2));
        trackSettingsPanel.setBorder(BorderFactory.createTitledBorder("Track Settings"));

        trackSettingsPanel.add(new JLabel("Number of Lanes:"));
        JSpinner laneSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        trackSettingsPanel.add(laneSpinner);

        trackSettingsPanel.add(new JLabel("Track Length:"));
        JTextField lengthField = new JTextField("50");
        trackSettingsPanel.add(lengthField);

        trackSettingsPanel.add(new JLabel("Track Shape:"));
        String[] shapes = {"Oval", "Figure-Eight", "Zigzag"};
        JComboBox<String> shapeBox = new JComboBox<>(shapes);
        trackSettingsPanel.add(shapeBox);

        trackSettingsPanel.add(new JLabel("Weather:"));
        String[] weatherTypes = {"Sunny", "Muddy", "Icy"};
        JComboBox<String> weatherBox = new JComboBox<>(weatherTypes);
        trackSettingsPanel.add(weatherBox);

        // HORSE SETTINGS
        JPanel horsePanel = new JPanel(new GridLayout(5, 2));
        horsePanel.setBorder(BorderFactory.createTitledBorder("Horse Customisation"));

        horsePanel.add(new JLabel("Horse Name:"));
        JTextField horseNameField = new JTextField("Thunder");
        horsePanel.add(horseNameField);

        horsePanel.add(new JLabel("Breed:"));
        String[] breeds = {"Thoroughbred", "Arabian", "Quarter Horse"};
        JComboBox<String> breedBox = new JComboBox<>(breeds);
        horsePanel.add(breedBox);

        horsePanel.add(new JLabel("Coat Color:"));
        String[] colors = {"Brown", "Black", "Grey", "White"};
        JComboBox<String> colorBox = new JComboBox<>(colors);
        horsePanel.add(colorBox);

        horsePanel.add(new JLabel("Emoji Symbol:"));
        JTextField emojiField = new JTextField("🐎");
        horsePanel.add(emojiField);

        horsePanel.add(new JLabel("Saddle Type:"));
        String[] saddles = {"Standard", "Lightweight", "Heavy Duty"};
        JComboBox<String> saddleBox = new JComboBox<>(saddles);
        horsePanel.add(saddleBox);

        JTextArea raceOutput = new JTextArea(10, 40);
        raceOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(raceOutput);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Race Output"));

        Race[] raceHolder = new Race[1];

        JButton startRaceButton = new JButton("Start Race");
        startRaceButton.addActionListener(e -> {
            int numberOfLanes = (int) laneSpinner.getValue();
            int trackLength = Integer.parseInt(lengthField.getText());
            String trackShape = (String) shapeBox.getSelectedItem();
            String weather = (String) weatherBox.getSelectedItem();

            String horseName = horseNameField.getText();
            String symbolText = emojiField.getText();
            char horseSymbol = symbolText.isEmpty() ? '@' : symbolText.charAt(0);
            String breed = (String) breedBox.getSelectedItem();
            String saddle = (String) saddleBox.getSelectedItem();

            raceOutput.setText("");

            Race race = new Race(trackLength, trackShape, weather, raceOutput);
            raceHolder[0] = race;

            // Create custom horse
            double baseConfidence = 0.8;
            if (breed.equals("Arabian")) baseConfidence += 0.1;
            if (saddle.equals("Lightweight")) baseConfidence += 0.05;
            if (saddle.equals("Heavy Duty")) baseConfidence -= 0.05;

            Horse customHorse = new Horse(horseSymbol, horseName, baseConfidence);
            race.addHorse(customHorse);

            Random rand = new Random();
            for (int i = 1; i < numberOfLanes; i++) {
                char randomSymbol = (char) ('A' + rand.nextInt(26));
                double confidence = 0.6 + 0.2 * rand.nextDouble();
                Horse randomHorse = new Horse(randomSymbol, "Opponent" + i, confidence);
                race.addHorse(randomHorse);
            }

            new Thread(race::startRace).start();
        });

        JButton viewStatsButton = new JButton("View Statistics");
        viewStatsButton.addActionListener(e -> {
            Race race = raceHolder[0];
            if (race == null) {
                JOptionPane.showMessageDialog(frame, "No race yet!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JTextArea statsArea = new JTextArea(20, 40);
            statsArea.setEditable(false);

            for (Horse horse : race.getHorses()) {
                HorseStatistics stats = race.getStatistics(horse);
                statsArea.append(horse.getName() + " (" + horse.getSymbol() + ")\n");
                statsArea.append("Win Rate: " + stats.getWinRate() + "%\n");
                statsArea.append("Avg Speed: " + stats.getAverageSpeed(race.getRaceLength()) + " units/sec\n");
                statsArea.append("Races: " + stats.getRacesRun() + ", Wins: " + stats.getRacesWon() + "\n");
                statsArea.append("Confidence: " + horse.getConfidence() + "\n\n");
            }

            JOptionPane.showMessageDialog(frame, new JScrollPane(statsArea), "Horse Statistics", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton betButton = new JButton("Place Bet");
        betButton.addActionListener(e -> {
            Race race = raceHolder[0];
            if (race == null) {
                JOptionPane.showMessageDialog(frame, "No race available!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] horseNames = race.getHorses().stream()
                    .map(h -> h.getName() + " (" + h.getSymbol() + ") - Odds: " + race.getBetManager().getOdds(h) + "x")
                    .toArray(String[]::new);

            String selectedHorseName = (String) JOptionPane.showInputDialog(
                    frame,
                    "Choose a horse to bet on:",
                    "Place Bet",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    horseNames,
                    horseNames[0]
            );

            if (selectedHorseName == null) return;

            Horse selectedHorse = race.getHorses().stream()
                    .filter(h -> selectedHorseName.startsWith(h.getName()))
                    .findFirst()
                    .orElse(null);

            if (selectedHorse == null) {
                JOptionPane.showMessageDialog(frame, "Horse not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String amountStr = JOptionPane.showInputDialog(frame, "Enter bet amount:");
            try {
                double amount = Double.parseDouble(amountStr);
                race.getBetManager().placeBet(selectedHorse, amount);
                JOptionPane.showMessageDialog(frame, "Bet placed on " + selectedHorse.getName() + " for $" + amount);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.add(trackSettingsPanel);
        topPanel.add(horsePanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(startRaceButton);
        bottomPanel.add(viewStatsButton);
        bottomPanel.add(betButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
