package boets.bts.backend.service.standing;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.repository.league.LeagueRepository;
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
import org.apache.commons.lang3.StringUtils;
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

    public List<StandingDto> getStandingsForLeagueByRound(Long leagueId, int season, int roundNumber) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not find League with id %s",leagueId)));
        List<Standing> standings = standingRepository.findAll(StandingSpecs.getStandingsByLeagueAndRound(league, roundNumber, season));
        if(standings.isEmpty()) {
            // calculate the historic data (or retrieve it via
        }
        return standingMapper.toStandingDtos(standings);
    }

    public List<StandingDto> getCurrentStandingsForLeague(Long leagueId) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not find League with id %s",leagueId)));
        int currentSeason = adminService.getCurrentSeason();
        Round currentRound = roundService.getCurrentRoundForLeague(leagueId, currentSeason);
        List<Standing> standings = standingRepository.findAll(StandingSpecs.getStandingsByLeague(league));
        if(standings.isEmpty() || !currentRound.getRound().equals(standings.get(0).getRound())) {
            List<StandingDto> standingDtos = standingClient.getLatestStandForLeague(leagueId.toString()).orElseGet(()-> Collections.emptyList());
            standings = standingMapper.toStandings(standingDtos);
            List<Standing> expandedStandings = standings.stream()
                    .peek(standing -> standing.setLeague(leagueRepository.findById(leagueId)
                            .orElseThrow(() -> new NotFoundException(String.format("Could not find league with id %s", standing.getLeague().getId())))))
                    .peek(standing -> standing.setTeam(teamRepository.findOne(TeamSpecs.getTeamByTeamId(standing.getTeam().getTeamId(), league))
                            .orElseThrow(() -> new NotFoundException(String.format("Could not find team with id %s", standing.getTeam().getTeamId())))))
                    .peek(standing -> {
                        standing.setRoundNumber(currentRound.getRoundNumber());
                        standing.setRound(currentRound.getRound());
                        standing.setSeason(currentSeason);
                    })
                    .collect(Collectors.toList());
            return standingMapper.toStandingDtos(standingRepository.saveAll(expandedStandings));
        } else {
            updateStandingWithRoundNumber(standings);
        }

        return standingMapper.toStandingDtos(standings);
    }

    private void updateStandingWithRoundNumber(List<Standing> standings) {
        List<Standing> updatedStandings = new ArrayList<>();
        for(Standing standing: standings) {
            if(standing.getRoundNumber() != null) {
                break;
            }
            String round = standing.getRound();
            if(StringUtils.startsWith(round, "Regular")) {
                String roundNumber = StringUtils.substringAfterLast(round, "_");
                standing.setRoundNumber(Integer.parseInt(roundNumber));
                updatedStandings.add(standing);
            }
        }
        if(!updatedStandings.isEmpty()) {
            standingRepository.saveAll(updatedStandings);
        }
    }

}
