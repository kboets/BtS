package boets.bts.backend.web.standing;

import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.team.TeamDto;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StandingClient {

    private Logger logger = LoggerFactory.getLogger(StandingClient.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
                JsonArray standingJsonArray = parseAllRoundsForLeaguesRawJson(response.body().string());

                //3. map data to dto
                return Optional.of(mapJsonToStandingDto(standingJsonArray, leagueId));
            }
        } catch (IOException | ParseException e) {
            logger.warn("Exception on calling getAllRoundsForLeagueAndSeason" + e);
        } finally {
            if(response != null && response.body()!= null) {
                response.body().close();
            }
        }
        return Optional.empty();
    }

    private List<StandingDto> mapJsonToStandingDto(JsonArray jsonArray, String leagueId) throws ParseException {
        List<StandingDto> dtos = new ArrayList<>();
        for(JsonElement standingJsonElement1 : jsonArray) {
            for(JsonElement standingJsonElement :standingJsonElement1.getAsJsonArray()) {
                JsonObject standingJson = standingJsonElement.getAsJsonObject();
                StandingDto standingDto = new StandingDto();
                standingDto.setPoints(standingJson.get("points").getAsInt());
                standingDto.setRank(standingJson.get("rank").getAsInt());
                TeamDto teamDto = new TeamDto();
                teamDto.setTeamId(standingJson.get("team_id").getAsString());
                standingDto.setTeam(teamDto);
                standingDto.setLastUpdate(dateFormat.parse(standingJson.get("lastUpdate").getAsString()));
                JsonObject allSubstanding = standingJson.getAsJsonObject("all");
                JsonObject awaySubstanding = standingJson.getAsJsonObject("away");
                JsonObject homeSubstanding = standingJson.getAsJsonObject("home");
                if(!allSubstanding.isJsonNull()) {
                    standingDto.setAllSubStanding(retrieveSubStanding(allSubstanding));
                }
                if(!awaySubstanding.isJsonNull()) {
                    standingDto.setAwaySubStanding(retrieveSubStanding(awaySubstanding));
                }
                if(!homeSubstanding.isJsonNull()) {
                    standingDto.setHomeSubStanding(retrieveSubStanding(awaySubstanding));
                }
                dtos.add(standingDto);
            }
        }
        return dtos;
    }

    private SubStandingDto retrieveSubStanding(JsonObject subStandingJson) {
        SubStandingDto subStandingDto = new SubStandingDto();
        subStandingDto.setMatchPlayed(subStandingJson.get("matchsPlayed").getAsInt());
        subStandingDto.setDraw(subStandingJson.get("draw").getAsInt());
        subStandingDto.setWin(subStandingJson.get("win").getAsInt());
        subStandingDto.setLose(subStandingJson.get("lose").getAsInt());
        subStandingDto.setGoalsFor(subStandingJson.get("goalsFor").getAsInt());
        subStandingDto.setGoalsAgainst(subStandingJson.get("goalsAgainst").getAsInt());
        return subStandingDto;
    }

    private JsonArray parseAllRoundsForLeaguesRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject api =  jsonObject.getAsJsonObject("api");
        return api.getAsJsonArray("standings");
    }
}
