package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "FORECASTDETAIL")
public class ForecastDetail implements Serializable {

    @Id
    @Column(name="forecast_detail_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_id", referencedColumnName = "id")
    private Team opponent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nextGame_id", referencedColumnName = "result_id")
    private Result nextGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forecast_id", referencedColumnName = "forecast_id")
    private Forecast forecast;
    @Column
    private int finalScore;
    @Column
    private int homeScore;
    @Column
    private int awayScore;
    @Column
    private int bonusMalusScore;
    @Column
    @Enumerated(EnumType.STRING)
    private ForecastResult forecastResult;
    @Column
    private String message;
    @Column
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getOpponent() {
        return opponent;
    }

    public void setOpponent(Team opponent) {
        this.opponent = opponent;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
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

    public Result getNextGame() {
        return nextGame;
    }

    public void setNextGame(Result nextGame) {
        this.nextGame = nextGame;
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

    public int getBonusMalusScore() {
        return bonusMalusScore;
    }

    public void setBonusMalusScore(int bonusMalusScore) {
        this.bonusMalusScore = bonusMalusScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForecastDetail that = (ForecastDetail) o;
        return finalScore == that.finalScore && homeScore == that.homeScore && awayScore == that.awayScore && bonusMalusScore == that.bonusMalusScore && Objects.equals(id, that.id) && Objects.equals(team, that.team) && Objects.equals(opponent, that.opponent) && Objects.equals(nextGame, that.nextGame) && Objects.equals(forecast, that.forecast) && forecastResult == that.forecastResult && Objects.equals(message, that.message) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, team, opponent, nextGame, forecast, finalScore, homeScore, awayScore, bonusMalusScore, forecastResult, message, errorMessage);
    }

    @Override
    public String toString() {
        return "ForecastDetail{" +
                "id=" + id +
                ", team=" + team +
                ", opponent=" + opponent +
                ", nextGame=" + nextGame +
                ", forecast=" + forecast +
                ", finalScore=" + finalScore +
                ", homeScore=" + homeScore +
                ", awayScore=" + awayScore +
                ", bonusMalusScore=" + bonusMalusScore +
                ", forecastResult=" + forecastResult +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }


}
