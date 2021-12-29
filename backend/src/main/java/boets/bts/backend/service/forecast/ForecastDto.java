package boets.bts.backend.service.forecast;

import boets.bts.backend.web.league.LeagueDto;

import java.util.ArrayList;
import java.util.List;

public class ForecastDto {

    private LeagueDto league;

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


}