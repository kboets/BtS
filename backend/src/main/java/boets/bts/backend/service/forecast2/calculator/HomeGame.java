package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastDetail;
import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(1)
public class HomeGame extends AbstractCalculator {
    private static final Logger logger = LoggerFactory.getLogger(HomeGame.class);
    private final ResultRepository resultRepository;

    public HomeGame(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public void calculate(Forecast forecast, ForecastDetail forecastDetail) {
        // get all results for requested rounds for this team
        List<Integer> rounds = super.calculateRequestedRounds(forecast.getRound());
        List<Result> allResults = resultRepository.findAll(ResultSpecs.forLeague(forecast.getLeague()).and(ResultSpecs.forTeam(forecastDetail.getTeam()).and(ResultSpecs.forRounds(rounds))));
        //take only home results
        List<Result> homeResults = allResults.stream().filter(result -> result.getHomeTeam().getTeamId().equals(forecastDetail.getTeam().getTeamId())).collect(Collectors.toList());
        if (homeResults.size() != 3) {
            logger.warn("Team {} has not played 3 home games, can not calculate correct home points", forecastDetail.getTeam().getName());
        }
        //get algorithm
        Algorithm algorithm = forecast.getAlgorithm();
        //calculate score
        BigInteger score = BigInteger.valueOf(0);
        for (Result result: homeResults) {
            if(isWinGame(forecastDetail.getTeam(), result)) {
                // win game
                int homeWinPoints = algorithm.getHomePoints().getWin();
                //int opponentStandingPoints = forecast.getLeague().getTeams().size() -
            } else if (isLoseGame(forecastDetail.getTeam(), result)) {
                //lose game
                int homeLosePoints = algorithm.getHomePoints().getLose();
            } else {
                // draw
                int homeDrawPoints = algorithm.getHomePoints().getDraw();

            }
        }

    }
}
