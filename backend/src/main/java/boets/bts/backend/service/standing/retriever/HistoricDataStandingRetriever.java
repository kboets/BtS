package boets.bts.backend.service.standing.retriever;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.standing.StandingRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.service.admin.AdminService;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultMapper;
import boets.bts.backend.web.round.RoundMapper;
import boets.bts.backend.web.standing.StandingClient;
import boets.bts.backend.web.standing.StandingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HistoricDataStandingRetriever extends AbstractStandingRetriever {

    private static final Logger logger = LoggerFactory.getLogger(HistoricDataStandingRetriever.class);

    public HistoricDataStandingRetriever(LeagueMapper leagueMapper, RoundMapper roundMapper, StandingClient standingClient, AdminService adminService, StandingMapper standingMapper, TeamRepository teamRepository, StandingRepository standingRepository, ResultRepository resultRepository, ResultMapper resultMapper) {
        super(leagueMapper, roundMapper, standingClient, adminService, standingMapper, teamRepository, standingRepository, resultRepository, resultMapper);
    }

    @Override
    public boolean accept(League league, Round currentRound, int roundNumber) {
        return adminService.isHistoricData();
    }

    @Override
    public List<Standing> getStanding(League league, Round currentRound, int roundNumber) {
        logger.info("Recalculating or retrieving the standings for round {} of league {} for historic data", roundNumber, league.getName());
        List<Standing> standingsForRound = new ArrayList<>();
        for (int i=1; i<=currentRound.getRoundNumber();i++) {
            standingsForRound = super.retrieveOrCalculateMissingStandings(i, league);
        }
        return standingsForRound;
    }
}
