package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.standing.StandingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(2)
public class AwayGame extends AbstractCalculator {
    private static final Logger logger = LoggerFactory.getLogger(AwayGame.class);

    public AwayGame(ResultRepository resultRepository, StandingRepository standingRepository) {
        super(resultRepository, standingRepository);
    }

    @Override
    public void calculate(Forecast forecast, ForecastDetail forecastDetail) {
        StringBuilder messageBuilder = new StringBuilder(forecastDetail.getMessage());
        //get last 3 played away games
        List<Result> awayResults = super.getPlayedGamesForTeam(forecast, forecastDetail.getTeam(), false);
        if (awayResults.size() < 3) {
            logger.warn("Team {} has not played 3 away games for forecast of round {}, can not calculate correct away points", forecastDetail.getTeam().getName(), forecast.getRound());
            forecastDetail.setErrorMessage(String.format("Team %s has not played 3 away games, can not calculate correct away points", forecastDetail.getTeam().getName()));
            handleNotEnoughGames(awayResults, forecastDetail);
        }
        // sort the list on roundNumber, most recent first
        List<Result> sortedAwayResults = awayResults.stream().sorted(Comparator.comparingInt(Result::getRoundNumber).reversed()).collect(Collectors.toList());
        //get algorithm
        Algorithm algorithm = forecast.getAlgorithm();
        int teams = forecast.getLeague().getTeams().size();
        // set init message
        messageBuilder.append(this.createInitMessage(algorithm));
        //calculate score
        int awayScore = 0;
        int index = 1;
        for (Result result : sortedAwayResults) {
            Team opponent = result.getHomeTeam();
            int rankingOpponent = getOpponentRanking(forecast, opponent, result.getRoundNumber());
            CalculatorMessage calculatorMessage = new CalculatorMessage();
            calculatorMessage.setAlgorithm(algorithm);
            calculatorMessage.setOpponentStanding(rankingOpponent);
            calculatorMessage.setTotalTeams(teams);
            calculatorMessage.setIndex(index);
            if (isWinGame(forecastDetail.getTeam(), result)) {
                messageBuilder.append(this.appendResultMessage(WIN,result, false, index));
                int awayWinPoints = algorithm.getAwayPoints().getWin();
                int opponentStandingPoints = teams - rankingOpponent;
                int currentWinPoints = awayWinPoints + opponentStandingPoints;
                calculatorMessage.setFinalScore(currentWinPoints);
                calculatorMessage.setInitScore(awayWinPoints);
                awayScore = awayScore + currentWinPoints;
                messageBuilder.append(this.appendScoreMessageWinDraw(calculatorMessage));
            } else if (isLoseGame(forecastDetail.getTeam(), result)) {
                messageBuilder.append(this.appendResultMessage(LOST,result, false, index));
                int awayLosePoints = algorithm.getAwayPoints().getLose();
                int totalScore = awayLosePoints - rankingOpponent;
                awayScore = awayScore + totalScore;
                calculatorMessage.setTotalScore(totalScore);
                calculatorMessage.setHome(false);
                messageBuilder.append(this.appendScoreLostMessage(calculatorMessage));
            } else {
                // draw
                messageBuilder.append(this.appendResultMessage(DRAW,result, false, index));
                int awayDrawPoints = algorithm.getAwayPoints().getDraw();
                int opponentStandingPoints = teams - rankingOpponent;
                int currentDrawPoints = awayDrawPoints + opponentStandingPoints;
                awayScore = awayScore + currentDrawPoints;
                calculatorMessage.setFinalScore(currentDrawPoints);
                calculatorMessage.setInitScore(awayDrawPoints);
                messageBuilder.append(this.appendScoreMessageWinDraw(calculatorMessage));
            }
            index++;
        }
        messageBuilder.append("<br><br>")
                .append("<b>")
                .append("Eind score uit wedstrijden : ")
                .append(awayScore)
                .append("</b>")
                .append("<br>");

        forecastDetail.setAwayScore(awayScore);
        forecastDetail.setMessage(messageBuilder.toString());
    }

    private String createInitMessage(Algorithm algorithm) {
        return  "<h3>Berekening uit wedstrijden</h3>" +
                "Winst : " +
                algorithm.getAwayPoints().getWin() +
                " punten + (aantal teams - rank tegenstrever)" +
                "<br>" +
                "verlies : " +
                algorithm.getAwayPoints().getLose() +
                " punten - (rank tegenstrever)" +
                "<br>" +
                "gelijk spel : " +
                algorithm.getAwayPoints().getDraw() +
                " punten  + (aantal teams - rank tegenstrever)" +
                "<br>";
    }
}
