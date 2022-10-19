package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "FORECAST")
public class Forecast implements Serializable {

    @Id
    @Column(name="forecast_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id")
    private League league;
    @Column(nullable = false)
    private int round;
    @Column(nullable = false)
    private int season;
    @Enumerated(EnumType.STRING)
    private ForecastResult forecastResult;
    @Column
    private String message;
    @ManyToOne
    @JoinColumn(name = "algorithm_id", referencedColumnName = "algorithm_id")
    private Algorithm algorithm;
    @OneToMany(mappedBy = "forecast", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ForecastDetail> forecastDetails;
    @Column
    private LocalDateTime date;


    public Forecast() {
    }

    public Forecast(League league, int round, Algorithm algorithm) {
        this.league = league;
        this.round = round;
        this.algorithm = algorithm;
    }


    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public List<ForecastDetail> getForecastDetails() {
        if (forecastDetails == null) {
            forecastDetails = new ArrayList<>();
        }
        return forecastDetails;
    }

    public void addForecastDetail(ForecastDetail forecastDetail) {
        getForecastDetails().add(forecastDetail);
    }

    public void setForecastDetails(List<ForecastDetail> forecastDetails) {
        this.forecastDetails = forecastDetails;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Forecast forecast = (Forecast) o;
        return round == forecast.round && season == forecast.season && Objects.equals(id, forecast.id) && Objects.equals(league, forecast.league) && forecastResult == forecast.forecastResult && Objects.equals(message, forecast.message) && Objects.equals(algorithm, forecast.algorithm) && Objects.equals(forecastDetails, forecast.forecastDetails) && Objects.equals(date, forecast.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, league, round, season, forecastResult, message, algorithm, forecastDetails, date);
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "id=" + id +
                ", league=" + league +
                ", round=" + round +
                ", season=" + season +
                ", forecastResult=" + forecastResult +
                ", message='" + message + '\'' +
                ", algorithm=" + algorithm +
                ", date=" + date +
                '}';
    }

    public String dumpToLog() {
        StringBuilder log = new StringBuilder(this.toString());
        this.getForecastDetails().stream().forEach(forecastDetail -> {
            log.append(forecastDetail.toString());
        });
        return log.toString();
    }


}
