package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.round.RoundSpecs;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.round.RoundClient;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
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
    private RoundRepository roundRepository;

    public RoundService(RoundMapper roundMapper, RoundClient roundClient, RoundRepository roundRepository) {
        this.roundMapper = roundMapper;
        this.roundClient = roundClient;
        this.roundRepository = roundRepository;
    }

    /**
     * Retrieves and saves the rounds of a league when not yet present.
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
                roundRepository.saveAll(rounds);
               //roundRepository.flush();
                league.setRounds(rounds);
            }
        }
    }

    public Round getCurrentRound(Long leagueId, int season) {
        RoundDto latestRound = roundClient.getCurrentRoundForLeagueAndSeason(season, leagueId).orElseThrow(RuntimeException::new);
        return roundMapper.toRound(latestRound);
    }

    public Round getRoundByName(String name) {
        if(roundRepository.findOne(RoundSpecs.getRoundByName(name)).isPresent()) {
            return roundRepository.findOne(RoundSpecs.getRoundByName(name)).get();
        } else {
            throw new NotFoundException("Could not find Round with name " +name);
        }
    }
}
