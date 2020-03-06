package boets.bts.backend.web.team;

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
import java.util.Objects;
import java.util.Optional;

@Service
public class TeamClient {

    private Logger logger = LoggerFactory.getLogger(TeamClient.class);

    public Optional<List<TeamDto>> retrieveTeamsOfLeague(Long leagueId) {
        //1. make call
        OkHttpClient client = new OkHttpClient();
        String url = WebUtils.buildUrl("teams", "league", leagueId.toString());
        Request request = WebUtils.createRequest(url);
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //2. parse data
                JsonArray teams = this.parseTeamsRawJson(response.body().string());
                //3. map data to dto
                return Optional.of(this.mapJsonToTeamDto(teams));
            }
        } catch (IOException e) {
            logger.warn("Exception on calling retrieveTeamsOfLeague" + e);
        } finally {
            if(response != null) {
                Objects.requireNonNull(response.body()).close();
            }
        }
        return Optional.empty();
    }

    private JsonArray parseTeamsRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject api =  jsonObject.getAsJsonObject("api");
        return api.getAsJsonArray("teams");
    }

    private List<TeamDto> mapJsonToTeamDto(JsonArray jsonArray) {
        List<TeamDto> dtos = new ArrayList<>();
        for(JsonElement teamJsonElement : jsonArray) {
            TeamDto dto = new TeamDto();
            JsonObject teamJson = teamJsonElement.getAsJsonObject();
            dto.setTeamId(teamJson.get("team_id").getAsString());
            dto.setName(teamJson.get("name").getAsString());
            if(!teamJson.get("logo").isJsonNull()) {
                dto.setLogo(teamJson.get("logo").getAsString());
            }
            if(!teamJson.get("venue_city").isJsonNull()) {
                dto.setCity(teamJson.get("venue_city").getAsString());
            }
            if(!teamJson.get("venue_name").isJsonNull()) {
                dto.setStadiumName(teamJson.get("venue_name").getAsString());
            }
            if(!teamJson.get("venue_capacity").isJsonNull()) {
                int capacity = teamJson.get("venue_capacity").getAsInt();
                dto.setStadiumCapacity(capacity);
            }
            dtos.add(dto);
        }

        return dtos;
    }
}
