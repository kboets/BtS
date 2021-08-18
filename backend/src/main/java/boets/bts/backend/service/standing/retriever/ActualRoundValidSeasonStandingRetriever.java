package boets.bts.backend.service.standing.retriever;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.standing.StandingRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultMapper;
import boets.bts.backend.web.round.RoundMapper;
import boets.bts.backend.web.standing.StandingClient;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.standing.StandingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Retrieves the actual standing of the current finished round of the current season that is already started.
 */
@Component
public class ActualRoundValidSeasonStandingRetriever extends AbstractStandingRetriever {

    private static final Logger logger = LoggerFactory.getLogger(ActualRoundValidSeasonStandingRetriever.class);

    public ActualRoundValidSeasonStandingRetriever(LeagueMapper leagueMapper, RoundMapper roundMapper, StandingClient standingClient, AdminService adminService, StandingMapper standingMapper, TeamRepository teamRepository, StandingRepository standingRepository, ResultRepository resultRepository, ResultMapper resultMapper) {
        super(leagueMapper, roundMapper, standingClient, adminService, standingMapper, teamRepository, standingRepository, resultRepository, resultMapper);
    }

    @Override
    public boolean accept(League league, Round currentRound, int roundNumber) {
        return !adminService.isHistoricData()
                && currentRound.getRoundNumber() == roundNumber
                && LocalDate.now().isAfter(league.getStartSeason())
                && !WebUtils.isWeekend();
    }

    @Override
    public List<Standing> getStanding(League league, Round currentRound, int roundNumber) {
        logger.info("Retrieving actual standing of league {} for current season that is started  !", league.getName());
        List<StandingDto> standingDtos = standingClient.getLatestStandForLeague(league.getId().toString()).orElse(Collections.emptyList());
        List<Standing> standings = standingMapper.toStandings(standingDtos);
        return this.saveAndReturn(standings, league, roundNumber);
    }
}
