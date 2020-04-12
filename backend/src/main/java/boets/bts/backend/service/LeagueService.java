package boets.bts.backend.service;

import boets.bts.backend.domain.Country;
import boets.bts.backend.domain.League;
import boets.bts.backend.repository.country.CountryRepository;
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
        List<League> leagues = getOrRetrieveLeaguesCurrentSeason();
        Map<String, List<League>> leaguesForCountry = leagues.stream()
                .filter(league -> allowedCountries.contains(league.getCountryCode()))
                .filter(league -> league.isSelected() == isSelected)
                .collect(Collectors.groupingBy(League::getCountryCode));
        List<LeagueDto> leagueDtos = new ArrayList<>();
        for(String countryCode: leaguesForCountry.keySet()) {
            LeagueBettingDefiner leagueBettingDefiner = leagueBettingDefinerFactory.retieveLeagueDefiner(countryCode);
            leagueDtos.addAll(leagueBettingDefiner.retieveAllowedBettingLeague(leagueMapper.toLeagueDtoList(leaguesForCountry.get(countryCode))));
        }
        return leagueDtos;
    }

    public List<LeagueDto> getLeaguesForCurrentSeasonForCountry(String countryCode) {
        List<LeagueDto> leagueDtos = this.getLeaguesForCountryAndSeason(countryCode, WebUtils.getCurrentSeason());
        //1. check if data in db is still up to date -> check isCurrent still ok
        LocalDate now = LocalDate.now();
        verifyPersistedLeagueIsCurrent(leagueMapper.toLeagues(leagueDtos));
        LeagueBettingDefiner leagueBettingDefiner = leagueBettingDefinerFactory.retieveLeagueDefiner(countryCode);

        return leagueBettingDefiner.retieveAllowedBettingLeague(leagueDtos);

    }

    public List<LeagueDto> getLeaguesForCountryAndSeason(String countryCode, int year) {
        //1. check if data is available in database
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueByCountryAndSeason(countryCode, year));
        if(leagues.isEmpty()) {
            logger.info(String.format("no leagues found for country %s and for season start year %s", countryCode, year));
            List<LeagueDto> leagueDtos = leagueClient.allLeaguesFromCountryForSeason(countryCode,year);
            leagues =  leagueMapper.toLeagues(leagueDtos);
            leagueRepository.saveAll(leagues);
            return leagueDtos;
        }
        return leagueMapper.toLeagueDtoList(leagues);
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

    private List<League> getOrRetrieveLeaguesCurrentSeason() {
        int currentSeason = WebUtils.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(currentSeason));
        if(leagues.isEmpty()) {
            logger.info(String.format("no leagues found for season start year %s", currentSeason));
            List<LeagueDto> leagueDtos = leagueClient.allLeaguesForSeason(currentSeason);
            leagues =  leagueMapper.toLeagues(leagueDtos);
            leagueRepository.saveAll(leagues);
        }
        verifyPersistedLeagueIsCurrent(leagues);
        return leagues;
    }


}
