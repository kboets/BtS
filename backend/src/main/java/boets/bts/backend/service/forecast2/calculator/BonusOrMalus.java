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
        // verify the next game is home or away
        Result nextGame = forecastDetail.getNextGame();
        boolean isHome = nextGame.getHomeTeam().getTeamId().equals(forecastDetail.getTeam().getTeamId());
        if (isHome) {
            forecastDetail.setBonusMalusScore(forecast.getAlgorithm().getHomeBonus());
        } else {
            forecastDetail.setBonusMalusScore(forecast.getAlgorithm().getAwayMalus());
        }
        // set as temp final score: home score + away score +/- bonus/malus
        forecastDetail.setFinalScore(forecastDetail.getHomeScore() + forecastDetail.getAwayScore() + forecastDetail.getBonusMalusScore());
    }
}
