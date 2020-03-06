package boets.bts.backend.web.round;

import boets.bts.backend.web.WebUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoundClient {

    private Logger logger = LoggerFactory.getLogger(RoundClient.class);

    public Optional<List<RoundDto>> getAllRoundsForLeagueAndSeason(int season, long leagueId) {
        //1. make call
        OkHttpClient client = new OkHttpClient();
        String url = WebUtils.buildUrl("rounds", "league", Long.toString(leagueId));
        Request request = WebUtils.createRequest(url);
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //2. parse data
                JsonArray leagueJsonArray = parseAllRoundsForLeaguesRawJson(response.body().string());
                //3. map data to dto
                return Optional.ofNullable(mapJsonToRoundDto(leagueJsonArray,season));
            }
        } catch (IOException e) {
            logger.warn("Exception on calling getAllRoundsForLeagueAndSeason" + e);
        } finally {
            if(response != null && response.body()!= null) {
                response.body().close();
            }
        }
        return Optional.empty();
    }

    private JsonArray parseAllRoundsForLeaguesRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject api =  jsonObject.getAsJsonObject("api");
        return api.getAsJsonArray("fixtures");
    }

    private List<RoundDto> mapJsonToRoundDto(JsonArray jsonArray, int season) {
        int roundNumber = 1;
        List<RoundDto> roundDtos = new ArrayList<>();
        for(JsonElement roundJsonElement : jsonArray) {
            RoundDto roundDto = new RoundDto();
            JsonObject roundJson = roundJsonElement.getAsJsonObject();
            roundDto.setRound(roundJson.getAsString());
            roundDto.setSeason(season);
            roundDto.setRoundId(Integer.toString(roundNumber));
            roundNumber++;
        }

        return roundDtos;
    }
}
