package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.service.forecast.TeamPerformanceQualifier;
import boets.bts.backend.web.team.TeamDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OpponentPerformanceCalculator implements  ScoreCalculator {

    private final Map<TeamPerformanceQualifier, Integer> teamPerformanceScoreMap;

    public OpponentPerformanceCalculator() {
        this.teamPerformanceScoreMap = new HashMap<>();
        initTeamPerformanceMap(teamPerformanceScoreMap);
    }

    @Override
    public void calculateScore(ForecastDetail forecastDetail, Map<String, TeamPerformanceQualifier> teamPerformanceQualifierMap) {
        int existingScore = forecastDetail.getScore();
        TeamDto opponent = forecastDetail.getNextOpponent();
        TeamPerformanceQualifier teamPerformanceQualifier = teamPerformanceQualifierMap.get(opponent.getId());
        Integer scoreOtherTeam = teamPerformanceScoreMap.get(teamPerformanceQualifier);
        int calculatedScore = Math.round(scoreOtherTeam/2);
        forecastDetail.setScore(existingScore-calculatedScore);
    }
}
