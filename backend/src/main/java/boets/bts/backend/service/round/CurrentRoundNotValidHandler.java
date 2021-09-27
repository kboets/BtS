package boets.bts.backend.service.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.round.RoundSpecs;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.round.IRoundClient;
import boets.bts.backend.web.round.RoundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Handles if the persisted current round has not the actual date of today.
 */
@Component
public class CurrentRoundNotValidHandler extends AbstractCurrentRoundHandler {

    private Logger logger = LoggerFactory.getLogger(CurrentRoundNotValidHandler.class);
    private RoundRepository roundRepository;

    public CurrentRoundNotValidHandler(IRoundClient roundClient, RoundMapper roundMapper, RoundRepository roundRepository) {
        super(roundClient, roundMapper);
        this.roundRepository = roundRepository;
    }

    @Override
    public boolean accept(Optional<Round> roundOptional) {
        if(roundOptional.isEmpty()) {
            return false;
        }
        Round currentPersisted = roundOptional.get();
        return !currentPersisted.getCurrentDate().equals(LocalDate.now());
    }

    @Override
    public Round save(Round round, League league, int season)  {
        logger.info("current round {} with current date {} is outdated", round.getRound(), round.getCurrentDate());
        Round currentRoundUpdated = this.getCurrentClientRound(round.getLeague().getId(), round.getSeason());
        Round verifiedRound = verifyRetrievedRound(currentRoundUpdated);
        if(round.getRound().equals(verifiedRound.getRound())) {
            //logger.info("current round {} is still current, update date", round.getRound());
            round.setCurrentDate(LocalDate.now());
        } else {
            //logger.info("current round {} is no longer current, update with latest", round.getRound());
            Round roundToBeUpdated = roundRepository.findOne(RoundSpecs.getRoundByNameAndLeague(league, verifiedRound.getRound())).orElseThrow(() -> new NotFoundException(String.format("Could not find round in database with name %s and league %s", currentRoundUpdated.getRound(), league.getName())));
            roundToBeUpdated.setCurrentDate(LocalDate.now());
            roundToBeUpdated.setCurrent(true);
            roundRepository.save(roundToBeUpdated);
            round.setCurrent(false);
        }
        return roundRepository.save(round);
    }
}
