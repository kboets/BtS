package boets.bts.backend.service.result;

import boets.bts.backend.domain.MatchStatus;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.results.IResultClient;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Retrieves result when only results are missing for the current round.
 */
@Component
@Transactional
public class CurrentRoundMissingResultHandler extends AbstractResultHandler {

    private Logger logger = LoggerFactory.getLogger(CurrentRoundMissingResultHandler.class);

    public CurrentRoundMissingResultHandler(ResultRepository resultRepository, IResultClient resultClient, ResultMapper resultMapper, TeamRepository teamRepository, LeagueRepository leagueRepository, RoundService roundService, RoundRepository roundRepository) {
        super(resultRepository, resultClient, resultMapper, teamRepository, leagueRepository, roundService, roundRepository);
    }


    @Override
    public boolean accepts(List<Result> allResults, List<Result> allNonFinishedResult, String currentRound) {
        List<Result> nonFinishedOtherRound = allNonFinishedResult.stream()
                .filter(result -> !result.getRound().equals(currentRound))
                .collect(Collectors.toList());
        List<Result> nonFinishedCurrentRound = allNonFinishedResult.stream()
                .filter(result -> result.getRound().equals(currentRound))
                .collect(Collectors.toList());
        return !allResults.isEmpty() && nonFinishedCurrentRound.size() != 0 && nonFinishedOtherRound.isEmpty();
    }

    @Override
    public List<Result> getResult(Long leagueId, List<Result> allNonFinishedResult, String currentRound) throws Exception {
        List<Result> nonFinishedCurrentRound = allNonFinishedResult.stream()
                .filter(result -> result.getRound().equals(currentRound))
                .collect(Collectors.toList());
        List<Result> toBeHandled = new ArrayList<>();
        Optional<List<ResultDto>> resultOptionalDtos = resultClient.retrieveAllResultForLeagueAndRound(leagueId, currentRound);
        if(!resultOptionalDtos.isPresent()) {
            logger.warn("Could not download results for current round {} and league {} ", currentRound, leagueId);
            return toBeHandled;
        }
        List<ResultDto> resultDtos = resultOptionalDtos.get();
        List<ResultDto> missingResultDtos = resultMapper.toResultDtos(nonFinishedCurrentRound);
        for(ResultDto missingResult: missingResultDtos) {
            for(ResultDto resultDto: resultDtos) {
                if(missingResult.getHomeTeam().getTeamId().equals(resultDto.getHomeTeam().getTeamId())
                 && missingResult.getAwayTeam().getTeamId().equals(resultDto.getAwayTeam().getTeamId())) {
                    toBeHandled.add(resultMapper.toResult(resultDto));
                    continue;
                }
            }
        }
        return expandAndSaveResult(toBeHandled);
    }


 }
