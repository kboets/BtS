package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;

import java.util.List;

/**
 * Collects all the data needed to perform the forecast
 */
public class ForecastData {

    private League league;
    private List<Result> nextResults;
    private Round currentRound;
    private List<Result> finishedResults;

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Round getCurrentRound() {
        return currentRound;
    }
    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
    }

    public List<Result> getFinishedResults() {
        return finishedResults;
    }

    public void setFinishedResults(List<Result> finishedResults) {
        this.finishedResults = finishedResults;
    }

    public List<Result> getNextResults() {
        return nextResults;
    }

    public void setNextResults(List<Result> nextResults) {
        this.nextResults = nextResults;
    }
}
