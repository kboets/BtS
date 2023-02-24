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
@Order(1)
public class HomeGame extends AbstractCalculator {
    private static final Logger logger = LoggerFactory.getLogger(HomeGame.class);

    public HomeGame(ResultRepository resultRepository, StandingRepository standingRepository) {
        super(resultRepository, standingRepository);
    }

    @Override
    public void calculate(Forecast forecast, ForecastDetail forecastDetail) {
        StringBuilder messageBuilder = new StringBuilder();
        //get last 3 played home games
        List<Result> homeResults = super.getPlayedGamesForTeam(forecast, forecastDetail.getTeam(), true);
        if (homeResults.size() < 3) {
            logger.warn("Team {} has played {} home games instead of 3 for forecast of round {}, can not calculate correct home points", forecastDetail.getTeam().getName(), homeResults.size(), forecast.getRound());
            handleNotEnoughGames(homeResults, forecastDetail);
        }
        // sort the list on roundNumber, most recent first
        List<Result> sortedHomeResults = homeResults.stream().sorted(Comparator.comparingInt(Result::getRoundNumber).reversed()).collect(Collectors.toList());
        //get algorithm
        Algorithm algorithm = forecast.getAlgorithm();
        int teams = forecast.getLeague().getTeams().size();
        // set init message
        messageBuilder.append(this.createInitMessage(algorithm, forecastDetail.getTeam()));
        int homeScore = 0;
        int index = 1;
        for (Result result : sortedHomeResults) {
            Team opponent = result.getAwayTeam();
            int rankingOpponent = super.getOpponentRanking(forecast, opponent, result.getRoundNumber());
            CalculatorMessage calculatorMessage = new CalculatorMessage();
            calculatorMessage.setAlgorithm(algorithm);
            calculatorMessage.setOpponentStanding(rankingOpponent);
            calculatorMessage.setTotalTeams(teams);
            if (isWinGame(forecastDetail.getTeam(), result)) {
                messageBuilder.append(this.appendResultMessage(WIN,result, true, index));
                int homeWinPoints = algorithm.getHomePoints().getWin();
                int opponentStandingPoints = teams - rankingOpponent;
                int currentWinPoints = homeWinPoints + opponentStandingPoints;
                homeScore = homeScore + currentWinPoints;
                calculatorMessage.setFinalScore(currentWinPoints);
                calculatorMessage.setInitScore(homeWinPoints);
                messageBuilder.append(this.appendScoreMessageWinDraw(calculatorMessage));
            } else if (isLoseGame(forecastDetail.getTeam(), result)) {
                messageBuilder.append(this.appendResultMessage(LOST,result, true, index));
                int homeLosePoints = algorithm.getHomePoints().getLose();
                int totalScore = homeLosePoints - rankingOpponent;
                homeScore = homeScore + totalScore;
                calculatorMessage.setTotalScore(totalScore);
                calculatorMessage.setHome(true);
                messageBuilder.append(this.appendScoreLostMessage(calculatorMessage));
            } else {
                // draw
                messageBuilder.append(this.appendResultMessage(DRAW,result, true, index));
                int homeDrawPoints = algorithm.getHomePoints().getDraw();
                int opponentStandingPoints = teams - rankingOpponent;
                int currentDrawPoints = homeDrawPoints + opponentStandingPoints;
                homeScore = homeScore + currentDrawPoints;
                calculatorMessage.setFinalScore(currentDrawPoints);
                calculatorMessage.setInitScore(homeDrawPoints);
                messageBuilder.append(this.appendScoreMessageWinDraw(calculatorMessage));
            }
            index++;
        }
        messageBuilder.append("<br><br>")
                .append("<b>")
                .append("Eind score thuis wedstrijden : ")
                .append(homeScore)
                .append("</b>")
                .append("<br>");
        forecastDetail.setHomeScore(homeScore);
        forecastDetail.setMessage(messageBuilder.toString());
    }



    private String createInitMessage(Algorithm algorithm, Team team) {
        return "<h2>Overzicht ploeg " +
                team.getName() +
                "</h2><h3>Berekening thuiswedstrijden</h3>" +
                "Winst : " +
                algorithm.getHomePoints().getWin() +
                " punten + (aantal teams - rank tegenstrever)" +
                "<br>" +
                "verlies : " +
                algorithm.getHomePoints().getLose() +
                " punten - (rank tegenstrever)" +
                "<br>" +
                "geijkspel : " +
                algorithm.getHomePoints().getDraw() +
                " punten  + (aantal teams - rank tegenstrever)"+
                "<br>";

    }
}
