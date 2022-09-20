package boets.bts.backend.service.forecast2.validator;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastResult;
import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.web.exception.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Checks if each team of the league has at least played 6 games.
 */
@Component
public class IsLeagueReadyRule implements ForecastRule {

    private final LeagueRepository leagueRepository;
    private final ResultRepository resultRepository;
    public static final String errorMessage = "All teams not played 6 games";

    public IsLeagueReadyRule(LeagueRepository leagueRepository, ResultRepository resultRepository) {
        this.leagueRepository = leagueRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public boolean validate(Forecast forecast) {
        League league = leagueRepository.findById(forecast.getLeague().getId()).orElseThrow(() -> new NotFoundException(String.format("Could not find League  %s", forecast.getLeague().getName())));
        int teamSize = league.getTeams().size();
        int expectedResult = teamSize/2 * 6;
        List<Result> results = resultRepository.findAll(ResultSpecs.getAllFinishedResult(league.getId()));
        if (results.size() < expectedResult) {
            forecast.setForecastResult(ForecastResult.FATAL);
            forecast.setMessage(errorMessage);
            return false;
        }
        return true;
    }
}
