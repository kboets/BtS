package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.repository.LeagueRepository;
import boets.bts.backend.web.dto.LeagueDto;
import boets.bts.backend.web.league.ILeagueClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LeagueService  {

    private LeagueRepository leagueRepository;

    private ILeagueClient leagueClient;

    public LeagueService(LeagueRepository leagueRepository, ILeagueClient leagueClient) {
        this.leagueClient = leagueClient;
        this.leagueRepository = leagueRepository;
    }

    public List<LeagueDto> getLeaguesForYearAndCountry(String countryCode, int year) {
        //1. check if data is available in database

        //1a. data not available -> retrieve data from webservice
        //1b. save all this data to db
        //2a. data is available -> run checks if data is still up-to-date
        //3. data is not up-to-date -> redo 1a
        //4. data is up to date -> return data

        return Collections.emptyList();
    }
}
