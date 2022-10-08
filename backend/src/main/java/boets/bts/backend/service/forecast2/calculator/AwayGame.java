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
@Order(2)
public class AwayGame extends AbstractCalculator {
    private static final Logger logger = LoggerFactory.getLogger(AwayGame.class);

    public AwayGame(ResultRepository resultRepository, StandingRepository standingRepository) {
        super(resultRepository, standingRepository);
    }

    @Override
    public void calculate(Forecast forecast, ForecastDetail forecastDetail) {
        //get last 3 played away games
        List<Result> awayResults = super.getPlayedGamesForTeam(forecast, forecastDetail.getTeam(), false);
        if (awayResults.size() != 3) {
            logger.warn("Team {} has not played 3 away games, can not calculate correct away points", forecastDetail.getTeam().getName());
            forecastDetail.setErrorMessage(String.format("Team %s has not played 3 away games, can not calculate correct away points", forecastDetail.getTeam().getName()));
            if (awayResults.size() == 2) {
                forecastDetail.setForecastResult(ForecastResult.WARNING);
            } else {
                forecastDetail.setForecastResult(ForecastResult.FATAL);
            }
        }
        //get algorithm
        Algorithm algorithm = forecast.getAlgorithm();
        int teams = forecast.getLeague().getTeams().size();
        //calculate score
        int awayScore = 0;
        for (Result result : awayResults) {
            Team opponent = result.getHomeTeam();
            int rankingOpponent = getOpponentRanking(forecast, opponent, result.getRoundNumber());
            if (isWinGame(forecastDetail.getTeam(), result)) {
                int awayWinPoints = algorithm.getAwayPoints().getWin();
                int opponentStandingPoints = teams - rankingOpponent;
                awayScore = awayScore + awayWinPoints;
                awayScore = awayScore + opponentStandingPoints;
            } else if (isLoseGame(forecastDetail.getTeam(), result)) {
                int awayLosePoints = algorithm.getAwayPoints().getLose();
                int totalScore = awayLosePoints - rankingOpponent;
                awayScore = awayScore + totalScore;
            } else {
                // draw
                int awayDrawPoints = algorithm.getAwayPoints().getDraw();
                int opponentStandingPoints = teams - rankingOpponent;
                awayScore = awayScore + awayDrawPoints;
                awayScore = awayScore + opponentStandingPoints;
            }
        }

        forecastDetail.setAwayScore(awayScore);
    }
}
