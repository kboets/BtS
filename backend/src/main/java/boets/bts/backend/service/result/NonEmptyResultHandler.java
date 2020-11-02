package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.results.ResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves all result until the current round.  
 */
@Component
public class NonEmptyResultHandler implements ResultHandler {

    private Logger logger = LoggerFactory.getLogger(NonEmptyResultHandler.class);

    private ResultMapper resultMapper;
    private RoundService roundService;
    private ResultRepository resultRepository;
    private RoundRepository roundRepository;

    public NonEmptyResultHandler(ResultMapper resultMapper, RoundService roundService, ResultRepository resultRepository, RoundRepository roundRepository) {
        this.resultMapper = resultMapper;
        this.roundService = roundService;
        this.resultRepository = resultRepository;
        this.roundRepository = roundRepository;
    }

    @Override
    public List<Result> getResultForLeague(Long leagueId) {
        try {
            //TODO CHECK if previous or current round should be taken
            Round previousRound = roundService.getPreviousCurrentRoundForLeague(leagueId);
            List<Result> resultForRound = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(leagueId, previousRound.getRound()));
            if(resultForRound.isEmpty()) {
                //handle missing result for rounds
            }
            if(resultForRound.size() != 9) {
                //handle missing result for this round
            }
            return resultForRound;
        } catch (Exception e) {
            logger.error("Could not retrieve current round for league with id {}", leagueId);
        }
        return null;
    }

    private List<Result> handleAllMissingResult(Long leagueId, Round previousRound) throws Exception {
        //1.  get latest round on which there was a result
        List<Result> lastPersistedResults = resultRepository.findAll(ResultSpecs.getResultByLeague(leagueId));
        int lastIndex = lastPersistedResults.size()-1;
        List<Round> missingRounds = retrieveMissingRounds(lastPersistedResults.get(lastIndex), previousRound);
        //call client to get all missing rounds
        
        return null;
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
