package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.web.dto.TeamDto;
import boets.bts.backend.web.dto.TeamMapper;
import boets.bts.backend.web.team.TeamClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private Logger logger = LoggerFactory.getLogger(TeamService.class);

    private TeamClient teamClient;
    private TeamMapper teamMapper;

    public TeamService(TeamClient teamClient, TeamMapper teamMapper) {
        this.teamClient = teamClient;
        this.teamMapper = teamMapper;
    }

    /**
     * Retrieves the teams of a league when not yet present.
     * @param league - the League to be checked
     * @return League - the updated League
     */
    public League updateLeagueWithTeams(League league) {
        if(league.getTeams().isEmpty()) {
            Optional<List<TeamDto>> optionalTeamDtos = teamClient.retrieveTeamsOfLeague(league.getId());
            if(optionalTeamDtos.isPresent()) {
                logger.info("Could retrieve {} teams for league {} ", optionalTeamDtos.get().size(), league.getName());
                league.setTeams(teamMapper.toTeams(optionalTeamDtos.get()));
            }
        }
        return league;
    }
}
