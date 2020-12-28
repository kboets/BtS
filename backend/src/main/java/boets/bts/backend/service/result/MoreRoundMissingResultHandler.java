package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.results.IResultClient;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Retrieves result when results are missing for different rounds.
 */
@Component
@Transactional
public class MoreRoundMissingResultHandler extends AbstractResultHandler {

    public MoreRoundMissingResultHandler(ResultRepository resultRepository, IResultClient resultClient, ResultMapper resultMapper, TeamRepository teamRepository, LeagueRepository leagueRepository, RoundService roundService, RoundRepository roundRepository) {
        super(resultRepository, resultClient, resultMapper, teamRepository, leagueRepository, roundService, roundRepository);
    }

    @Override
    public boolean accepts(List<Result> allResults, List<Result> allNonFinishedResult, String currentRound) {
        List<Result> nonFinishedOtherRound = allNonFinishedResult.stream()
                .filter(result -> !result.getRound().equals(currentRound))
                .collect(Collectors.toList());
        return !allResults.isEmpty()  && !nonFinishedOtherRound.isEmpty();
    }

    @Override
    public List<Result> getResult(Long leagueId, List<Result> allNonFinishedResult, String currentRound) throws Exception {
        List<ResultDto> resultDtos = resultClient.retrieveAllResultForLeague(leagueId, WebUtils.getCurrentSeason()).orElseGet(Collections::emptyList);
        List<ResultDto> allNonFinishedResultDtos = resultMapper.toResultDtos(allNonFinishedResult);
        List<Result> toBeHandled = resultMapper.toResults(super.verifyAndUpdate(allNonFinishedResultDtos, resultDtos));
        if(!toBeHandled.isEmpty()) {
            return resultRepository.saveAll(toBeHandled);
        } else {
            return toBeHandled;
        }
    }


}
