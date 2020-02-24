package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.web.dto.LeagueDto;
import boets.bts.backend.web.dto.LeagueMapper;
import boets.bts.backend.web.league.ILeagueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeagueService  {

    private Logger logger = LoggerFactory.getLogger(LeagueService.class);

    private final LeagueRepository leagueRepository;
    private final ILeagueClient leagueClient;
    private final LeagueMapper leagueMapper;


    public LeagueService(LeagueRepository leagueRepository, ILeagueClient leagueClient, LeagueMapper leagueMapper) {
        this.leagueClient = leagueClient;
        this.leagueRepository = leagueRepository;
        this.leagueMapper = leagueMapper;

    }

    public Optional<LeagueDto> getLeagueById(Long leagueId) {
        Optional<League> leagueOptional = leagueRepository.findById(leagueId);
        if(leagueOptional.isPresent()) {
            return leagueOptional
                    .map(leagueMapper::toLeagueDto);
        }
        return Optional.empty();
    }

    public List<LeagueDto> getCurrentSeasonLeaguesForCountry(String countryCode) {
        List<LeagueDto> leagueDtos = this.getLeaguesForCountryAndSeason(countryCode, getCurrentSeason());
        //1. check if data in db is still up to date -> check isCurrent still ok
        LocalDate now = LocalDate.now();
        if(leagueDtos.stream().anyMatch(leagueDto -> now.isAfter(leagueDto.getEndSeason()) && leagueDto.isCurrent())) {
            logger.info(String.format("Updating the current league to not current as the current date %s is after the end date $S", now, leagueDtos.get(0).getEndSeason()));
            List<LeagueDto> updatedList = leagueDtos.stream().peek(leagueDto -> leagueDto.setCurrent(false)).collect(Collectors.toList());
            leagueRepository.saveAll(leagueMapper.toLeagues(updatedList));
        }
        //2. only available on betting sites (1 + 2 klasse)
        return leagueDtos.stream()
                .filter(leagueDto -> leagueDto.getName().contains("Jupiler") || leagueDto.getName().contains("Klasse"))
                .collect(Collectors.toList());
    }

    public List<LeagueDto> getLeaguesForCountryAndSeason(String countryCode, int year) {
        //1. check if data is available in database
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueByCountryAndSeason(countryCode, year));
        if(leagues.isEmpty()) {
            logger.info(String.format("no leagues found for country %s and for season start year %s", countryCode, year));
            List<LeagueDto> leagueDtos = leagueClient.retrieveAllLeagues(countryCode,year);
            leagues =  leagueMapper.toLeagues(leagueDtos);
            leagueRepository.saveAll(leagues);
            return leagueDtos;
        }
        return leagueMapper.toLeagueDtoList(leagues);
    }

    private int getCurrentSeason() {
        LocalDate now = LocalDate.now();
        if(now.getMonthValue() < Month.JULY.getValue()) {
            return now.getYear() - 1;
        }
        return  now.getYear();
    }
}
