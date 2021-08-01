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
import boets.bts.backend.web.results.IResultClient;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class NonEmptyResultHandler extends AbstractResultHandler {

    private Logger logger = LoggerFactory.getLogger(NonEmptyResultHandler.class);

    public NonEmptyResultHandler(ResultRepository resultRepository, IResultClient resultClient, ResultMapper resultMapper, TeamRepository teamRepository, LeagueRepository leagueRepository, RoundService roundService, RoundRepository roundRepository, AdminService adminService) {
        super(resultRepository, resultClient, resultMapper, teamRepository, leagueRepository, roundService, roundRepository, adminService);
    }

    @Override
    public boolean accepts(List<Result> allResults) {
        return !allResults.isEmpty();
    }

    @Override
    public List<Result> getResult(League league) throws Exception {
        List<Result> allResults = resultRepository.findAll(ResultSpecs.forLeague(league));
        List<Result> allNonFinished = allResults.stream().filter(result -> result.getEventDate().isBefore(LocalDate.now()) && !result.getMatchStatus().equals("Match Finished")).collect(Collectors.toList());
        logger.info("Total number of non finished results {} for league {} ", allNonFinished.size(), league.getName());
        if(!allNonFinished.isEmpty()) {
            List<ResultDto> clientResultDtos = resultClient.retrieveAllResultForLeague(league.getId(), adminService.getCurrentSeason()).orElseGet(Collections::emptyList);
            boolean isUpdated = false;
            for(Result missingResult : allNonFinished) {
                for(ResultDto resultDto: clientResultDtos) {
                    if(missingResult.getHomeTeam().getTeamId().toString().equals(resultDto.getHomeTeam().getTeamId())
                            && missingResult.getAwayTeam().getTeamId().toString().equals(resultDto.getAwayTeam().getTeamId())) {
                        missingResult.setMatchStatus(resultDto.getMatchStatus());
                        missingResult.setGoalsHomeTeam(resultDto.getGoalsHomeTeam());
                        missingResult.setGoalsAwayTeam(resultDto.getGoalsAwayTeam());
                        isUpdated = true;
                        logger.info("Following result is updated {} ", missingResult.getId());
                    }
                }
            }
            if(isUpdated) {
                resultRepository.saveAll(allNonFinished);
            }
        }
        allResults = resultRepository.findAll(ResultSpecs.forLeague(league));
        if(allResults.stream().anyMatch(result -> result.getRoundNumber() == null)) {
            List<Result> updatedResults = allResults.stream()
                    .filter(result -> result.getRoundNumber() == null)
                    .peek(result -> result.setRoundNumber(getRoundNumber(result.getRound())))
                    .collect(Collectors.toList());
            resultRepository.saveAll(updatedResults);
        }
        return resultRepository.findAll(ResultSpecs.forLeague(league));
    }


}
