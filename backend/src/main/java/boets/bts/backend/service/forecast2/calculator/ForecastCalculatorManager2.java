package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.forecast.calculator.ForecastCalculatorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ForecastCalculatorManager2 {
    private static final Logger logger = LoggerFactory.getLogger(ForecastCalculatorManager2.class);

    private final ResultRepository resultRepository;

    public ForecastCalculatorManager2(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public void calculateForecast(Forecast forecast) {
        // for each team, create a forecastdetail
        // calculate score
        // return forecast
    }

    private void createForecastDetail(Forecast forecast) {
        League league = forecast.getLeague();
        Set<Team> teams = league.getTeams();
        for (Team team: teams) {
            ForecastDetail forecastDetail = new ForecastDetail();
            forecastDetail.setTeam(team);
            Optional<Result> nextResult = resultRepository.findAll(ResultSpecs.forRound(forecast.getRound()).and(ResultSpecs.forLeague(forecast.getLeague()).and(ResultSpecs.forRound(forecast.getRound()))))
                    .stream().findFirst();
            if (nextResult.isPresent()) {
                Result nextGame = nextResult.get();
                forecastDetail.setNextGame(nextGame);
                forecastDetail.setOpponent(nextGame.getAwayTeam().getTeamId().equals(team.getTeamId())?nextGame.getHomeTeam():nextGame.getAwayTeam());
                forecast.addForecastDetail(forecastDetail);
            } else {
                logger.warn("Could not find next result for team {} in round {} of league {}", team.getName(), forecast.getRound(), league.getName());
            }
        }
    }


}
