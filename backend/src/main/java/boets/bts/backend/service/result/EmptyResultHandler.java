package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.results.ResultClient;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Retrieves and saves all result for league for all matches
 */
@Component
public class EmptyResultHandler extends AbstractResultHandler {


    public EmptyResultHandler(ResultRepository resultRepository, ResultClient resultClient, ResultMapper resultMapper, TeamRepository teamRepository, LeagueRepository leagueRepository, RoundService roundService, RoundRepository roundRepository) {
        super(resultRepository, resultClient, resultMapper, teamRepository, leagueRepository, roundService, roundRepository);
    }

    @Override
    public List<Result> getResultForLeague(Long leagueId) {
        List<ResultDto> resultDtos = resultClient.retrieveAllResultForLeague(leagueId, WebUtils.getCurrentSeason()).orElseGet(Collections::emptyList);
        List<Result> resultList = resultMapper.toResults(resultDtos);
        return expandAndSaveResult(resultList);
    }


}
