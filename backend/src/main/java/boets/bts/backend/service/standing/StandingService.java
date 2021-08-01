package boets.bts.backend.service.standing;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.standing.StandingRepository;
import boets.bts.backend.repository.standing.StandingSpecs;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.repository.team.TeamSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.standing.StandingClient;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.standing.StandingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StandingService {

    private Logger logger = LoggerFactory.getLogger(StandingService.class);

    private StandingRepository standingRepository;
    private ResultRepository resultRepository;
    private StandingMapper standingMapper;
    private StandingClient standingClient;
    private LeagueRepository leagueRepository;
    private TeamRepository teamRepository;
    private RoundService roundService;
    private AdminService adminService;
    private StandingCalculator standingCalculator;

    public StandingService(StandingRepository standingRepository, StandingMapper standingMapper, StandingClient standingClient, LeagueRepository leagueRepository,
                           TeamRepository teamRepository, RoundService roundService, AdminService adminService, ResultRepository resultRepository) {
        this.standingRepository = standingRepository;
        this.standingMapper = standingMapper;
        this.standingClient = standingClient;
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.roundService = roundService;
        this.adminService = adminService;
        this.resultRepository = resultRepository;
        this.standingCalculator = new StandingCalculator();
    }

    public List<StandingDto> getCurrentStandingForLeague(Long leagueId) {
        Round currentRound = roundService.getCurrentRoundForLeague(leagueId, adminService.getCurrentSeason());

        List<Standing> standingsForLeagueByRound = this.getStandingsForLeagueByRound(leagueId, adminService.getCurrentSeason(), currentRound.getRoundNumber());
        return standingMapper.toStandingDtos(standingsForLeagueByRound);
    }

    public List<Standing> getStandingsForLeagueByRound(Long leagueId, int season, int roundNumber) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not find League with id %s", leagueId)));
        List<Standing> standingsForRound = standingRepository.findAll(StandingSpecs.forLeague(league).and(StandingSpecs.forRound(roundNumber)));
        if (!standingsForRound.isEmpty()) {
            return standingsForRound;
        }
        Round currentRound = roundService.getCurrentRoundForLeague(leagueId, adminService.getCurrentSeason());
        logger.info("Could not find standing of round {} for league {} of season {}", roundNumber, league.getName(), season);
        if (adminService.getCurrentSeason() == season
                && currentRound.getRoundNumber() == roundNumber && LocalDate.now().isAfter(league.getStartSeason())) {
            logger.info("Retrieving current standing for current season !");
            List<StandingDto> standingDtos = standingClient.getLatestStandForLeague(leagueId.toString()).orElseGet(() -> Collections.emptyList());
            List<Standing> standings = standingMapper.toStandings(standingDtos);
            //Round currentRound = roundService.getCurrentRoundForLeague(leagueId, season);
            List<Standing> expandedStandings = standings.stream()
                    .peek(standing -> standing.setLeague(leagueRepository.findById(leagueId)
                            .orElseThrow(() -> new NotFoundException(String.format("Could not find league with id %s", standing.getLeague().getId())))))
                    .peek(standing -> standing.setTeam(teamRepository.findOne(TeamSpecs.getTeamByTeamId(standing.getTeam().getTeamId(), league))
                            .orElseThrow(() -> new NotFoundException(String.format("Could not find team with id %s", standing.getTeam().getTeamId())))))
                    .peek(standing -> {
                        standing.setRoundNumber(roundNumber);
                        standing.setSeason(season);
                    })
                    .collect(Collectors.toList());
            //save to db
            return standingRepository.saveAll(expandedStandings);
        } else {
            logger.info("Recalculating standing for previous round !");
            // calculate the historic standing, get all results for league
            List<Result> results = resultRepository.findAll(ResultSpecs.allFinishedResultsCurrentRoundIncluded(league, roundNumber));
            List<Team> teams = league.getTeams();
            //TODO
            return Collections.emptyList();
        }
    }

    // each 30 minutes
    @Scheduled(cron = "30 5-59/30 * * * ?")
    public void scheduleStandings() throws Exception {
        logger.info("Scheduler triggered to update standings ..");
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
