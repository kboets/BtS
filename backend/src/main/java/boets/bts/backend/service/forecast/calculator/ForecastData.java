package boets.bts.backend.service.forecast.calculator;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.round.RoundDto;

import java.util.List;

/**
 * Collects all the data needed to perform the forecast
 */
public class ForecastData {

    private LeagueDto league;
    private List<ResultDto> nextResults;
    private RoundDto currentRound;
    private List<ResultDto> finishedResults;

    public LeagueDto getLeague() {
        return league;
    }

    public void setLeague(LeagueDto league) {
        this.league = league;
    }

    public List<ResultDto> getNextResults() {
        return nextResults;
    }

    public void setNextResults(List<ResultDto> nextResults) {
        this.nextResults = nextResults;
    }

    public RoundDto getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(RoundDto currentRound) {
        this.currentRound = currentRound;
    }

    public List<ResultDto> getFinishedResults() {
        return finishedResults;
    }

    public void setFinishedResults(List<ResultDto> finishedResults) {
        this.finishedResults = finishedResults;
    }
}
