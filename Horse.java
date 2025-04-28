public class Horse {
    private final String name;
    private char symbol;
    private int distanceTravelled;
    private boolean hasFallen;
    private double confidence;

    public Horse(char horseSymbol, String horseName, double horseConfidence) {
        this.symbol = horseSymbol;
        this.name = horseName;
        this.confidence = Math.max(0, Math.min(1, horseConfidence));
        this.distanceTravelled = 0;
        this.hasFallen = false;
    }

    public void fall() {
        hasFallen = true;
    }

    public double getConfidence() {
        return confidence;
    }

    public int getDistanceTravelled() {
        return distanceTravelled;
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }

    public void goBackToStart() {
        distanceTravelled = 0;
        hasFallen = false;
    }

    public boolean hasFallen() {
        return hasFallen;
    }

    public void moveForward() {
        if (!hasFallen) {
            distanceTravelled++;
        }
    }

    public void setConfidence(double newConfidence) {
        confidence = Math.max(0, Math.min(1, newConfidence));
    }

    public void setSymbol(char newSymbol) {
        this.symbol = newSymbol;
    }
}
