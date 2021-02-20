package boets.bts.backend.web.forecast;

import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.results.ResultDto;

import java.util.List;

public class LeagueResultsDto {
    private LeagueDto league;
    private List<ResultDto> results;

    public LeagueDto getLeague() {
        return league;
    }

    public void setLeague(LeagueDto league) {
        this.league = league;
    }

    public List<ResultDto> getResults() {
        return results;
    }

    public void setResults(List<ResultDto> results) {
        this.results = results;
    }
}
