package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.leagueDefiner.LeagueBettingDefiner;
import boets.bts.backend.service.leagueDefiner.LeagueBettingDefinerFactory;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.league.ILeagueClient;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final TeamService teamService;
    private final LeagueBettingDefinerFactory leagueBettingDefinerFactory;
    private final RoundService roundService;
    private final AdminService adminService;


    public LeagueService(LeagueRepository leagueRepository, ILeagueClient leagueClient, LeagueMapper leagueMapper, TeamService teamService
            , LeagueBettingDefinerFactory leagueBettingDefinerFactory, RoundService roundService, AdminService adminService) {
        this.leagueClient = leagueClient;
        this.leagueRepository = leagueRepository;
        this.leagueMapper = leagueMapper;
        this.teamService = teamService;
        this.leagueBettingDefinerFactory = leagueBettingDefinerFactory;
        this.roundService = roundService;
        this.adminService = adminService;
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
        if(leagues.isEmpty() || leagues.size() < allowedCountries.size()) {
            List<League> leaguesRequestedSeason = this.retrieveAndFilterAndPersistLeagues(currentSeason);
            return leagueMapper.toLeagueDtoList(leaguesRequestedSeason);
        }
        verifyPersistedLeagueIsCurrent();
        return leagueMapper.toLeagueDtoList(leagues);
    }

    private List<League> retrieveAndFilterAndPersistLeagues(int currentSeason) {
        logger.info("No leagues found in database for season start year {}, start retrieving", currentSeason);
        //1 make call to webservice to retrieve all Leagues of current season
        List<League> leagues = retrieveLeaguesFromWebService(currentSeason);
        //2. filter the leagues for the allowed country's
        Map<String, List<League>> leaguesForCountry = leagues.stream()
                .filter(league -> allowedCountries.contains(league.getCountryCode()))
                .collect(Collectors.groupingBy(League::getCountryCode));
        //3. filter the leagues further for the allowed betting sides
        List<League> selectedLeagues = new ArrayList<>();
        for(String countryCode: leaguesForCountry.keySet()) {
            LeagueBettingDefiner leagueBettingDefiner = leagueBettingDefinerFactory.retieveLeagueDefiner(countryCode);
            selectedLeagues.addAll(leagueBettingDefiner.retrieveAllowedBettingLeague(leaguesForCountry.get(countryCode)));
        }
        //4. verify if league has all rounds + teams
        selectedLeagues = selectedLeagues.stream()
                .filter(league -> league.getRounds().isEmpty())
                .peek(roundService::updateLeagueWithRounds)
                .collect(Collectors.toList());
        selectedLeagues = selectedLeagues.stream()
                .filter(league -> league.getTeams().isEmpty())
                .peek(teamService::updateLeagueWithTeams)
                .collect(Collectors.toList());

        //5. persist to database
        leagueRepository.saveAll(selectedLeagues);
        //6. check if league is still current
        verifyPersistedLeagueIsCurrent();
        return selectedLeagues;
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
