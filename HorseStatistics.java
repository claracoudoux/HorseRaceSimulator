public class HorseStatistics {
    private int racesRun;
    private int racesWon;
    private int totalDistance;

    public void recordRace(int distance, boolean won) {
        racesRun++;
        if (won) {
            racesWon++;
        }
        totalDistance += distance;
    }

    public double getAverageSpeed(int raceLength) {
        if (racesRun == 0) return 0;
        return (double) totalDistance / racesRun;
    }

    public double getWinRate() {
        if (racesRun == 0) return 0;
        return (double) racesWon / racesRun * 100;
    }

    public int getRacesRun() {
        return racesRun;
    }

    public int getRacesWon() {
        return racesWon;
    }
}
