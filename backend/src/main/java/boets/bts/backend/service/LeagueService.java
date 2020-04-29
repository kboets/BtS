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
    private final RoundService roundService;


    public LeagueService(LeagueRepository leagueRepository, ILeagueClient leagueClient, LeagueMapper leagueMapper, TeamService teamService
            , LeagueBettingDefinerFactory leagueBettingDefinerFactory, RoundService roundService) {
        this.leagueClient = leagueClient;
        this.leagueRepository = leagueRepository;
        this.leagueMapper = leagueMapper;
        this.teamService = teamService;
        this.leagueBettingDefinerFactory = leagueBettingDefinerFactory;
        this.roundService = roundService;
    }

    public List<LeagueDto> getCurrentSelectedLeagues() {
        boolean isChanged = false;
        List<LeagueDto> availableLeagues = this.getLeaguesCurrentSeason(true);
        //check teams
        List<League> leagueList = leagueMapper.toLeagues(availableLeagues);
        if(availableLeagues.stream().anyMatch(leagueDto -> leagueDto.getTeamDtos().isEmpty())) {
            isChanged = true;
            for(League league: leagueList) {
                teamService.updateLeagueWithTeams(league);
            }
        }
        //check rounds
        if(availableLeagues.stream().anyMatch(leagueDto -> leagueDto.getRoundDtos().isEmpty())) {
            isChanged = true;
            for(League league: leagueList) {
                roundService.updateLeagueWithRounds(league);
            }
        }
        if(isChanged) {
            leagueRepository.saveAll(leagueList);
            availableLeagues.clear();
            availableLeagues.addAll(leagueMapper.toLeagueDtoList(leagueList));
        }

       return availableLeagues;
    }


    public List<LeagueDto> getLeaguesCurrentSeason(boolean isSelected) {
        int currentSeason = WebUtils.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(currentSeason));
        if(leagues.isEmpty()) {
            //make call to webservice
            logger.info(String.format("no leagues found for season start year %s", currentSeason));
            leagues = retrieveLeaguesFromWebService(currentSeason);
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
        if(leagues.isEmpty()) {
            leagueRepository.saveAll(selectedLeagues);
        }
        verifyPersistedLeagueIsCurrent(selectedLeagues);
        return leagueMapper.toLeagueDtoList(selectedLeagues);
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
        return leagueMapper.toLeagues(leagueDtos);
    }

}
