package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.forecast.ForecastRepository;
import boets.bts.backend.repository.forecast.ForecastSpecs;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.forecast2.validator.ForecastValidator;
import boets.bts.backend.web.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
@Transactional
public class ForecastCalculatorManager2 {
    private static final Logger logger = LoggerFactory.getLogger(ForecastCalculatorManager2.class);

    private final ResultRepository resultRepository;
    private final ForecastRepository forecastRepository;
    private List<ScoreCalculator> scoreCalculators;
    private final ForecastValidator validator;


    public ForecastCalculatorManager2(ResultRepository resultRepository, List<ScoreCalculator> scoreCalculators, ForecastRepository forecastRepository,
                                      ForecastValidator validator) {
        this.resultRepository = resultRepository;
        this.forecastRepository = forecastRepository;
        this.scoreCalculators = scoreCalculators;
        this.validator = validator;
    }

    public List<Forecast> calculateForecasts(League league, int roundNumber, List<Algorithm> algorithms)  {


        return null;
    }

    /**
     * Asynchronously check if a Forecast for this league, round and algorithm is ready calculated.  If not, creates a new basic Forecast.
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
     * Asynchronously validates the league. Uses the validator for.
     *
     * @param dataSetFuture - CompletableFuture of a forecast ready to be validated
     * @return A Completable futures of the ForecastData ready to calculate Forecasts.
     */
    private CompletableFuture<Optional<Object>> validateLeague(CompletableFuture<Optional<Forecast>> dataSetFuture) {
        return dataSetFuture
                .thenApplyAsync(dataSetOpt -> dataSetOpt.map(this::validateLeague));
    }

    /**
     * Asynchronously create all forecast details if there is no error message.
     * @param dataSetFuture
     * @return
     */
    private CompletableFuture<Forecast> createForecastDetailsAsync(CompletableFuture<Forecast> dataSetFuture) {
        return dataSetFuture
                .thenApplyAsync((dataSet) ->  {
                    if (dataSet.getForecastResult() == null) {
                        return createForecastDetail(dataSet);
                    } else {
                        return dataSet;
                    }
                });
    }

    private CompletableFuture<Forecast> calculateAsync(Forecast forecast) {
        return CompletableFuture
                // Asynchronously calculate scores
                .supplyAsync(() ->  {
                    Forecast forecast1 = calculateScore(forecast);
                    return forecast1;
                });
    }

    private CompletableFuture<Forecast> calculateFinalScoreAsync(Forecast forecast) {
        return CompletableFuture
                // Asynchronously calculate scores
                .supplyAsync(() ->  {
                    Forecast forecast1 = calculateFinalScore(forecast);
                    return forecast1;
                });
    }

    /**
     *
     * @param league
     * @param roundNumber
     * @param algorithm
     * @return
     */
    protected Optional<Forecast> leagueAlreadyCalculated(League league, int roundNumber, Algorithm algorithm) {
        Optional<Forecast> optionalForecast = forecastRepository.findAll(ForecastSpecs.forAlgorithm(algorithm).and(ForecastSpecs.forLeague(league)).and(ForecastSpecs.forRound(roundNumber)))
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

    private Forecast createForecastDetail(Forecast forecast) {
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
                continue;
            }
        }
        return forecast;
    }


}
