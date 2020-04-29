package boets.bts.backend.web.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.web.team.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoundService {

    private Logger logger = LoggerFactory.getLogger(RoundService.class);

    private RoundMapper roundMapper;
    private RoundClient roundClient;

    public RoundService(RoundMapper roundMapper, RoundClient roundClient) {
        this.roundMapper = roundMapper;
        this.roundClient = roundClient;
    }

    /**
     * Retrieves the rounds of a league when not yet present.
     * @param league - the League to be checked
     * @return League - the updated League
     */
    public void updateLeagueWithRounds(League league) {
        if(league.getRounds().isEmpty()) {
            Optional<List<RoundDto>> optionalRoundDtos = roundClient.getAllRoundsForLeagueAndSeason(league.getSeason(), league.getId());
            if(optionalRoundDtos.isPresent()) {
                logger.info("Could retrieve {} rounds for league {} ", optionalRoundDtos.get().size(), league.getName());
                List<Round> rounds = roundMapper.toRounds(optionalRoundDtos.get());
                rounds.forEach(round -> round.setLeague(league));
                league.setRounds(roundMapper.toRounds(optionalRoundDtos.get()));
            }
        }
    }
}
