package boets.bts.backend.web.algorithm;

import boets.bts.backend.domain.AlgorithmPoints;

public class AlgorithmDto {

    private Long algorithm_id;
    private String type;
    private String name;
    private AlgorithmPointsDto homePoints;
    private AlgorithmPointsDto awayPoints;
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

    public AlgorithmPointsDto getHomePoints() {
        return homePoints;
    }

    public void setHomePoints(AlgorithmPointsDto homePoints) {
        this.homePoints = homePoints;
    }

    public AlgorithmPointsDto getAwayPoints() {
        return awayPoints;
    }

    public void setAwayPoints(AlgorithmPointsDto awayPoints) {
        this.awayPoints = awayPoints;
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
