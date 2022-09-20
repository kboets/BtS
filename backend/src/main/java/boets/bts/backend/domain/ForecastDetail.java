package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name = "FORECASTDETAIL")
public class ForecastDetail implements Serializable {

    @Id
    @Column(name="forecast_detail_id")
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
    private BigInteger gameScore;

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

    public BigInteger getGameScore() {
        return gameScore;
    }

    public void setGameScore(BigInteger gameScore) {
        this.gameScore = gameScore;
    }

    public Result getNextGame() {
        return nextGame;
    }

    public void setNextGame(Result nextGame) {
        this.nextGame = nextGame;
    }
}
