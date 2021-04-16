package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Forecast {

    private ForecastType forecastType;
    private League league;
    private List<ForecastDetail> forecastDetails;

    public Forecast(ForecastType forecastType) {
        this.forecastType = forecastType;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public List<ForecastDetail> getForecastDetails() {
        if(forecastDetails == null) {
            this.forecastDetails = new ArrayList<>();
        }
        return forecastDetails;
    }

    public ForecastType getForecastType() {
        return forecastType;
    }

    public void setForecastType(ForecastType forecastType) {
        this.forecastType = forecastType;
    }
}