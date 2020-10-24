package boets.bts.backend.service.round;

import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.round.RoundSpecs;
import boets.bts.backend.web.round.RoundClient;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class ExistingCurrentRoundHandler implements CurrentRoundHandler {

    private Logger logger = LoggerFactory.getLogger(ExistingCurrentRoundHandler.class);
    private RoundClient roundClient;
    private RoundMapper roundMapper;
    private RoundRepository roundRepository;

    public ExistingCurrentRoundHandler(RoundClient roundClient, RoundMapper roundMapper, RoundRepository roundRepository) {
        this.roundClient = roundClient;
        this.roundMapper = roundMapper;
        this.roundRepository = roundRepository;
    }

    @Override
    public Round save(Round round, long leagueId, int season) throws Exception {
        //check if updated date is today
        if(!round.getCurrentDate().equals(LocalDate.now())) {
            logger.info("current round {} with current date {} is outdated", leagueId, round.getCurrentDate());
            Round currentRoundUpdated = this.getCurrentRound(round.getLeague().getId(), round.getSeason());
            if(round.getRound().equals(currentRoundUpdated.getRound())) {
                logger.info("current round {} is still current, update date", round.getRound());
                round.setCurrentDate(LocalDate.now());
            } else {
                logger.info("current round {} is no longer current, update with latest", round.getRound());
                Round roundToBeUpdated = roundRepository.findOne(RoundSpecs.getRoundByNameAndLeague(leagueId, currentRoundUpdated.getRound())).orElseThrow(() -> new Exception(String.format("Could not find round in db with name", currentRoundUpdated.getRound())));
                roundToBeUpdated.setCurrentDate(LocalDate.now());
                roundToBeUpdated.setCurrent(true);
                roundRepository.save(roundToBeUpdated);
                round.setCurrent(false);
            }
            return roundRepository.save(round);
        }
        logger.info("current round {} with current date {} is still valid", round.getRound(), round.getCurrentDate());
        return round;

    }

    private Round getCurrentRound(long leagueId, int season)  throws Exception {
        Optional<RoundDto> currentRoundOptional = roundClient.getCurrentRoundForLeagueAndSeason(leagueId, season);
        RoundDto roundDto = currentRoundOptional.orElseThrow(() -> new Exception(String.format("Could not find current round for league %s from season %s", leagueId, season)));
        return roundMapper.toRound(roundDto);
    }
}
