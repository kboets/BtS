package boets.bts.backend.web.standing;

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
public class StandingClient {

    private Logger logger = LoggerFactory.getLogger(StandingClient.class);

    public Optional<List<StandingDto>> getLatestStandForLeague(String leagueId) {
        //1. make call
        OkHttpClient client = new OkHttpClient();
        String url = WebUtils.buildUrl("leagueTable", leagueId);
        Request request = WebUtils.createRequest(url);
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //2. parse data
                JsonArray leagueJsonArray = parseAllRoundsForLeaguesRawJson(response.body().string());
                //3. map data to dto
                return Optional.ofNullable(mapJsonToStandingDto(leagueJsonArray));
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

    private List<StandingDto> mapJsonToStandingDto(JsonArray jsonArray) {
        List<StandingDto> dtos = new ArrayList<>();
        for(JsonElement leagueJsonElement : jsonArray) {
            JsonObject standingJson = leagueJsonElement.getAsJsonObject();
            StandingDto standingDto = new StandingDto();
            
        }

        return dtos;
    }

    private JsonArray parseAllRoundsForLeaguesRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject api =  jsonObject.getAsJsonObject("api");
        return api.getAsJsonArray("standings");
    }
}
