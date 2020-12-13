package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.LeagueService;
import boets.bts.backend.service.TeamService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResultService {

    private Logger logger = LoggerFactory.getLogger(ResultService.class);

    private ResultRepository resultRepository;
    private ResultHandlerSelector resultHandlerSelector;
    private ResultMapper resultMapper;
    private RoundService roundService;
    private LeagueService leagueService;
    private TeamService teamService;

    public ResultService(ResultRepository resultRepository, ResultHandlerSelector resultHandlerSelector, ResultMapper resultMapper, RoundService roundService, LeagueService leagueService, TeamService teamService) {
        this.resultRepository = resultRepository;
        this.resultHandlerSelector = resultHandlerSelector;
        this.resultMapper = resultMapper;
        this.roundService = roundService;
        this.leagueService = leagueService;
        this.teamService = teamService;
    }

    public List<ResultDto> retrieveAllResultsForLeague(Long leagueId) throws Exception {
        Round currentRound = roundService.getCurrentRoundForLeague(leagueId);
        List<Result> allResults = resultRepository.findAll(ResultSpecs.getResultByLeague(leagueId));
        List<ResultDto> allHandledResults = resultMapper.toResultDtos(allResults);
        List<Result> allNonFinishedResults = resultRepository.findAll(ResultSpecs.getAllNonFinishedResultUntilRound(leagueId, currentRound.getRound()));
        Optional<ResultHandler> resultOptionalHandler = resultHandlerSelector.select(allResults, allNonFinishedResults, currentRound.getRound());
        if(resultOptionalHandler.isPresent()) {
            logger.info("Not all results are found for the current league {} ", leagueId);
            allHandledResults.addAll(resultMapper.toResultDtos(resultOptionalHandler.get().getResult(leagueId, allNonFinishedResults, currentRound.getRound())));
        }
        return allHandledResults;

    }



}
