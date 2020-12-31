package boets.bts.backend.web.league;

import boets.bts.backend.web.WebUtils;
import com.google.gson.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("mock")
@Service
public class LeagueMockClient implements ILeagueClient{

    private static String allLeaguesFile = "leagues_allBelgium_2020.json";
    @Override
    public List<LeagueDto> allLeaguesFromCountryForSeason(String countryCode, int year) {
        //1. make fake call
        String dataJson = WebUtils.readJsonFileFromApi(allLeaguesFile, year).orElseGet(String::new);
        //2. parse data
        JsonArray leagues = parseAllLeaguesRawJson(dataJson);
        //3. map data to dto
        return mapJsonToLeagueDto(leagues);
    }

    @Override
    public List<LeagueDto> allLeaguesForSeason(int year) {
        return allLeaguesFromCountryForSeason("BE", 2020);
    }


}
