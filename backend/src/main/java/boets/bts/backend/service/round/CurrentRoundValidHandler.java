package boets.bts.backend.service.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.round.RoundSpecs;
import boets.bts.backend.web.round.IRoundClient;
import boets.bts.backend.web.round.RoundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Handles if the persisted current round has the actual date of today.
 */
@Component
public class CurrentRoundValidHandler extends AbstractCurrentRoundHandler {

    private Logger logger = LoggerFactory.getLogger(CurrentRoundValidHandler.class);

    public CurrentRoundValidHandler(IRoundClient roundClient, RoundMapper roundMapper) {
        super(roundClient, roundMapper);
    }

    @Override
    public boolean accept(Optional<Round> roundOptional) {
        if(!roundOptional.isPresent()) {
            return false;
        }
        Round currentPersisted = roundOptional.get();
        return currentPersisted.getCurrentDate().equals(LocalDate.now());
    }

    @Override
    public Round save(Round round, League league, int season)  {
        logger.info("current round {} with current date {} is still valid", round.getRound(), round.getCurrentDate());
        return round;
    }


}
