package boets.bts.backend.service.round;

import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.web.round.RoundClient;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NonExistingRoundHandler implements CurrentRoundHandler {

    private Logger logger = LoggerFactory.getLogger(ExistingCurrentRoundHandler.class);
    private RoundClient roundClient;
    private RoundMapper roundMapper;
    private RoundRepository roundRepository;

    public NonExistingRoundHandler(RoundClient roundClient, RoundMapper roundMapper, RoundRepository roundRepository) {
        this.roundClient = roundClient;
        this.roundMapper = roundMapper;
        this.roundRepository = roundRepository;
    }

    @Override
    public Round save(Round round, long leagueId, int season) throws Exception {
        logger.info("No current round found for leagueId {} and season {} ", leagueId, season);
        Round updatedRound = getCurrentRound(leagueId, season);
        return roundRepository.save(updatedRound);
    }

    private Round getCurrentRound(long leagueId, int season) throws Exception {
        Optional<RoundDto> currentRoundOptional = roundClient.getCurrentRoundForLeagueAndSeason(leagueId, season);
        RoundDto roundDto = currentRoundOptional.orElseThrow(() -> new Exception(String.format("Could not find current round for league %s from season %s", leagueId, season)));
        return roundMapper.toRound(roundDto);
    }
}
