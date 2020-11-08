package boets.bts.backend.service.result;

import boets.bts.backend.domain.League;
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
import boets.bts.backend.web.results.ResultClient;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Retrieves all result until the current round.  
 */
@Component
public class NonEmptyResultHandler extends AbstractResultHandler {

    private Logger logger = LoggerFactory.getLogger(NonEmptyResultHandler.class);

    public NonEmptyResultHandler(ResultRepository resultRepository, IResultClient resultClient, ResultMapper resultMapper, TeamRepository teamRepository, LeagueRepository leagueRepository, RoundService roundService, RoundRepository roundRepository) {
        super(resultRepository, resultClient, resultMapper, teamRepository, leagueRepository, roundService, roundRepository);
    }


    @Override
    public List<Result> getResultForLeague(Long leagueId) {
        try {
            Round previousRound = roundService.getPreviousCurrentRoundForLeague(leagueId);
            List<Result> resultForRound = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(leagueId, previousRound.getRound()));
            List<Result> nonFinishedLeagueResult = resultForRound.stream()
                    .filter(result -> !result.getMatchStatus().equals(MatchStatus.FINISHED.getName()))
                    .collect(Collectors.toList());
            if(nonFinishedLeagueResult.isEmpty()) {
                resultForRound = handleAllMissingResult(leagueId,previousRound);
                return expandAndSaveResult(resultForRound);
            }

            if(nonFinishedLeagueResult.size() != 9) {
                resultForRound = handleMissingResultForRound(previousRound, nonFinishedLeagueResult);
                return expandAndSaveResult(resultForRound);
            }
            return nonFinishedLeagueResult;

        } catch (Exception e) {
            logger.error("Could not retrieve current round for league with id {}", leagueId);
            return Collections.EMPTY_LIST;
        }
    }

    private List<Result> handleMissingResultForRound(Round previousRound, List<Result> nonFinishedLeagueResult) {
        List<Result> toBeHandled = new ArrayList<>();
        Optional<List<ResultDto>> resultOptionalDtos = resultClient.retrieveAllResultForLeagueAndRound(nonFinishedLeagueResult.get(0).getLeague().getId(), previousRound.getRound());
        if(!resultOptionalDtos.isPresent()) {
            return toBeHandled;
        }
        List<ResultDto> resultDtos = resultOptionalDtos.get();
        List<ResultDto> nonFinishedResultDtos = resultMapper.toResultDtos(nonFinishedLeagueResult);
        for(ResultDto missingResult: nonFinishedResultDtos) {
            for(ResultDto resultDto: resultDtos) {
                if(missingResult.getHomeTeam().getTeamId().equals(resultDto.getHomeTeam().getTeamId())) {
                    toBeHandled.add(resultMapper.toResult(resultDto));
                    continue;
                }
            }
        }
        return toBeHandled;
    }

    private List<Result> handleAllMissingResult(Long leagueId, Round previousRound)  {
        //1.  get latest round on which there was a result
        List<Result> lastPersistedResults = resultRepository.findAll(ResultSpecs.getResultByLeague(leagueId));
        int lastIndex = lastPersistedResults.size()-1;
        List<ResultDto> allMissingRoundDtos = new ArrayList<>();
        List<Round> missingRounds = retrieveMissingRounds(lastPersistedResults.get(lastIndex), previousRound);
        //call client to get all missing rounds
        for(Round missingRound : missingRounds) {
            Optional<List<ResultDto>> resultOptionalDtos = resultClient.retrieveAllResultForLeagueAndRound(leagueId, missingRound.getRound());
            if(resultOptionalDtos.isPresent()) {
                allMissingRoundDtos.addAll(resultOptionalDtos.get());
            }
        }
        return resultMapper.toResults(allMissingRoundDtos);
    }

    private List<Round> retrieveMissingRounds(Result lastPersistedResult, Round previousRound)  {
        String lastPersistedRoundName = lastPersistedResult.getRound();
        Round lastRound = roundService.getRoundByName(lastPersistedRoundName);
        //calculate missing rounds
        List<Round> missingRounds = new ArrayList<>();
        long lastPersistedId = lastRound.getId();
        while(lastPersistedId != previousRound.getId()) {
            lastPersistedId++;
            missingRounds.add(roundRepository.getOne(lastPersistedId));
        }
        return missingRounds;
    }
 }
