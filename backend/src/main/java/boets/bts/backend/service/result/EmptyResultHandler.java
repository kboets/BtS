package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.results.IResultClient;
import boets.bts.backend.web.results.ResultClient;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Retrieves result when no results could be find.
 */
@Component
public class EmptyResultHandler extends AbstractResultHandler {


    public EmptyResultHandler(ResultRepository resultRepository, IResultClient resultClient, ResultMapper resultMapper, TeamRepository teamRepository, LeagueRepository leagueRepository, RoundService roundService, RoundRepository roundRepository, AdminService adminService) {
        super(resultRepository, resultClient, resultMapper, teamRepository, leagueRepository, roundService, roundRepository, adminService);
    }

    @Override
    public boolean accepts(List<Result> allResults, List<Result> allNonFinishedResult, String currentRound) {
        return allResults.isEmpty();
    }

    @Override
    public List<Result> getResult(Long leagueId, List<Result> allNonFinishedResult, String currentRound) throws Exception {
        List<ResultDto> resultDtos = resultClient.retrieveAllResultForLeague(leagueId, adminService.getCurrentSeason()).orElseGet(Collections::emptyList);
        List<Result> resultList = resultMapper.toResults(resultDtos);
        return expandAndSaveResult(resultList, leagueId);
    }


}
