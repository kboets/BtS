package boets.bts.backend.service.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.round.RoundSpecs;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.round.IRoundClient;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoundService {

    private Logger logger = LoggerFactory.getLogger(RoundService.class);

    private RoundMapper roundMapper;
    private IRoundClient roundClient;
    private RoundRepository roundRepository;
    private LeagueRepository leagueRepository;
    private CurrentRoundHandlerSelector currentRoundHandlerSelector;

    public RoundService(RoundMapper roundMapper, IRoundClient roundClient, RoundRepository roundRepository, LeagueRepository leagueRepository, CurrentRoundHandlerSelector currentRoundHandlerSelector) {
        this.roundMapper = roundMapper;
        this.roundClient = roundClient;
        this.roundRepository = roundRepository;
        this.leagueRepository = leagueRepository;
        this.currentRoundHandlerSelector = currentRoundHandlerSelector;
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
            league.getRounds().addAll(rounds);
            roundRepository.saveAll(rounds);
        }
    }

    public Round getCurrentRoundForLeague(Long leagueId, int season)  {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not found league with id %s", leagueId)));
        Optional<Round> currentPersistedRound = roundRepository.findOne(RoundSpecs.getCurrentRoundForSeason(league, season));
        Optional<CurrentRoundHandler> currentRoundHandlerOptional = currentRoundHandlerSelector.select(currentPersistedRound);
        if(!currentRoundHandlerOptional.isPresent()) {
            logger.warn("Could not retrieve a current round handler for league {} ", league.getName());
            throw new NotFoundException("Could not retrieve a current round handler");
        }
        CurrentRoundHandler currentRoundHandler = currentRoundHandlerOptional.get();
        return currentRoundHandler.save(currentPersistedRound.isPresent()?currentPersistedRound.get():null, league, season);

    }

    public List<Round> getAllRoundsForLeague(Long leagueId) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not found league with id %s", leagueId)));
        return roundRepository.findAll(RoundSpecs.getRoundsByLeagueId(league));
    }

    /**
     * Cron job each half hour
     */
    @Scheduled(cron = "* 1/30 * * * *")
    public void scheduleRound() {
        List<League> leagues = leagueRepository.findAll();
        leagues.forEach(league -> this.getCurrentRoundForLeague(league.getId(), WebUtils.getCurrentSeason()));
    }

}
