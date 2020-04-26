package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.leagueDefiner.LeagueBettingDefiner;
import boets.bts.backend.service.leagueDefiner.LeagueBettingDefinerFactory;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.league.ILeagueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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


    public LeagueService(LeagueRepository leagueRepository, ILeagueClient leagueClient, LeagueMapper leagueMapper, TeamService teamService
            , LeagueBettingDefinerFactory leagueBettingDefinerFactory) {
        this.leagueClient = leagueClient;
        this.leagueRepository = leagueRepository;
        this.leagueMapper = leagueMapper;
        this.teamService = teamService;
        this.leagueBettingDefinerFactory = leagueBettingDefinerFactory;
    }

    public Optional<LeagueDto> getLeagueById(Long leagueId) {
        Optional<League> leagueOptional = leagueRepository.findById(leagueId);
        if(leagueOptional.isPresent()) {
            League league = teamService.updateLeagueWithTeams(leagueOptional.get());
            return Optional.ofNullable(leagueMapper.toLeagueDto(league));
        }
        return Optional.empty();
    }

    public List<LeagueDto> getLeaguesCurrentSeason(boolean isSelected) {
        int currentSeason = WebUtils.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(currentSeason));
        if(leagues.isEmpty()) {
            //make call to webservice
            logger.info(String.format("no leagues found for season start year %s", currentSeason));
            leagues = retrieveLeaguesFromWebService(currentSeason);
            persistLeagues(leagues);
        }
        Map<String, List<League>> leaguesForCountry = leagues.stream()
                .filter(league -> allowedCountries.contains(league.getCountryCode()))
                .filter(league -> league.isSelected() == isSelected)
                .collect(Collectors.groupingBy(League::getCountryCode));

        List<League> selectedLeagues = new ArrayList<>();
        //select only the leagues available on betting sites of the country
        for(String countryCode: leaguesForCountry.keySet()) {
            LeagueBettingDefiner leagueBettingDefiner = leagueBettingDefinerFactory.retieveLeagueDefiner(countryCode);
            selectedLeagues.addAll(leagueBettingDefiner.retieveAllowedBettingLeague(leaguesForCountry.get(countryCode)));
        }
        verifyPersistedLeagueIsCurrent(leagues);
        return leagueMapper.toLeagueDtoList(selectedLeagues);
    }

    public List<LeagueDto> getLeaguesForCurrentSeasonForCountry(String countryCode) {
        List<League> leagues = this.getLeaguesForCountryAndSeason(countryCode, WebUtils.getCurrentSeason());
        verifyPersistedLeagueIsCurrent(leagues);
        //only select the one from the betting sites
        LeagueBettingDefiner leagueBettingDefiner = leagueBettingDefinerFactory.retieveLeagueDefiner(countryCode);
        List<League> selectedLeagues = leagueBettingDefiner.retieveAllowedBettingLeague(leagues);
        return leagueMapper.toLeagueDtoList(selectedLeagues);
    }

    public List<League> getLeaguesForCountryAndSeason(String countryCode, int year) {
        //1. check if data is available in database
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueByCountryAndSeason(countryCode, year));
        if(leagues.isEmpty()) {
            logger.info(String.format("no leagues found for country %s and for season start year %s", countryCode, year));
            List<LeagueDto> leagueDtos = leagueClient.allLeaguesFromCountryForSeason(countryCode,year);
            leagues =  leagueMapper.toLeagues(leagueDtos);
            persistLeagues(leagues);
        }
        return leagues;
    }

    private void verifyPersistedLeagueIsCurrent(List<League> leagues) {
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
        List<League> leagues =  leagueMapper.toLeagues(leagueDtos);
        return leagues.stream()
                .filter(league -> allowedCountries.contains(league.getCountryCode()))
                .collect(Collectors.toList());
    }

    private void persistLeagues(List<League> leagues) {
        List<League> toBePersisted = new ArrayList<>();
        for(League league: leagues) {
            String countryCode = league.getCountryCode();
            LeagueBettingDefiner leagueBettingDefiner = leagueBettingDefinerFactory.retieveLeagueDefiner(league.getCountryCode());
            List<League> leaguesForCountry = leagues.stream().filter(league1 -> league1.getCountryCode().equals(countryCode)).collect(Collectors.toList());
            toBePersisted.addAll(leagueBettingDefiner.retieveAllowedBettingLeague(leaguesForCountry));
        }
        leagueRepository.saveAll(toBePersisted);
    }



}
