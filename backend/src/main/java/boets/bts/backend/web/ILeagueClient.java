package boets.bts.backend.web;

import boets.bts.backend.dto.LeagueDto;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public interface ILeagueClient {

    List<LeagueDto> retrieveAllLeagues(String countryCode, int year);

    default JsonArray parseAllBelgianLeaguesRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject api =  jsonObject.getAsJsonObject("api");
        return api.getAsJsonArray("leagues");
    }

    default List<LeagueDto> mapJsonToLeagueDto(JsonArray jsonArray) {
        List<LeagueDto> dtos = new ArrayList<>();
        for(JsonElement leagueJsonElement : jsonArray) {
            LeagueDto dto = new LeagueDto();
            JsonObject leagueJson = leagueJsonElement.getAsJsonObject();
            dto.setLeague_id(leagueJson.get("league_id").getAsString());
            leagueJson.get("is_current");
            dto.setCurrent(leagueJson.get("is_current").getAsBoolean());
            dto.setEnd_season(leagueJson.get("season_end").getAsString());
            dto.setStart_season(leagueJson.get("season_start").getAsString());
            dto.setFlag(leagueJson.get("flag").getAsString());
            if(!leagueJson.get("logo").isJsonNull()) {
                dto.setLogo(leagueJson.get("logo").getAsString());
            }
            dto.setName(leagueJson.get("name").getAsString());
            dtos.add(dto);
        }

        return dtos;
    }
}
