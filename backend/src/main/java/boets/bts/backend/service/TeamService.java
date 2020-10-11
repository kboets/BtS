package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Team;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.team.TeamDto;
import boets.bts.backend.web.team.TeamMapper;
import boets.bts.backend.web.team.TeamClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final TeamClient teamClient;
    private final TeamMapper teamMapper;
    private final TeamRepository teamRepository;

    public TeamService(TeamClient teamClient, TeamMapper teamMapper, TeamRepository teamRepository) {
        this.teamClient = teamClient;
        this.teamMapper = teamMapper;
        this.teamRepository = teamRepository;
    }

    /**
     * Retrieves the teams of a league when not yet present.
     * @param league - the League to be checked
     * @return League - the updated League
     */
    public void updateLeagueWithTeams(League league) {
        Optional<List<TeamDto>> optionalTeamDtos = teamClient.retrieveTeamsOfLeague(league.getId());
        if(optionalTeamDtos.isPresent()) {
            logger.info("Could retrieve {} teams for league {} ", optionalTeamDtos.get().size(), league.getName());
            List<Team> teams = teamMapper.toTeams(optionalTeamDtos.get());
            teams.forEach(team -> team.setLeague(league));
            league.getTeams().addAll(teams);
        }
    }

    public Team getTeamById(Long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if(teamOptional.isPresent()) {
            return teamOptional.get();
        } else {
            throw new NotFoundException("Could not find team with id " +teamId);
        }

    }
}
