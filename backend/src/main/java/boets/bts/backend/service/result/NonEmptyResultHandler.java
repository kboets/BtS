package boets.bts.backend.service.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.results.IResultClient;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NonEmptyResultHandler extends AbstractResultHandler {

    public NonEmptyResultHandler(ResultRepository resultRepository, IResultClient resultClient, ResultMapper resultMapper, TeamRepository teamRepository, LeagueRepository leagueRepository, RoundService roundService, RoundRepository roundRepository, AdminService adminService) {
        super(resultRepository, resultClient, resultMapper, teamRepository, leagueRepository, roundService, roundRepository, adminService);
    }

    @Override
    public boolean accepts(List<Result> allResults) {
        return !allResults.isEmpty();
    }

    @Override
    public List<Result> getResult(League league) throws Exception {
        List<ResultDto> clientResultDtos = resultClient.retrieveAllResultForLeague(league.getId(), adminService.getCurrentSeason()).orElseGet(Collections::emptyList);
        List<Result> allResults = resultRepository.findAll(ResultSpecs.getResultByLeague(league));
        List<ResultDto> clientAllNonFinishedResultDtos = clientResultDtos.stream().filter(resultDto -> !resultDto.getMatchStatus().equals("Match Finished") && resultDto.getEventDate().isBefore(LocalDate.now())).collect(Collectors.toList());
        List<Result> allNonFinished = allResults.stream().filter(result -> result.getEventDate().isBefore(LocalDate.now()) && !result.getMatchStatus().equals("Match Finished")).collect(Collectors.toList());
        boolean isUpdated = false;
        for(Result missingResult : allNonFinished) {
            for(ResultDto resultDto: clientAllNonFinishedResultDtos) {
                if(missingResult.getHomeTeam().getTeamId().toString().equals(resultDto.getHomeTeam().getTeamId())
                        && missingResult.getAwayTeam().getTeamId().toString().equals(resultDto.getAwayTeam().getTeamId())) {
                    missingResult.setMatchStatus(resultDto.getMatchStatus());
                    missingResult.setGoalsHomeTeam(resultDto.getGoalsHomeTeam());
                    missingResult.setGoalsAwayTeam(resultDto.getGoalsHomeTeam());
                    isUpdated = true;
                }
            }
        }
        if(isUpdated) {
            resultRepository.saveAll(allNonFinished);
        }
        return resultRepository.findAll(ResultSpecs.getResultByLeague(league));
    }
}
