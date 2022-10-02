package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.standing.StandingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class HomeGame extends AbstractCalculator {
    private static final Logger logger = LoggerFactory.getLogger(HomeGame.class);

    public HomeGame(ResultRepository resultRepository, StandingRepository standingRepository) {
        super(resultRepository, standingRepository);
    }

    @Override
    public void calculate(Forecast forecast, ForecastDetail forecastDetail) {
        //get last 3 played home games
        List<Result> homeResults = super.getPlayedGamesForTeam(forecast, forecastDetail.getTeam(), true);
        if (homeResults.size() != 3) {
            logger.warn("Team {} has not played 3 home games, can not calculate correct home points", forecastDetail.getTeam().getName());
            forecastDetail.setErrorMessage(String.format("Team %s has not played 3 home games, can not calculate correct home points", forecastDetail.getTeam().getName()));
            if (homeResults.size() == 2) {
                forecastDetail.setForecastResult(ForecastResult.WARNING);
            } else {
                forecastDetail.setForecastResult(ForecastResult.FATAL);
            }
        }
        //get algorithm
        Algorithm algorithm = forecast.getAlgorithm();
        int teams = forecast.getLeague().getTeams().size();
        //calculate score
        int homeScore = 0;
        for (Result result : homeResults) {
            Team opponent = result.getAwayTeam();
            int rankingOpponent = super.getOpponentRanking(forecast, opponent, result.getRoundNumber());
            if (isWinGame(forecastDetail.getTeam(), result)) {
                int homeWinPoints = algorithm.getHomePoints().getWin();
                int opponentStandingPoints = teams - rankingOpponent;
                homeScore = homeScore + homeWinPoints;
                homeScore = homeScore + opponentStandingPoints;
            } else if (isLoseGame(forecastDetail.getTeam(), result)) {
                int homeLosePoints = algorithm.getHomePoints().getLose();
                int totalScore = homeLosePoints - rankingOpponent;
                homeScore = homeScore + totalScore;
            } else {
                // draw
                int homeDrawPoints = algorithm.getHomePoints().getDraw();
                int opponentStandingPoints = teams - rankingOpponent;
                homeScore = homeScore + homeDrawPoints;
                homeScore = homeScore + opponentStandingPoints;
            }
        }
        forecastDetail.setHomeScore(homeScore);

    }
}
