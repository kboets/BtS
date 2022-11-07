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
        StringBuilder messageBuilder = new StringBuilder(forecastDetail.getMessage());
        //get last 3 played away games
        List<Result> awayResults = super.getPlayedGamesForTeam(forecast, forecastDetail.getTeam(), false);
        if (awayResults.size() < 3) {
            logger.warn("Team {} has not played 3 away games for forecast of round {}, can not calculate correct away points", forecastDetail.getTeam().getName(), forecast.getRound());
            forecastDetail.setErrorMessage(String.format("Team %s has not played 3 away games, can not calculate correct away points", forecastDetail.getTeam().getName()));
            if (awayResults.size() == 2) {
                forecastDetail.setForecastResult(ForecastResult.WARNING);
            } else {
                forecastDetail.setForecastResult(ForecastResult.FATAL);
                return;
            }
        }
        //get algorithm
        Algorithm algorithm = forecast.getAlgorithm();
        int teams = forecast.getLeague().getTeams().size();
        // set init message
        messageBuilder.append(this.createInitMessage(algorithm));
        //calculate score
        int awayScore = 0;
        for (Result result : awayResults) {
            Team opponent = result.getHomeTeam();
            int rankingOpponent = getOpponentRanking(forecast, opponent, result.getRoundNumber());
            if (isWinGame(forecastDetail.getTeam(), result)) {
                messageBuilder.append(this.appendResultMessage(WIN,result, false));
                int awayWinPoints = algorithm.getAwayPoints().getWin();
                int opponentStandingPoints = teams - rankingOpponent;
                int currentWinPoints = awayWinPoints + opponentStandingPoints;
                awayScore = awayScore + currentWinPoints;
                messageBuilder.append(this.appendScoreMessageWinDraw(teams, rankingOpponent, currentWinPoints, awayWinPoints));
            } else if (isLoseGame(forecastDetail.getTeam(), result)) {
                messageBuilder.append(this.appendResultMessage(LOST,result, false));
                int awayLosePoints = algorithm.getAwayPoints().getLose();
                int totalScore = awayLosePoints - rankingOpponent;
                awayScore = awayScore + totalScore;
                messageBuilder.append(this.appendScoreLostMessage(rankingOpponent, totalScore, algorithm, false));
            } else {
                // draw
                messageBuilder.append(this.appendResultMessage(DRAW,result, false));
                int awayDrawPoints = algorithm.getAwayPoints().getDraw();
                int opponentStandingPoints = teams - rankingOpponent;
                int currentDrawPoints = awayDrawPoints + opponentStandingPoints;
                awayScore = awayScore + currentDrawPoints;
                messageBuilder.append(this.appendScoreMessageWinDraw(teams, rankingOpponent, currentDrawPoints, awayDrawPoints));
            }
        }
        messageBuilder.append("<br><b>")
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
