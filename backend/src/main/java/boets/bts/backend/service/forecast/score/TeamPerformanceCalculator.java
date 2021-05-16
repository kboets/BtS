package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.service.forecast.TeamPerformanceQualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TeamPerformanceCalculator implements ScoreCalculator {

    private final Map<TeamPerformanceQualifier, Integer> teamPerformanceScoreMap;

    public TeamPerformanceCalculator() {
        this.teamPerformanceScoreMap = new HashMap<>();
        initTeamPerformanceMap(teamPerformanceScoreMap);
    }

    @Override
    public void calculateScore(ForecastDetail forecastDetail, Map<String, TeamPerformanceQualifier> teamPerformanceQualifierMap) {
        int existingScore = forecastDetail.getScore();
        int calculatedScore = teamPerformanceScoreMap.get(forecastDetail.getPerformanceQualifier());
        forecastDetail.setScore(existingScore+calculatedScore);
    }

}
