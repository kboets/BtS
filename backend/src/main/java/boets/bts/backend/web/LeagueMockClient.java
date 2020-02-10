package boets.bts.backend.web;

import boets.bts.backend.dto.LeagueDto;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
