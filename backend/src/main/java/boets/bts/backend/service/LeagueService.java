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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeagueService  {

    private Logger logger = LoggerFactory.getLogger(LeagueService.class);

    private final LeagueRepository leagueRepository;
    private final ILeagueClient leagueClient;
    private final LeagueMapper leagueMapper;
    private final TeamService teamService;
    private final LeagueBettingDefinerFactory leagueBettingDefinerFactory;



    public LeagueService(LeagueRepository leagueRepository, ILeagueClient leagueClient, LeagueMapper leagueMapper, TeamService teamService, LeagueBettingDefinerFactory leagueBettingDefinerFactory) {
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

//    public List<LeagueDto> getAllCurrentLeagues() {
//        List<League> leagueDtos = leagueRepository.findAll();
//        //1. check if data in db is still up to date -> check isCurrent still ok
//        LocalDate now = LocalDate.now();
//
//    }

    public List<LeagueDto> getCurrentSeasonLeaguesForCountry(String countryCode) {
        List<LeagueDto> leagueDtos = this.getLeaguesForCountryAndSeason(countryCode, WebUtils.getCurrentSeason());
        //1. check if data in db is still up to date -> check isCurrent still ok
        LocalDate now = LocalDate.now();
        if(leagueDtos.stream().anyMatch(leagueDto -> now.isAfter(leagueDto.getEndSeason()) && leagueDto.isCurrent())) {
            logger.info(String.format("Updating the current league to not current as the current date %s is after the end date %S", now, leagueDtos.get(0).getEndSeason()));
            List<LeagueDto> updatedList = leagueDtos.stream().peek(leagueDto -> leagueDto.setCurrent(false)).collect(Collectors.toList());
            leagueRepository.saveAll(leagueMapper.toLeagues(updatedList));
        }
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


}
