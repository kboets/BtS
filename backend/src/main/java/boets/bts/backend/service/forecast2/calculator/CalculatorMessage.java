package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.Algorithm;

public class CalculatorMessage {
    private int totalTeams;
    private int opponentStanding;
    private int finalScore;
    private int initScore;
    private Algorithm algorithm;
    private boolean isHome;
    private int index;
    private int booster;

    private boolean hasBooster;


    public int getTotalTeams() {
        return totalTeams;
    }

    public void setTotalTeams(int totalTeams) {
        this.totalTeams = totalTeams;
    }

    public int getOpponentStanding() {
        return opponentStanding;
    }

    public void setOpponentStanding(int opponentStanding) {
        this.opponentStanding = opponentStanding;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public int getInitScore() {
        return initScore;
    }

    public void setInitScore(int initScore) {
        this.initScore = initScore;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getBooster() {
        return booster;
    }

    public void setBooster(int booster) {
        this.booster = booster;
    }

    public boolean isHasBooster() {
        return hasBooster;
    }

    public void setHasBooster(boolean hasBooster) {
        this.hasBooster = hasBooster;
    }
}
