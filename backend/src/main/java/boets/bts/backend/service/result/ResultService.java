package boets.bts.backend.service.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.LeagueService;
import boets.bts.backend.service.TeamService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.results.ResultClient;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultService {

    private Logger logger = LoggerFactory.getLogger(ResultService.class);

    private ResultRepository resultRepository;
    private ResultHandlerFactory resultHandlerFactory;
    private ResultMapper resultMapper;
    private RoundService roundService;
    private LeagueService leagueService;
    private TeamService teamService;

    public ResultService(ResultRepository resultRepository, ResultHandlerFactory resultHandlerFactory, ResultMapper resultMapper, RoundService roundService, LeagueService leagueService, TeamService teamService) {
        this.resultRepository = resultRepository;
        this.resultHandlerFactory = resultHandlerFactory;
        this.resultMapper = resultMapper;
        this.roundService = roundService;
        this.leagueService = leagueService;
        this.teamService = teamService;
    }

    public List<ResultDto> retrieveAllResultsForLeague(Long leagueId) {
        List<Result> results = resultRepository.findAll(ResultSpecs.getResultByLeague(leagueId));
        ResultHandler resultHandler = resultHandlerFactory.getResultHandler(results.isEmpty());
        return resultMapper.toResultDtos(resultHandler.getResultForLeague(leagueId));
    }

}
