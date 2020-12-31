package boets.bts.backend.web.team;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ITeamClient {

    Optional<List<TeamDto>> retrieveTeamsOfLeague(Long leagueId);

    default  JsonArray parseTeamsRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject api =  jsonObject.getAsJsonObject("api");
        return api.getAsJsonArray("teams");
    }

    default List<TeamDto> mapJsonToTeamDto(JsonArray jsonArray) {
        List<TeamDto> dtos = new ArrayList<>();
        for(JsonElement teamJsonElement : jsonArray) {
            TeamDto dto = new TeamDto();
            JsonObject teamJson = teamJsonElement.getAsJsonObject();
            dto.setId(null);
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
