package boets.bts.backend.service.round;

import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.round.RoundSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.round.IRoundClient;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class RoundService {

    private final static Logger logger = LoggerFactory.getLogger(RoundService.class);

    private final RoundMapper roundMapper;
    private final IRoundClient roundClient;
    private final RoundRepository roundRepository;
    private final LeagueRepository leagueRepository;
    private final CurrentRoundHandlerSelector currentRoundHandlerSelector;
    private final AdminService adminService;

    public RoundService(RoundMapper roundMapper, IRoundClient roundClient, RoundRepository roundRepository, LeagueRepository leagueRepository, CurrentRoundHandlerSelector currentRoundHandlerSelector,
                        AdminService adminService) {
        this.roundMapper = roundMapper;
        this.roundClient = roundClient;
        this.roundRepository = roundRepository;
        this.leagueRepository = leagueRepository;
        this.currentRoundHandlerSelector = currentRoundHandlerSelector;
        this.adminService = adminService;
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
        if(leagueId == null) {
            logger.warn("The league id is null");
            return null;
        }
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not found league with id %s", leagueId)));
        if(league.getRounds().isEmpty()) {
            this.updateLeagueWithRounds(league);
        } else {
            Set<Round> allRoundsForLeague = league.getRounds();
            //verify if round number is already calculated
            if(allRoundsForLeague.iterator().next().getRoundNumber() == null) {
                this.updateRoundWithRoundNumber(allRoundsForLeague);
            }
        }

        Optional<Round> currentPersistedRound = roundRepository.findOne(RoundSpecs.getCurrentRoundForSeason(league, season));
        Optional<CurrentRoundHandler> currentRoundHandlerOptional = currentRoundHandlerSelector.select(currentPersistedRound);
        if(!currentRoundHandlerOptional.isPresent()) {
            logger.warn("Could not retrieve a current round handler for league {} ", league.getName());
            throw new NotFoundException("Could not retrieve a current round handler");
        }
        CurrentRoundHandler currentRoundHandler = currentRoundHandlerOptional.get();
        return currentRoundHandler.save(currentPersistedRound.orElse(null), league, season);
    }

    public Round getNextRound(Long leagueId) {
        Round currentRound = this.getCurrentRoundForLeague(leagueId, adminService.getCurrentSeason());
        List<Round> getAllRounds = this.getAllRoundsForLeague(leagueId);
        return getAllRounds.stream().filter(round -> round.getRoundNumber() == currentRound.getRoundNumber()+1).findFirst().orElse(currentRound);
    }

    public Round getLastRound(Long leagueId) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not found league with id %s", leagueId)));
        List<Round> rounds4League = roundRepository.findAll(RoundSpecs.getRoundsByLeagueId(league));
        rounds4League.sort(Comparator.comparing(Round::getRoundNumber));
        return rounds4League.get(rounds4League.size()-1);
    }

    public List<Round> getAllRoundsForLeague(Long leagueId) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not found league with id %s", leagueId)));
        return roundRepository.findAll(RoundSpecs.getRoundsByLeagueId(league));
    }

    public void setCurrentRoundForHistoricData(Long leagueId, int season) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not found league with id %s", leagueId)));
        Set<Round> rounds = league.getRounds();
        if(rounds.isEmpty()) {
            this.updateLeagueWithRounds(league);
            rounds = league.getRounds();
        } else {
            Set<Round> allRoundsForLeague = league.getRounds();
            if(allRoundsForLeague.iterator().next().getRoundNumber() == null) {
                this.updateRoundWithRoundNumber(allRoundsForLeague);
            }
        }
        Optional<Round> currentPersistedRound = roundRepository.findOne(RoundSpecs.getCurrentRoundForSeason(league, season));
        Round round;
        if(!currentPersistedRound.isPresent()) {
            //take random round
            int random = rounds.size() - 10;
            List<Round> rounds1 = new ArrayList<>(rounds);
            round = rounds1.get(random);
            round.setCurrentDate(LocalDate.now());
            round.setCurrent(true);
        } else {
            round = currentPersistedRound.get();
            round.setCurrentDate(LocalDate.now());
        }
        roundRepository.save(round);
    }

    public Round updateCurrentRound(Long roundId, Long leagueId) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not found league with id %s", leagueId)));
        Round newCurrentRound = roundRepository.findById(roundId).orElseThrow(() -> new NotFoundException(String.format("Could not found round with id %s", roundId)));
        Optional<Round> currentPersistedRound = roundRepository.findOne(RoundSpecs.getCurrentRoundForSeason(league, league.getSeason()));
        if(currentPersistedRound.isPresent()) {
            Round previousCurrent = currentPersistedRound.get();
            previousCurrent.setCurrent(false);
            roundRepository.save(previousCurrent);
        }
        newCurrentRound.setCurrent(true);
        newCurrentRound.setCurrentDate(LocalDate.now());
        return roundRepository.save(newCurrentRound);
    }

    /**
     * Cron job each 15 minutes
     */
    @Scheduled(cron = "* 1-59/15 * * * ?")
    public void scheduleRound() {
        logger.info("Scheduler triggered to update rounds ..");
        if(!adminService.isTodayExecuted(AdminKeys.CRON_ROUNDS) && !adminService.isHistoricData()) {
            List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason()));
            logger.info("Scheduler started for non historic data");
            leagues.forEach(league -> this.getCurrentRoundForLeague(league.getId(), adminService.getCurrentSeason()));
            adminService.executeAdmin(AdminKeys.CRON_ROUNDS, "OK");
        } else if(!adminService.isTodayExecuted(AdminKeys.CRON_RESULTS) && adminService.isHistoricData()) {
            List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason()));
            logger.info("Scheduler started for historic data");
            adminService.executeAdmin(AdminKeys.CRON_ROUNDS, "OK");
            leagues.forEach(league -> this.setCurrentRoundForHistoricData(league.getId(), adminService.getCurrentSeason()));
        }
    }

    private void updateRoundWithRoundNumber(Set<Round> roundSet) {
        List<Round> rounds = new ArrayList<>(roundSet);
        rounds.sort(Comparator.comparing(Round::getId));
        int index = 1;
        for(Round round:rounds) {
            round.setRoundNumber(index);
            index++;
        }
        roundRepository.saveAll(rounds);
    }



}
