package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.repository.team.TeamSpecs;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.results.ResultClient;
import boets.bts.backend.web.results.ResultMapper;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractResultHandler implements ResultHandler {

    protected ResultRepository resultRepository;
    protected ResultClient resultClient;
    protected ResultMapper resultMapper;
    protected TeamRepository teamRepository;
    protected LeagueRepository leagueRepository;
    protected RoundService roundService;
    protected RoundRepository roundRepository;

    public AbstractResultHandler(ResultRepository resultRepository, ResultClient resultClient, ResultMapper resultMapper, TeamRepository teamRepository, LeagueRepository leagueRepository, RoundService roundService, RoundRepository roundRepository) {
        this.resultRepository = resultRepository;
        this.resultClient = resultClient;
        this.resultMapper = resultMapper;
        this.teamRepository = teamRepository;
        this.leagueRepository = leagueRepository;
        this.roundService = roundService;
        this.roundRepository = roundRepository;
    }

    protected List<Result> expandAndSaveResult(List<Result> resultList) {
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

    public abstract List<Result> getResultForLeague(Long leagueId);
}
