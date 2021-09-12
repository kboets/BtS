package boets.bts.backend.service.forecast;

import boets.bts.backend.web.league.LeagueDto;

import java.util.ArrayList;
import java.util.List;

public class Forecast {

    private String forecastType;
    private LeagueDto league;
    private List<ForecastDetail> forecastDetails;

    public Forecast(String forecastType) {
        this.forecastType = forecastType;
    }

    public Forecast() {
    }

    public LeagueDto getLeague() {
        return league;
    }

    public void setLeague(LeagueDto league) {
        this.league = league;
    }

    public List<ForecastDetail> getForecastDetails() {
        if(forecastDetails == null) {
            this.forecastDetails = new ArrayList<>();
        }
        return forecastDetails;
    }

    public String getForecastType() {
        return forecastType;
    }

    public void setForecastType(String forecastType) {
        this.forecastType = forecastType;
    }
}