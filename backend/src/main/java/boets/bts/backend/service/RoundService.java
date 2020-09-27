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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Round getCurrentRoundForLeagueAndSeason(Long leagueId, int season) {
        this.updateCurrentRoundRorLeagueAndSeason(leagueId,season);
        return roundRepository.findOne(RoundSpecs.getCurrentRound()).orElseThrow(() -> new RuntimeException("Could not find current round for league with id "+leagueId));
    }


    public void updateCurrentRoundRorLeagueAndSeason(Long leagueId, int season) {
        Optional<RoundDto> currentRound = roundClient.getCurrentRoundForLeagueAndSeason(season, leagueId);
        if(!currentRound.isPresent()) {
            throw new RuntimeException("Could not retrieve current round for league id " + leagueId);
        }
        List<Round> rounds = roundRepository.findAll(RoundSpecs.getRoundsByLeagueId(leagueId));
        List<Round> roundsToBeChecked = rounds.stream().peek(round -> round.setCurrent(false)).collect(Collectors.toList());
        for(Round round : roundsToBeChecked) {
            if(round.getRound().equals(currentRound.get().getRound())){
                round.setCurrent(true);
                round.setCurrentDate(LocalDate.now());
            }
        }
        roundRepository.saveAll(roundsToBeChecked);
    }

    public Round getRoundByName(String name) {
        if(roundRepository.findOne(RoundSpecs.getRoundByName(name)).isPresent()) {
            return roundRepository.findOne(RoundSpecs.getRoundByName(name)).get();
        } else {
            throw new NotFoundException("Could not find Round with name " +name);
        }
    }
}
