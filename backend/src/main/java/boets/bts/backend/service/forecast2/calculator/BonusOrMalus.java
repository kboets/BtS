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
            messageBuilder.append("<br>Volgende wedstrijd is thuis : <br><b>")
                    .append(forecastDetail.getTeam().getName())
                    .append(" - ")
                    .append(forecastDetail.getNextGame().getAwayTeam().getName())
                    .append("</b> , voeg extra punten toe : ")
                    .append(forecast.getAlgorithm().getHomeBonus());
            forecastDetail.setBonusMalusScore(forecast.getAlgorithm().getHomeBonus());
        } else {
            messageBuilder.append("<br>Volgende wedstrijd is uit : <br><b>")
                    .append(forecastDetail.getNextGame().getHomeTeam().getName())
                    .append(" - ")
                    .append(forecastDetail.getTeam().getName())
                    .append("</b> , verminder met malus punten : ")
                    .append(forecast.getAlgorithm().getAwayMalus());
            forecastDetail.setBonusMalusScore(forecast.getAlgorithm().getAwayMalus());
        }
        forecastDetail.setSubTotal(forecastDetail.getHomeScore() + forecastDetail.getAwayScore() + forecastDetail.getBonusMalusScore());
        forecastDetail.setMessage(messageBuilder.toString());
    }
}
