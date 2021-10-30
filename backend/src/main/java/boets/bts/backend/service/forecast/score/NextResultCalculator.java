package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.calculator.ForecastData;
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
        StringBuffer infoMessage = new StringBuffer(forecastDetail.getInfo());
        ResultDto nextResult = forecastDetail.getNextResult();
        TeamDto teamDto = forecastDetail.getTeam();
        int currentScore = forecastDetail.getResultScore();
        if (nextResult != null && nextResult.getHomeTeam().getTeamId().equals(teamDto.getTeamId())) {
            // next game is home game, add bonus to result
            forecastDetail.setScore(forecastDetail.getScore() + homeGame);
            infoMessage
                    .append("+ 10 punten vanwege thuismatch")
                    .append(", totaal = ")
                    .append(forecastDetail.getScore())
                    .append("<br>");
        } else {
            forecastDetail.setScore(forecastDetail.getScore() - homeGame);
            infoMessage
                    .append("- 10 punten vanwege thuismatch")
                    .append(", totaal = ")
                    .append(forecastDetail.getScore())
                    .append("<br>");
        }
        forecastDetail.setInfo(infoMessage.toString());
    }
}
