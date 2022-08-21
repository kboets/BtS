package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.leagueDefiner.LeagueBettingDefiner;
import boets.bts.backend.service.leagueDefiner.LeagueBettingDefinerFactory;
import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.service.standing.StandingService;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.league.ILeagueClient;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static boets.bts.backend.service.CountryService.allowedCountries;

@Service
@Transactional
public class LeagueService  {

    private Logger logger = LoggerFactory.getLogger(LeagueService.class);

    private final LeagueRepository leagueRepository;
    private final ILeagueClient leagueClient;
    private final LeagueMapper leagueMapper;
    private final StandingService standingService;
    private final LeagueBettingDefinerFactory leagueBettingDefinerFactory;
    private final RoundService roundService;
    private final ResultService resultService;
    private final AdminService adminService;


    public LeagueService(LeagueRepository leagueRepository, ILeagueClient leagueClient, LeagueMapper leagueMapper, StandingService standingService,
                         LeagueBettingDefinerFactory leagueBettingDefinerFactory, RoundService roundService, AdminService adminService,
                         ResultService resultService) {
        this.leagueClient = leagueClient;
        this.leagueRepository = leagueRepository;
        this.leagueMapper = leagueMapper;
        this.standingService = standingService;
        this.leagueBettingDefinerFactory = leagueBettingDefinerFactory;
        this.roundService = roundService;
        this.adminService = adminService;
        this.resultService = resultService;
    }
    //each day at 1 am
    @Scheduled(cron ="0 0 1,13 * * *")
    public void scheduled() {
        this.initCurrentLeagues();
    }
    public Optional<LeagueDto> getLeagueDtoById(Long id) {
        Optional<League> leagueOptional = leagueRepository.findById(id);
        return leagueOptional.map(leagueMapper::toLeagueDto);
    }

    public League getLeagueById(Long id) {
        Optional<League> leagueOptional = leagueRepository.findById(id);
        if(leagueOptional.isPresent()){
            League league = leagueOptional.get();
            //update the current round
            roundService.updateLeagueWithRounds(league);
            return league;
        } else {
            throw new NotFoundException("Could not find league with id " +id);
        }
    }

    public List<LeagueDto> getCurrentLeagues() {
        int currentSeason = adminService.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(currentSeason));
        verifyPersistedLeagueIsCurrent();
        return leagueMapper.toLeagueDtoList(leagues);
    }

    public List<League> initCurrentLeagues() {
        int currentSeason = adminService.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(currentSeason));
        if(leagues.isEmpty() || leagues.size() < allowedCountries.size()) {
            logger.info("The number of leagues {} is lower as requested, retrieving missing leagues ", leagues.size());
            this.retrieveAndFilterAndPersistLeagues(currentSeason, leagues);
            leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(currentSeason));
        }
        return leagues;
    }

    public boolean deleteLeague(Long leagueId) {
        resultService.removeAllResultsForLeague(leagueId);
        standingService.removeAllStandingForLeague(leagueId);
        leagueRepository.deleteById(leagueId);
        return true;
    }

    private void retrieveAndFilterAndPersistLeagues(int currentSeason, List<League> leagues) {
        logger.info("Not all leagues found in database for season start year {}, start retrieving", currentSeason);
        //1 make call to webservice to retrieve all Leagues of current season
        List<League> clientLeagues = retrieveLeaguesFromWebService(currentSeason);
        //2. filter the leagues for the allowed country's
        Map<String, List<League>> leaguesForCountry = clientLeagues.stream()
                .filter(league -> allowedCountries.contains(league.getCountryCode()))
                .collect(Collectors.groupingBy(League::getCountryCode));
        List<League> selectedLeagues = new ArrayList<>();
        //3. filter the leagues further for the allowed betting sides
        for(String countryCode: leaguesForCountry.keySet()) {
            LeagueBettingDefiner leagueBettingDefiner = leagueBettingDefinerFactory.retieveLeagueDefiner(countryCode);
            selectedLeagues.addAll(leagueBettingDefiner.retrieveAllowedBettingLeague(leaguesForCountry.get(countryCode)));
        }
        //4. filter the existing leagues out of the selected
        List<String> leagueNames = leagues.stream().map(League::getName).collect(Collectors.toList());
        selectedLeagues = selectedLeagues.stream().filter(league -> !leagueNames.contains(league.getName())).collect(Collectors.toList());
        //4. persist to database
        if (!selectedLeagues.isEmpty()) {
            leagueRepository.saveAll(selectedLeagues);
        }
        //6. check if league is still current
        verifyPersistedLeagueIsCurrent();
    }

    public List<LeagueDto> updateLeagueAvailableOrSelectable(List<Long> leagueIds, boolean toSelected) {
        List<League> updatedLeagues = leagueIds.stream()
                .map(id -> leagueRepository.findById(id))
                .flatMap(league -> league.isPresent() ? Stream.of(league.get()) : Stream.empty())
                .peek(league -> league.setSelected(toSelected))
                .collect(Collectors.toList());

        List<LeagueDto> leagueDtoList = leagueMapper.toLeagueDtoList(leagueRepository.saveAll(updatedLeagues));
        return leagueDtoList;
    }


    private void verifyPersistedLeagueIsCurrent() {
        List<League> leagues = leagueRepository.findAll();
        LocalDate now = LocalDate.now();
        if(leagues.stream().anyMatch(league -> now.isAfter(league.getEndSeason()) && league.isCurrent())) {
            logger.info(String.format("Updating the current league to not current as the current date %s is after the end date %S", now, leagues.get(0).getEndSeason()));
            List<League> updatedList = leagues.stream()
                    .filter(league -> now.isAfter(league.getEndSeason()) && league.isCurrent())
                    .peek(league -> league.setCurrent(false)).collect(Collectors.toList());
            leagueRepository.saveAll(updatedList);
        }
    }

    private List<League> retrieveLeaguesFromWebService(int currentSeason) {
        List<LeagueDto> leagueDtos = leagueClient.allLeaguesForSeason(currentSeason);
        return leagueMapper.toLeagues(leagueDtos);
    }



}
