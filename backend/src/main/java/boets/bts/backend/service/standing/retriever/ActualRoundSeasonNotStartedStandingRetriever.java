package boets.bts.backend.service.standing.retriever;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.standing.StandingRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultMapper;
import boets.bts.backend.web.round.RoundMapper;
import boets.bts.backend.web.standing.StandingClient;
import boets.bts.backend.web.standing.StandingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * The current season with the actual round but the season is not yet started
 */
@Component
public class ActualRoundSeasonNotStartedStandingRetriever extends AbstractStandingRetriever {

    private static final Logger logger = LoggerFactory.getLogger(ActualRoundSeasonNotStartedStandingRetriever.class);

    public ActualRoundSeasonNotStartedStandingRetriever(LeagueMapper leagueMapper, RoundMapper roundMapper, StandingClient standingClient, AdminService adminService, StandingMapper standingMapper, TeamRepository teamRepository, StandingRepository standingRepository, ResultRepository resultRepository, ResultMapper resultMapper) {
        super(leagueMapper, roundMapper, standingClient, adminService, standingMapper, teamRepository, standingRepository, resultRepository, resultMapper);
    }

    @Override
    public boolean accept(League league, Round currentRound, int roundNumber) {
        LocalDate now = LocalDate.now();
        return !adminService.isHistoricData()
                && currentRound.getRoundNumber() == roundNumber
                && (league.getStartSeason().isAfter(now) || league.getStartSeason().isEqual(now));

    }

    @Override
    public List<Standing> getStanding(League league, Round currentRound, int roundNumber) {
        logger.info("Season of league {} is not yet started.", league.getName());
        return Collections.emptyList();
    }
}
