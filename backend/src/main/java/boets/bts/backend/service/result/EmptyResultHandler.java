package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Team;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.repository.team.TeamSpecs;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.results.ResultClient;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Retrieves and saves all result for league for all matches
 */
@Component
public class EmptyResultHandler implements ResultHandler {

    private ResultRepository resultRepository;
    private ResultClient resultClient;
    private ResultMapper resultMapper;
    private TeamRepository teamRepository;
    private LeagueRepository leagueRepository;


    public EmptyResultHandler(ResultRepository resultRepository, ResultClient resultClient, ResultMapper resultMapper, TeamRepository teamRepository, LeagueRepository leagueRepository) {
        this.resultRepository = resultRepository;
        this.resultClient = resultClient;
        this.resultMapper = resultMapper;
        this.teamRepository = teamRepository;
        this.leagueRepository = leagueRepository;
    }

    @Override
    public List<Result> getResultForLeague(Long leagueId) {
        List<ResultDto> resultDtos = resultClient.retrieveAllResultForLeague(leagueId, WebUtils.getCurrentSeason()).orElseGet(Collections::emptyList);
        List<Result> resultList = resultMapper.toResults(resultDtos);
        List<Result> resultWithObjects = resultList.stream()
                .peek(result -> result.setAwayTeam(teamRepository.findOne(TeamSpecs.getTeamByTeamId(result.getAwayTeam().getTeamId()))
                        .orElseThrow(() -> new NotFoundException(String.format("Could not find team with id %s", result.getAwayTeam().getTeamId())))))
                .peek(result -> result.setHomeTeam(teamRepository.findOne(TeamSpecs.getTeamByTeamId(result.getHomeTeam().getTeamId()))
                        .orElseThrow(() -> new NotFoundException(String.format("Could not find team with id %s", result.getHomeTeam().getTeamId())))))
                .peek(result -> result.setLeague(leagueRepository.findById(result.getLeague().getId())
                        .orElseThrow(() -> new NotFoundException(String.format("Could not find league with id %s", result.getLeague().getId())))))
                .collect(Collectors.toList());

        return resultRepository.saveAll(resultWithObjects);
    }
}
