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
import java.util.concurrent.CompletableFuture;

@Component
public class ForecastCalculatorManager2 {
    private static final Logger logger = LoggerFactory.getLogger(ForecastCalculatorManager2.class);

    private final ResultRepository resultRepository;
    private List<ScoreCalculator> scoreCalculators;

    public ForecastCalculatorManager2(ResultRepository resultRepository, List<ScoreCalculator> scoreCalculators) {
        this.resultRepository = resultRepository;
        this.scoreCalculators = scoreCalculators;
    }

    public Forecast calculateForecast(Forecast forecast) {
        CompletableFuture<Void> completableFuture = this.manageForecastAsync(forecast);
        completableFuture.join();
        return forecast;
    }

    private CompletableFuture<Void> manageForecastAsync(Forecast forecast) {
        return CompletableFuture
                // Asynchronously create all forecast details
                .runAsync(() -> createForecastDetail(forecast))
                // Asynchronously calculate score
                .thenRunAsync(() -> calculateScore(forecast))
                // Asynchronously calculate final score
                .thenRunAsync(() -> calculateFinalScore(forecast));
    }

    private void createForecastDetail(Forecast forecast) {
        League league = forecast.getLeague();
        Set<Team> teams = league.getTeams();
        for (Team team: teams) {
            ForecastDetail forecastDetail = new ForecastDetail();
            forecastDetail.setTeam(team);
            Optional<Result> nextResult = resultRepository.findAll(ResultSpecs.forLeague(forecast.getLeague()).and(ResultSpecs.forRound(forecast.getRound()).and(ResultSpecs.forTeam(team))))
                    .stream().findFirst();
            if (nextResult.isPresent()) {
                Result nextGame = nextResult.get();
                forecastDetail.setNextGame(nextGame);
                forecastDetail.setOpponent(nextGame.getAwayTeam().getTeamId().equals(team.getTeamId())?nextGame.getHomeTeam():nextGame.getAwayTeam());
                forecast.addForecastDetail(forecastDetail);
            } else {
                logger.warn("Could not find next result for team {} in round {} of league {}", team.getName(), forecast.getRound(), league.getName());
                forecastDetail.setErrorMessage(String.format("Could not find next result for team %s in round %s of league %s", forecastDetail.getTeam().getName(), forecast.getRound(), forecast.getLeague().getName()));
                forecastDetail.setForecastResult(ForecastResult.FATAL);
            }
        }
    }

    private void calculateScore(Forecast forecast) {
        for(ForecastDetail forecastDetail: forecast.getForecastDetails()) {
            for(ScoreCalculator scoreCalculator : scoreCalculators) {
                if (forecastDetail.getForecastResult().equals(ForecastResult.FATAL)) {
                    break;
                }
                scoreCalculator.calculate(forecast, forecastDetail);
            }
        }
    }

    private void calculateFinalScore(Forecast forecast) {
        for(ForecastDetail forecastDetail: forecast.getForecastDetails()) {
            if (forecastDetail.getForecastResult().equals(ForecastResult.FATAL)) {
                continue;
            }
            Team opponent = forecastDetail.getOpponent();
            // get the forecast detail of the opponent
            Optional<ForecastDetail> fdOptionalOpponent = forecast.getForecastDetails()
                    .stream()
                    .filter(forecastDetail1 -> forecastDetail1.getTeam().getTeamId().equals(opponent.getTeamId()))
                    .findFirst();
            if (fdOptionalOpponent.isPresent()) {
                int finalScoreOpponent = fdOptionalOpponent.get().getFinalScore();
                int finalScore = forecastDetail.getFinalScore() - finalScoreOpponent;
                forecastDetail.setFinalScore(finalScore);
            } else {
                forecastDetail.setForecastResult(ForecastResult.FATAL);
                forecastDetail.setErrorMessage(String.format("Could not get forecast detail of opponent %s of team %s of round %s", forecastDetail.getOpponent().getName(), forecastDetail.getTeam().getName(), forecast.getRound()));
                continue;
            }

        }
    }


}
