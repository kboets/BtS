package boets.bts.backend.service.standing;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.repository.standing.StandingRepository;
import boets.bts.backend.repository.standing.StandingSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.service.standing.retriever.StandingRetriever;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.standing.StandingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StandingService {

    private static final Logger logger = LoggerFactory.getLogger(StandingService.class);

    private final StandingRepository standingRepository;
    private final StandingMapper standingMapper;
    private final LeagueRepository leagueRepository;
    private final RoundService roundService;
    private final AdminService adminService;
    private final StandingRetrieverSelector standingRetrieverSelector;

    public StandingService(StandingRepository standingRepository, StandingMapper standingMapper, LeagueRepository leagueRepository, RoundService roundService, AdminService adminService, StandingRetrieverSelector standingRetrieverSelector) {
        this.standingRepository = standingRepository;
        this.standingMapper = standingMapper;
        this.leagueRepository = leagueRepository;
        this.roundService = roundService;
        this.adminService = adminService;
        this.standingRetrieverSelector = standingRetrieverSelector;
    }

    public List<StandingDto> getCurrentStandingForLeague(Long leagueId) {
        Round currentRound = roundService.getCurrentRoundForLeague(leagueId, adminService.getCurrentSeason());
        List<Standing> standingsForLeagueByRound;
        if(WebUtils.isWeekend()) {
            int currentRoundNumber = currentRound.getRoundNumber()-1;
            logger.info("Current round {} for league {} while getting standings", currentRoundNumber, leagueId);
            standingsForLeagueByRound = this.getStandingsForLeagueByRound(leagueId, adminService.getCurrentSeason(), currentRoundNumber);
        } else {
            logger.info("Current round {} for league {} while getting standings", currentRound.getRoundNumber(), leagueId);
            standingsForLeagueByRound = this.getStandingsForLeagueByRound(leagueId, adminService.getCurrentSeason(), currentRound.getRoundNumber());
        }
        logger.info("Retrieved standing {} for league {}", standingsForLeagueByRound.isEmpty()?"NOT FOUND":standingsForLeagueByRound.get(0).getAllSubStanding().getMatchPlayed(), leagueId);
        return standingMapper.toStandingDtos(standingsForLeagueByRound);
    }

    public List<Standing> getStandingsForLeagueByRound(Long leagueId, int season, int roundNumber) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not find League with id %s", leagueId)));
        List<Standing> standingsForRound = standingRepository.findAll(StandingSpecs.forLeague(league).and(StandingSpecs.forRound(roundNumber)));
        if (!standingsForRound.isEmpty()) {
            return standingsForRound;
        }
        Round currentRound = roundService.getCurrentRoundForLeague(leagueId, adminService.getCurrentSeason());
        Optional<StandingRetriever> standingOptionalRetriever = standingRetrieverSelector.select(league, currentRound, roundNumber);
        if(standingOptionalRetriever.isPresent()) {
            return standingOptionalRetriever.get().getStanding(league, currentRound, roundNumber);
        }
        logger.info("Could not find standing of round {} for league {} of season {}", roundNumber, league.getName(), season);
        return Collections.emptyList();
    }


    // each 30 minutes
    @Scheduled(cron = "* 7-59/15 * * * ?")
    public void scheduleStandings() {
        if(!adminService.isTodayExecuted(AdminKeys.CRON_STANDINGS) && !adminService.isHistoricData()) {
            logger.info("Running cron job to update standings ..");
            List<Long> leagueIds = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason())).stream().map(League::getId).collect(Collectors.toList());
            for (Long leagueId : leagueIds) {
                this.getCurrentStandingForLeague(leagueId);
            }
            adminService.executeAdmin(AdminKeys.CRON_STANDINGS, "OK");
        }
    }
}
