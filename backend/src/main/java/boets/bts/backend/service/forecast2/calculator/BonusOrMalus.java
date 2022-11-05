package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastDetail;
import boets.bts.backend.domain.Result;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class BonusOrMalus implements ScoreCalculator {

    @Override
    public void calculate(Forecast forecast, ForecastDetail forecastDetail) {
        StringBuilder messageBuilder = new StringBuilder(forecastDetail.getMessage());
        // verify the next game is home or away
        Result nextGame = forecastDetail.getNextGame();
        boolean isHome = nextGame.getHomeTeam().getTeamId().equals(forecastDetail.getTeam().getTeamId());
        if (isHome) {
            messageBuilder.append("<br>Next game is home game, add extra points:  <b>").append(forecast.getAlgorithm().getHomeBonus()).append("</b>");
            forecastDetail.setBonusMalusScore(forecast.getAlgorithm().getHomeBonus());
        } else {
            messageBuilder.append("<br>Next game is away game, extract points :  <b>").append(forecast.getAlgorithm().getAwayMalus()).append("</b>");
            forecastDetail.setBonusMalusScore(forecast.getAlgorithm().getAwayMalus());
        }
        forecastDetail.setSubTotal(forecastDetail.getHomeScore() + forecastDetail.getAwayScore() + forecastDetail.getBonusMalusScore());
        forecastDetail.setMessage(messageBuilder.toString());
    }
}
