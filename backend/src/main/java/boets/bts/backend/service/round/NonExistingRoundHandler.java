package boets.bts.backend.service.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.round.RoundSpecs;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.round.IRoundClient;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class NonExistingRoundHandler extends AbstractCurrentRoundHandler {

    private Logger logger = LoggerFactory.getLogger(CurrentRoundValidHandler.class);
    private RoundRepository roundRepository;

    public NonExistingRoundHandler(IRoundClient roundClient, RoundMapper roundMapper, RoundRepository roundRepository) {
        super(roundClient, roundMapper);
        this.roundRepository = roundRepository;
    }

    @Override
    public boolean accept(Optional<Round> roundOptional) {
        return roundOptional.isEmpty();
    }

    @Override
    public Round save(Round round, League league, int season)  {
        logger.info("No current round found for league {} and season {} ", league.getName(), season);
        Round clientRound = getCurrentClientRound(league.getId(), season);
        Round verifiedRound = verifyRetrievedRound(clientRound);
        //now get persisted round
        Optional<Round> persistedOptionalRound = roundRepository.findOne(RoundSpecs.getRoundByNameAndLeague(league, verifiedRound.getRound()));
        if(!persistedOptionalRound.isPresent()) {
            logger.warn("No round found for leagueId {} and name {} ", league.getName(), verifiedRound.getRound());
            throw new NotFoundException(String.format("Could not find round in database for league with id %s with name %s", league.getName(), verifiedRound.getRound()));
        }
        Round persisted = persistedOptionalRound.get();
        persisted.setCurrentDate(LocalDate.now());
        persisted.setCurrent(true);
        return roundRepository.save(persisted);
    }

}
