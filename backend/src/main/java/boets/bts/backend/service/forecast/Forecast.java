package boets.bts.backend.service.forecast;

import boets.bts.backend.web.league.LeagueDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Forecast implements Serializable {

    private LeagueDto league;

    private List<ForecastDetail> forecastDetails;

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

    public void setForecastDetails(List<ForecastDetail> forecastDetails) {
        this.forecastDetails = forecastDetails;
    }


}