package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.forecast.ForecastRepository;
import boets.bts.backend.repository.forecast.ForecastSpecs;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.forecast.ForecastDto;
import boets.bts.backend.service.forecast2.validator.ForecastValidator;
import boets.bts.backend.web.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static boets.bts.backend.service.forecast2.calculator.StreamOfFuturesCollector.toFuture;

@Component
//@Transactional
public class ForecastCalculatorManager2 {
    private static final Logger logger = LoggerFactory.getLogger(ForecastCalculatorManager2.class);

    private final ResultRepository resultRepository;
    private final ForecastRepository forecastRepository;
    private final List<ScoreCalculator> scoreCalculators;
    private final ForecastValidator validator;


    public ForecastCalculatorManager2(ResultRepository resultRepository, List<ScoreCalculator> scoreCalculators, ForecastRepository forecastRepository,
                                      ForecastValidator validator) {
        this.resultRepository = resultRepository;
        this.forecastRepository = forecastRepository;
        this.scoreCalculators = scoreCalculators;
        this.validator = validator;
    }

    public List<Forecast> calculateForecasts(League league, List<Integer> roundNumbers, Algorithm algorithm) throws Exception {
        CompletableFuture<Stream<Forecast>> forecastCompletableFutureStream = roundNumbers.stream()
                // async verifies if league is already correct calculated.
                .map((roundNumber) -> this.isLeagueAlreadyCalculatedAsync(league, roundNumber, algorithm))
                // async validates the league
                .map(this::validateLeagueAsync)
                // async creates the details
                .map(this::createForecastDetailsAsync)
                // async calculates the home/away points
                .map(this::calculateScoreAsync)
                // async calculates the final points
                .map(this::calculateFinalScoreAsync)
                .collect(toFuture());
        return getForecastsFromStream(forecastCompletableFutureStream);
    }

    /**
     * Collects all Forecasts from stream of CompletableFuture.
     *
     * @param forecastStreamFuture - Stream of CompletableFuture.
     * @return List of Forecast
     * @throws Exception
     */
    private List<Forecast> getForecastsFromStream(CompletableFuture<Stream<Forecast>> forecastStreamFuture) throws Exception {
        try {
            Stream<Forecast> forecastDataStream =
                    //Wait until all the data have been collected and calculated
                    forecastStreamFuture.join();
            return forecastDataStream
                    .collect(Collectors.toList());
        } catch (CompletionException clex) {
            logger.error("Exception thrown while calculating forecasts ", clex);
            throw new Exception(clex.getCause());
        }
    }

    /**
     * Asynchronously check if a Forecast for this league, round and algorithm is already calculated.  If not, creates a new basic Forecast.
     * If yes, verifies if it is valid. In that case no further calculation must be done.
     *
     * @param league - the league
     * @param roundNumber - round to be checked
     * @param algorithm - algorithm to be checked
     *
     * @return A completable future to null if not ready, else a completable future of Forecast
     */
    private CompletableFuture<Optional<Forecast>> isLeagueAlreadyCalculatedAsync(League league, int roundNumber, Algorithm algorithm) {
        return CompletableFuture
                .supplyAsync(() -> this.leagueAlreadyCalculated(league, roundNumber, algorithm));
    }

    /**
     * Asynchronously validates the league. Uses the ForecastValidator to perform the validations.
     *
     * @param dataSetFuture - CompletableFuture of a forecast ready to be validated
     * @return A Completable futures of the forecasts.
     */
    private CompletableFuture<Object> validateLeagueAsync(CompletableFuture<Optional<Forecast>> dataSetFuture) {
        return dataSetFuture
                .thenApplyAsync(dataSetOpt -> dataSetOpt.map(this::validateLeague));
    }

    /**
     * Asynchronously creates the forecast details in case no validation error is set.
     * @param dataSetFuture CompletableFuture of a forecast ready to create each details.
     * @return A Completable futures of the forecasts with details.
     */
    private CompletableFuture<Forecast> createForecastDetailsAsync(CompletableFuture<Object> dataSetFuture) {
        return dataSetFuture
                .thenApplyAsync((dataSet) ->  {
                    Forecast forecast = (Forecast) dataSet;
                    if (forecast.getForecastResult() == null) {
                        return createForecastDetail(forecast);
                    } else {
                        return forecast;
                    }
                });
    }

    /**
     * Asynchronously all score for the last 3 home and away games.
     * @param dataSetFuture CompletableFuture of a Forecast ready to calculate home and away points 
     * @return CompletableFuture of a Forecast ready to calculate the final score
     */
    private CompletableFuture<Forecast> calculateScoreAsync(CompletableFuture<Forecast> dataSetFuture) {
        return dataSetFuture                
                .thenApplyAsync(this::calculateScore);
    }

    /**
     * Asynchronously calculates the final score.
     * @param dataSetFuture CompletableFuture of a Forecast ready to calculate the final score
     * @return CompletableFuture of a Forecast ready to get persisted.
     */
    private CompletableFuture<Forecast> calculateFinalScoreAsync(CompletableFuture<Forecast> dataSetFuture) {
        return dataSetFuture                
                .thenApplyAsync(this::calculateFinalScore);
    }

    
    protected Optional<Forecast> leagueAlreadyCalculated(League league, int roundNumber, Algorithm algorithm) {
        Optional<Forecast> optionalForecast = forecastRepository.findAll(Specification.where(ForecastSpecs.forAlgorithm(algorithm)).and(ForecastSpecs.forLeague(league)).and(ForecastSpecs.forRound(7)))
                .stream()
                .findFirst();
        if (optionalForecast.isPresent()) {
            Forecast forecast = optionalForecast.get();
            if(ForecastResult.OK.equals(forecast.getForecastResult())) {
                return Optional.empty();
            } else {
                return Optional.of(forecast);
            }
        } else {
            return Optional.of(new Forecast(league, roundNumber, algorithm));
        }
    }

    protected Forecast validateLeague(Forecast forecast) {
        forecast.setForecastResult(null);
        forecast.setDate(LocalDateTime.now());
        forecast.setSeason(WebUtils.getCurrentSeason());
        validator.validate(forecast);
        return forecast;
    }

    protected Forecast createForecastDetail(Forecast forecast) {
        League league = forecast.getLeague();
        Set<Team> teams = league.getTeams();
        for (Team team: teams) {
            ForecastDetail forecastDetail = new ForecastDetail();
            forecastDetail.setTeam(team);
            Optional<Result> nextResult = resultRepository.findAll(ResultSpecs.forLeague(forecast.getLeague())
                            .and(ResultSpecs.forRound(forecast.getRound()).and(ResultSpecs.forTeam(team))))
                    .stream().findFirst();
            if (nextResult.isPresent()) {
                Result nextGame = nextResult.get();
                forecastDetail.setNextGame(nextGame);
                forecastDetail.setOpponent(nextGame.getAwayTeam().getTeamId().equals(team.getTeamId())?nextGame.getHomeTeam():nextGame.getAwayTeam());
            } else {
                logger.warn("Could not find next result for team {} in round {} of league {}", team.getName(), forecast.getRound(), league.getName());
                forecastDetail.setErrorMessage(String.format("Could not find next result for team %s in round %s of league %s", forecastDetail.getTeam().getName(), forecast.getRound(), forecast.getLeague().getName()));
                forecastDetail.setForecastResult(ForecastResult.FATAL);
            }
            forecast.addForecastDetail(forecastDetail);
        }
        return forecast;
    }

    private Forecast calculateScore(Forecast forecast) {
        for(ForecastDetail forecastDetail: forecast.getForecastDetails()) {
            for(ScoreCalculator scoreCalculator : scoreCalculators) {
                if (forecastDetail.getForecastResult().equals(ForecastResult.FATAL)) {
                    break;
                }
                scoreCalculator.calculate(forecast, forecastDetail);
            }
        }
        return forecast;
    }

    private Forecast calculateFinalScore(Forecast forecast) {
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
            }
        }
        return forecast;
    }


}
