package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.service.forecast.TeamPerformanceQualifier;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HomeGameCalculator implements ScoreCalculator {

    @Override
    public void calculateScore(ForecastDetail forecastDetail, Map<String, TeamPerformanceQualifier> teamPerformanceQualifierMap) {
        ResultDto nextResult = forecastDetail.getNextResult();
        TeamDto team = forecastDetail.getTeam();
        int currentScore = forecastDetail.getScore();
        if(nextResult.getHomeTeam().getId().equals(team.getId())) {
            forecastDetail.setScore(currentScore+10);
        }
    }
}
