package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.standing.StandingRepository;
import boets.bts.backend.repository.standing.StandingSpecs;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.repository.team.TeamSpecs;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.standing.StandingClient;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.standing.StandingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandingService {

    private Logger logger = LoggerFactory.getLogger(StandingService.class);

    private StandingRepository standingRepository;
    private StandingMapper standingMapper;
    private StandingClient standingClient;
    private LeagueRepository leagueRepository;
    private TeamRepository teamRepository;
    private RoundService roundService;
    private AdminService adminService;

    public StandingService(StandingRepository standingRepository, StandingMapper standingMapper, StandingClient standingClient, LeagueRepository leagueRepository,
                           TeamRepository teamRepository, RoundService roundService, AdminService adminService) {
        this.standingRepository = standingRepository;
        this.standingMapper = standingMapper;
        this.standingClient = standingClient;
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.roundService = roundService;
        this.adminService = adminService;
    }

    public List<StandingDto> getStandingsForLeague(Long leagueId) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not find League with id %s",leagueId)));
        List<Standing> standings = standingRepository.findAll(StandingSpecs.getStandingsByLeague(league));
        if(standings.isEmpty()) {
            List<StandingDto> standingDtos = standingClient.getLatestStandForLeague(leagueId.toString()).orElseGet(()-> Collections.emptyList());
            standings = standingMapper.toStandings(standingDtos);
            List<Standing> expandedStandings = standings.stream()
                    .peek(standing -> standing.setLeague(leagueRepository.findById(leagueId)
                            .orElseThrow(() -> new NotFoundException(String.format("Could not find league with id %s", standing.getLeague().getId())))))
                    .peek(standing -> standing.setTeam(teamRepository.findOne(TeamSpecs.getTeamByTeamId(standing.getTeam().getTeamId(), league))
                            .orElseThrow(() -> new NotFoundException(String.format("Could not find team with id %s", standing.getTeam().getTeamId())))))
                    .collect(Collectors.toList());
            return standingMapper.toStandingDtos(standingRepository.saveAll(expandedStandings));
        }
        Round currentRound = roundService.getCurrentRoundForLeague(leagueId, adminService.getCurrentSeason());
        if(!currentRound.getRound().equals(standings.get(0).getRound())) {
            List<StandingDto> standingDtos = standingClient.getLatestStandForLeague(leagueId.toString()).orElseGet(()-> Collections.emptyList());
            List<Standing> clientStandings = standingMapper.toStandings(standingDtos);
            List<Standing> toBeUpdatedStandings = new ArrayList<>();
            for(Standing persistedStanding : standings) {
                for(Standing updatedStanding : clientStandings) {
                    if(persistedStanding.getTeam().getTeamId().equals(updatedStanding.getTeam().getTeamId())) {
                        toBeUpdatedStandings.add(mergeStandings(persistedStanding, updatedStanding, currentRound));
                    }
                }
            }
            return standingMapper.toStandingDtos(standingRepository.saveAll(toBeUpdatedStandings));
        }
        return standingMapper.toStandingDtos(standings);
    }

    private Standing mergeStandings(Standing persistedStanding, Standing updatedStanding, Round round) {
        persistedStanding.setAwaySubStanding(updatedStanding.getAwaySubStanding());
        persistedStanding.setLastUpdate(updatedStanding.getLastUpdate());
        persistedStanding.setAllSubStanding(updatedStanding.getAllSubStanding());
        persistedStanding.setHomeSubStanding(updatedStanding.getHomeSubStanding());
        persistedStanding.setRank(updatedStanding.getRank());
        persistedStanding.setPoints(updatedStanding.getPoints());
        persistedStanding.setRound(round.getRound());
        return persistedStanding;
    }
}
