package boets.bts.backend.web.league;

import boets.bts.backend.web.dto.LeagueDto;
import boets.bts.backend.web.WebUtils;
import com.google.gson.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("mock")
@Service
public class LeagueMockClient implements ILeagueClient{

    private static String allLeaguesFile = "allBelgianLeagues.json";
    @Override
    public List<LeagueDto> retrieveAllLeagues(String countryCode, int year) {
        //1. make fake call
        String dataJson = WebUtils.readJsonFileFromApi(allLeaguesFile).orElseGet(String::new);
        //2. parse data
        JsonArray leagues = parseAllBelgianLeaguesRawJson(dataJson);
        //3. map data to dto
        return mapJsonToLeagueDto(leagues);
    }


}
