package boets.bts.backend.service.forecast;

import boets.bts.backend.web.league.LeagueDto;

import java.util.ArrayList;
import java.util.List;

public class ForecastDto {

    private LeagueDto league;

    private List<ForecastDetailDto> forecastDetails;

    public ForecastDto() {
    }

    public ForecastDto(ForecastDto forecastDto) {
        this.setLeague(forecastDto.getLeague());
        List<ForecastDetailDto> copiedForecastDetails = new ArrayList<>();
        for(ForecastDetailDto forecastDetailDto: forecastDto.getForecastDetails()) {
            copiedForecastDetails.add(new ForecastDetailDto(forecastDetailDto));
        }
        this.setForecastDetails(copiedForecastDetails);
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