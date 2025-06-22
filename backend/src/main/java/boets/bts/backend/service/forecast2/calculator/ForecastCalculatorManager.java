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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Transactional
public class ForecastCalculatorManager {
    private static final Logger logger = LoggerFactory.getLogger(ForecastCalculatorManager.class);

    private final ResultRepository resultRepository;
    private final ForecastRepository forecastRepository;
    private final List<ScoreCalculator> scoreCalculators;
    private final ForecastValidator validator;


    public ForecastCalculatorManager(ResultRepository resultRepository, List<ScoreCalculator> scoreCalculators, ForecastRepository forecastRepository,
                                     ForecastValidator validator) {
        this.resultRepository = resultRepository;
        this.forecastRepository = forecastRepository;
        this.scoreCalculators = scoreCalculators;
        this.validator = validator;
    }

    public List<Forecast> calculateForecasts(League league, List<Integer> roundNumbers, Algorithm algorithm)  {
           return roundNumbers.stream()
                .map(roundNumber -> this.leagueAlreadyCalculated(league,roundNumber,algorithm))
                .flatMap(Optional::stream)
                .map(this::validateLeague)
                .flatMap(Optional::stream)
                .map(this::createForecastDetail)
                .map(this::calculateScore)
                .map(this::calculateFinalScore)
                .collect(Collectors.toList());
    }
    
    protected Optional<Forecast> leagueAlreadyCalculated(League league, int roundNumber, Algorithm algorithm) {
        Optional<Forecast> optionalForecast = forecastRepository.findAll(Specification.where(ForecastSpecs.forAlgorithm(algorithm)).and(ForecastSpecs.forLeague(league)).and(ForecastSpecs.forRound(roundNumber)))
                .stream()
                .findFirst();
        if (optionalForecast.isPresent()) {
            Forecast forecast = optionalForecast.get();
            logger.info("Forecast for League {} with round {} and algorithm {} was already calculated ", league.getName(), roundNumber, algorithm.getName());
            if(ForecastResult.OK.equals(forecast.getForecastResult())) {
                return Optional.empty();
            } else {
                logger.info("Forecast for League {} with round {} and algorithm {} was not correct calculated, retrying ", league.getName(), roundNumber, algorithm.getName());
                return Optional.of(forecast);
            }
        } else {
            return Optional.of(new Forecast(league, roundNumber, algorithm));
        }
    }

    protected Optional<Forecast> validateLeague(Forecast forecast) {
        forecast.setForecastResult(null);
        forecast.setDate(LocalDateTime.now());
        forecast.setSeason(WebUtils.getCurrentSeason());
        validator.validate(forecast);
        if (forecast.getForecastResult().equals(ForecastResult.FATAL)) {
            return Optional.empty();
        } else {
            return Optional.of(forecast);
        }

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
            forecastDetail.setForecast(forecast);
        }
        return forecast;
    }

    private Forecast calculateScore(Forecast forecast) {
        logger.info("Start calculating forecasts for League {}, round {} and algorithm {} ", forecast.getLeague().getName(), forecast.getRound(), forecast.getAlgorithm().getName());
        for(ForecastDetail forecastDetail: forecast.getForecastDetails()) {
            for(ScoreCalculator scoreCalculator : scoreCalculators) {
                if (ForecastResult.FATAL.equals(forecastDetail.getForecastResult())) {
                    logger.warn("Fatal validation result, can not calculate forecast for League {}, round {} and algorithm {} ", forecast.getLeague().getName(), forecast.getRound(), forecast.getAlgorithm().getName());
                    break;
                }
                scoreCalculator.calculate(forecast, forecastDetail);
            }
        }
        return forecast;
    }

    private Forecast calculateFinalScore(Forecast forecast) {
        for(ForecastDetail forecastDetail: forecast.getForecastDetails()) {
            if (ForecastResult.FATAL.equals(forecastDetail.getForecastResult())) {
                continue;
            }
            Team opponent = forecastDetail.getOpponent();
            // get the forecast detail of the opponent
            Optional<ForecastDetail> fdOptionalOpponent = forecast.getForecastDetails()
                    .stream()
                    .filter(forecastDetail1 -> forecastDetail1.getTeam().getTeamId().equals(opponent.getTeamId()))
                    .findFirst();
            if (fdOptionalOpponent.isPresent()) {
                ForecastDetail forecastDetailOpponent = fdOptionalOpponent.get();
                int scoreOpponent = forecastDetailOpponent.getHomeScore() + forecastDetailOpponent.getAwayScore();
                forecastDetail.setOpponentScore(scoreOpponent);
                int finalScore = forecastDetail.getSubTotal() - scoreOpponent;
                forecastDetail.setFinalScore(finalScore);
                forecastDetail.setMessage(createFinalScoreMessage(forecastDetail));
            } else {
                forecastDetail.setForecastResult(ForecastResult.FATAL);
                forecastDetail.setErrorMessage(String.format("Could not get forecast detail of opponent %s of team %s of round %s", forecastDetail.getOpponent().getName(), forecastDetail.getTeam().getName(), forecast.getRound()));
            }
        }
        return forecast;
    }



    private String createFinalScoreMessage(ForecastDetail forecastDetail) {
        StringBuilder messageBuilder = new StringBuilder(forecastDetail.getMessage());
        messageBuilder.append("<br><h4>Eind score: </h4>")
                .append("Sub totaal : ")
                .append("<br>")
                .append(forecastDetail.getHomeScore())
                .append(" +  ")
                .append(forecastDetail.getAwayScore())
                .append(" + (")
                .append(forecastDetail.getBonusMalusScore())
                .append(")")
                .append(" = ")
                .append(forecastDetail.getSubTotal())
                .append("<br>")
                .append("eind totaal : ")
                .append(forecastDetail.getSubTotal())
                .append(" - ")
                .append(" score tegenstrever : ")
                .append(forecastDetail.getOpponentScore())
                .append(" = <b>")
                .append(forecastDetail.getFinalScore())
                .append("</b>");
        return messageBuilder.toString();
    }

}
