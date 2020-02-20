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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public List<LeagueDto> getLeaguesForCountryAndSeason(String countryCode, int year) {
        //1. check if data is available in database
        List<LeagueDto> leagueDtos = new ArrayList<>();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueByCountryAndSeason(countryCode, year));
        if(leagues.isEmpty()) {
            logger.info(String.format("no leagues found for country %s and for season start year %s", countryCode, year));
            leagueDtos = leagueClient.retrieveAllLeagues(countryCode,year);
            leagues =  leagueMapper.toLeagues(leagueDtos);
            leagueRepository.saveAll(leagues);
        }
        //2a. data is available -> run checks if data is still up-to-date
        //3. data is not up-to-date -> redo 1a
        //4. data is up to date -> return data

        return Collections.emptyList();
    }
}
