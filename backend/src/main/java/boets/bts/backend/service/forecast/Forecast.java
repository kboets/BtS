package boets.bts.backend.service.forecast;

import boets.bts.backend.web.league.LeagueDto;

import java.util.ArrayList;
import java.util.List;

public class Forecast {

    private ForecastType forecastType;
    private LeagueDto league;
    private List<ForecastDetail> forecastDetails;

    public Forecast(ForecastType forecastType) {
        this.forecastType = forecastType;
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

    public ForecastType getForecastType() {
        return forecastType;
    }

    public void setForecastType(ForecastType forecastType) {
        this.forecastType = forecastType;
    }
}