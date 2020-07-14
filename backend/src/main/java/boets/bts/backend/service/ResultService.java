package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
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
    private ResultMapper resultMapper;
    private RoundService roundService;
    private ResultClient resultClient;
    private LeagueService leagueService;
    private TeamService teamService;

    public ResultService(ResultRepository resultRepository, ResultMapper resultMapper, RoundService roundService, ResultClient resultClient,
        LeagueService leagueService, TeamService teamService) {
        this.resultRepository = resultRepository;
        this.resultMapper = resultMapper;
        this.roundService = roundService;
        this.resultClient = resultClient;
        this.leagueService = leagueService;
        this.teamService = teamService;
    }

    public List<ResultDto> retrieveAllResultsForLeague(Long leagueId) {
        League league = leagueService.getLeagueById(leagueId);
        List<Result> results = resultRepository.findAll(ResultSpecs.getResultByLeagueAndSeason(league));
        if(results.isEmpty()) {
            List<ResultDto> resultDtos = resultClient.retrieveAllResultForLeague(leagueId).orElseGet(Collections::emptyList);
            List<Result> resultList = resultMapper.toResults(resultDtos);
            List<Result> resultListWith = resultList.stream()
                    .peek(result -> result.setRound(roundService.getRoundByName(result.getRound().getRound())))
                    .peek(result -> result.setLeague(leagueService.getLeagueById(result.getLeague().getId())))
                    .peek(result -> result.setAwayTeam(teamService.getTeamById(result.getAwayTeam().getId())))
                    .peek(result -> result.setHomeTeam(teamService.getTeamById(result.getHomeTeam().getId())))
                    .collect(Collectors.toList());
            resultListWith = resultRepository.saveAll(resultListWith);
            return resultMapper.toResultDtos(resultListWith);
        }
        return resultMapper.toResultDtos(results);
    }

}
