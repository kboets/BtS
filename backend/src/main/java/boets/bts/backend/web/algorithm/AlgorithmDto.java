package boets.bts.backend.web.algorithm;

import javax.persistence.Column;

public class AlgorithmDto {

    private Long algorithm_id;
    private String type;
    private String name;
    private int homeWin;
    private int homeDraw;
    private int homeLose;
    private int awayWin;
    private int awayDraw;
    private int awayLose;
    private Integer homeBonus;
    private Integer awayMalus;
    private boolean current;
    private Integer threshold;


    public Long getAlgorithm_id() {
        return algorithm_id;
    }

    public void setAlgorithm_id(Long algorithm_id) {
        this.algorithm_id = algorithm_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHomeWin() {
        return homeWin;
    }

    public void setHomeWin(int homeWin) {
        this.homeWin = homeWin;
    }

    public int getHomeDraw() {
        return homeDraw;
    }

    public void setHomeDraw(int homeDraw) {
        this.homeDraw = homeDraw;
    }

    public int getHomeLose() {
        return homeLose;
    }

    public void setHomeLose(int homeLose) {
        this.homeLose = homeLose;
    }

    public int getAwayWin() {
        return awayWin;
    }

    public void setAwayWin(int awayWin) {
        this.awayWin = awayWin;
    }

    public int getAwayDraw() {
        return awayDraw;
    }

    public void setAwayDraw(int awayDraw) {
        this.awayDraw = awayDraw;
    }

    public int getAwayLose() {
        return awayLose;
    }

    public void setAwayLose(int awayLose) {
        this.awayLose = awayLose;
    }

    public Integer getHomeBonus() {
        return homeBonus;
    }

    public void setHomeBonus(Integer homeBonus) {
        this.homeBonus = homeBonus;
    }

    public Integer getAwayMalus() {
        return awayMalus;
    }

    public void setAwayMalus(Integer awayMalus) {
        this.awayMalus = awayMalus;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
