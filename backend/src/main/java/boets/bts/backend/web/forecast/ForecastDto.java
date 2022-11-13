package boets.bts.backend.web.forecast;

import boets.bts.backend.domain.ForecastResult;
import boets.bts.backend.web.algorithm.AlgorithmDto;
import boets.bts.backend.web.league.LeagueDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ForecastDto {

    private Long id;
    private LeagueDto league;
    private int round;
    private int season;
    private ForecastResult forecastResult;
    private String message;
    private AlgorithmDto algorithmDto;
    private LocalDateTime date;
    private List<ForecastDetailDto> forecastDetails;


    public ForecastDto() {
    }

    public LeagueDto getLeague() {
        return league;
    }

    public void setLeague(LeagueDto league) {
        this.league = league;
    }

    public List<ForecastDetailDto> getForecastDetails() {
        if(forecastDetails == null) {
            this.forecastDetails = new ArrayList<>();
        }
        return forecastDetails;
    }

    public void setForecastDetails(List<ForecastDetailDto> forecastDetails) {
        this.forecastDetails = forecastDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ForecastResult getForecastResult() {
        return forecastResult;
    }

    public void setForecastResult(ForecastResult forecastResult) {
        this.forecastResult = forecastResult;
    }

    public AlgorithmDto getAlgorithmDto() {
        return algorithmDto;
    }

    public void setAlgorithmDto(AlgorithmDto algorithmDto) {
        this.algorithmDto = algorithmDto;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}