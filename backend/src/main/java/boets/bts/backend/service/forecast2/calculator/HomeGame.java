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
            //TODO add warning/fault message + stop calculation ?
            logger.warn("Team {} has not played 3 home games, can not calculate correct home points", forecastDetail.getTeam().getName());
        }
        //get algorithm
        Algorithm algorithm = forecast.getAlgorithm();
        int teams = forecast.getLeague().getTeams().size();
        //calculate score
        int homescore = 0;
        for (Result result : homeResults) {
            Team opponent = result.getAwayTeam();
            int rankingOpponent = super.getOpponentRanking(forecast, opponent, result.getRoundNumber());
            if (isWinGame(forecastDetail.getTeam(), result)) {
                int homeWinPoints = algorithm.getHomePoints().getWin();
                int opponentStandingPoints = teams - rankingOpponent;
                homescore = homescore + homeWinPoints;
                homescore = homescore + opponentStandingPoints;
            } else if (isLoseGame(forecastDetail.getTeam(), result)) {
                int homeLosePoints = algorithm.getHomePoints().getLose();
                int totalScore = homeLosePoints - rankingOpponent;
                homescore = homescore + totalScore;
            } else {
                // draw
                int homeDrawPoints = algorithm.getHomePoints().getDraw();
                int opponentStandingPoints = teams - rankingOpponent;
                homescore = homescore + homeDrawPoints;
                homescore = homescore + opponentStandingPoints;
            }
        }
        forecastDetail.setHomeScore(homescore);

    }
}
