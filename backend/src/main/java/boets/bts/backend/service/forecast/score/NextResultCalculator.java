package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.ForecastData;
import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class NextResultCalculator implements ScoreCalculator {

    private int homeGame;

    public NextResultCalculator() {
        homeGame = 10;
    }

    @Override
    public void calculateScore(ForecastDetail forecastDetail, ForecastData forecastData, List<ForecastDetail> forecastDetails) {
        ResultDto nextResult = forecastDetail.getNextResult();
        TeamDto teamDto = forecastDetail.getTeam();
        if (nextResult.getHomeTeam().getTeamId().equals(teamDto.getTeamId())) {
            // next game is home game, add bonus to result
            forecastDetail.setScore(forecastDetail.getScore() + homeGame);
        } else {
            forecastDetail.setScore(forecastDetail.getScore() - 10);
        }
    }
}
