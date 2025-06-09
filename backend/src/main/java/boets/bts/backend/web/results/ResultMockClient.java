package boets.bts.backend.web.results;

import boets.bts.backend.web.WebUtils;
import com.google.gson.JsonArray;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Profile("mock")
@Service
public class ResultMockClient implements IResultClient {

    private static String fixtures_jupilerLeagues_2020 = "fixtures_jupilerLeague_2020.json";
    private static String fixtures_jupilerLeagues_2020Round2 = "fixtures_jupilerLeague_2020Round2.json";
    private static final Map<Long, String> fixturesForLeagueAndSeason;

    static {
        fixturesForLeagueAndSeason = new HashMap<>();
        fixturesForLeagueAndSeason.put(2660L, fixtures_jupilerLeagues_2020);
        fixturesForLeagueAndSeason.put(2661L, fixtures_jupilerLeagues_2020Round2);

    }

    public Optional<List<ResultDto>>  retrieveAllResultForLeague(Long leagueId, int season) {
        //1. get json file
        String fileName = fixturesForLeagueAndSeason.get(leagueId);
        //2. make fake call
        String dataJson = WebUtils.readJsonFileFromApi(fileName, season).orElseGet(String::new);
        //3.  parse data
        JsonArray results = parseAllResultsRawJson(dataJson);
        //4. map data to dto
        return mapJsonToResultDto(results);
    }

    @Override
    public Optional<List<ResultDto>> retrieveAllResultForLeagueAndRound(Long leagueId, String round, int season) {
        //1. get json file
        String fileName = fixturesForLeagueAndSeason.get(2661L);
        //2. make fake call
        String dataJson = WebUtils.readJsonFileFromApi(fileName, 2020).orElseGet(String::new);
        //3.  parse data
        JsonArray results = parseAllResultsRawJson(dataJson);
        //4. map data to dto
        return mapJsonToResultDto(results);
    }
}
