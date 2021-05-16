package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.service.forecast.TeamPerformanceQualifier;

import java.util.Map;

public interface ScoreCalculator {

    void calculateScore(ForecastDetail forecastDetail, Map<String, TeamPerformanceQualifier> teamPerformanceQualifierMap);

    default void initTeamPerformanceMap(Map<TeamPerformanceQualifier, Integer> teamPerformanceScoreMap) {
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.TOPPER, 70);
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.UITSTEKEND, 60);
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.ZEER_GOED, 50);
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.GOED, 40);
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.BEHOORLIJK, 35);
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.MATIG, 30);
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.ONDERMAATS, 25);
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.SLECHT, 20);
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.ZEER_SLECHT, 10);
        teamPerformanceScoreMap.put(TeamPerformanceQualifier.FLOPPER, 0);
    }
}
