package boets.bts.backend.web.forecast;

import boets.bts.backend.domain.ForecastResult;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ForecastDetailDto {

    private Long id;
    private TeamDto team;
    private TeamDto opponent;
    private ResultDto nextGame;
    private int finalScore;
    private int homeScore;
    private int awayScore;
    private int opponentScore;
    private ForecastResult forecastResult;
    private String message;
    private String errorMessage;

    public ForecastDetailDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamDto getTeam() {
        return team;
    }

    public void setTeam(TeamDto team) {
        this.team = team;
    }

    public TeamDto getOpponent() {
        return opponent;
    }

    public void setOpponent(TeamDto opponent) {
        this.opponent = opponent;
    }

    public ResultDto getNextGame() {
        return nextGame;
    }

    public void setNextGame(ResultDto nextGame) {
        this.nextGame = nextGame;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }

    public ForecastResult getForecastResult() {
        return forecastResult;
    }

    public void setForecastResult(ForecastResult forecastResult) {
        this.forecastResult = forecastResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
