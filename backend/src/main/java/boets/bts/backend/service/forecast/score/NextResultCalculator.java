package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.calculator.ForecastData;
import boets.bts.backend.service.forecast.ForecastDetailDto;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
@Order(3)
public class NextResultCalculator implements ScoreCalculator {

    private int homeGame;

    public NextResultCalculator() {
        homeGame = 10;
    }

    @Override
    public void calculateScore(ForecastDetailDto forecastDetail, ForecastData forecastData, List<ForecastDetailDto> forecastDetails) {
        StringBuilder infoMessage = new StringBuilder();
        ResultDto nextResult = forecastDetail.getNextResult();
        TeamDto teamDto = forecastDetail.getTeam();
        if (nextResult != null && nextResult.getHomeTeam().getTeamId().equals(teamDto.getTeamId())) {
            // next game is home game, add bonus to result
            //forecastDetail.setScore(forecastDetail.getScore() + homeGame);
            BigInteger result = forecastDetail.getResultScore().add(BigInteger.valueOf(10));
            forecastDetail.setResultScore(result);
            infoMessage.append(addMessage(forecastDetail, "+"));
        } else {
            //forecastDetail.setScore(forecastDetail.getScore() - homeGame);
            BigInteger result = forecastDetail.getResultScore().subtract(BigInteger.valueOf(10));
            forecastDetail.setResultScore(result);
            infoMessage.append(addMessage(forecastDetail, "-"));
        }
        StringBuilder builder = new StringBuilder();
        builder.append(forecastDetail.getInfo())
                .append(infoMessage);
        forecastDetail.setInfo(builder.toString());
    }

    private String addMessage(ForecastDetailDto forecastDetail, String bonusMalus) {
        String homeAwayMatch = bonusMalus.equals("+")?"thuis match":"uit match";
        StringBuilder infoMessage = new StringBuilder();
        infoMessage
                .append(bonusMalus)
                .append(" ")
                .append(homeGame)
                .append(" punten vanwege ")
                .append(homeAwayMatch)
                .append("<br>");
        return infoMessage.toString();
    }
}
