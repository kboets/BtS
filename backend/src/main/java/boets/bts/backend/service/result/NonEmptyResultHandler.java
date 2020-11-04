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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            //TODO CHECK if previous or current round should be taken
            Round previousRound = roundService.getPreviousCurrentRoundForLeague(leagueId);
            List<Result> resultForRound = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(leagueId, previousRound.getRound()));
            if(resultForRound.isEmpty()) {
                resultForRound = handleAllMissingResult(leagueId,previousRound);
            }
            if(resultForRound.size() != 9) {
                resultForRound = handleMissingResultForRound(leagueId, previousRound);
            }
            return expandAndSaveResult(resultForRound);
        } catch (Exception e) {
            logger.error("Could not retrieve current round for league with id {}", leagueId);
        }
        return null;
    }

    private List<Result> handleMissingResultForRound(Long leagueId, Round previousRound) {
        List<Result> toBeHandled = new ArrayList<>();
        //1.  get results for this round and league
        List<Result> resultsForRound = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(leagueId, previousRound.getRound()));
        //2. find results that don't have result finished
        List<Result> nonFinishedLeagueResult = resultsForRound.stream()
                .filter(result -> !result.getMatchStatus().equals(MatchStatus.FINISHED))
                .collect(Collectors.toList());
        Optional<List<ResultDto>> resultOptionalDtos = resultClient.retrieveAllResultForLeagueAndRound(leagueId, previousRound.getRound());
        if(resultOptionalDtos.isPresent()) {
            List<ResultDto> resultDtos = resultOptionalDtos.get();
            for(Result missingResult: nonFinishedLeagueResult) {
                for(ResultDto resultDto: resultDtos) {
                    if(missingResult.getLeague().getId().equals(resultDto.getLeague().getLeague_id())) {
                        toBeHandled.add(resultMapper.toResult(resultDto));
                        continue;
                    }
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
