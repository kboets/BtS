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
        //JsonObject api =  jsonObject.getAsJsonObject("api");
        return jsonObject.getAsJsonArray("response");
    }

    default List<TeamDto> mapJsonToTeamDto(JsonArray jsonArray) {
        List<TeamDto> dtos = new ArrayList<>();
        for(JsonElement standingJsonElement : jsonArray) {
            JsonObject standingJson = standingJsonElement.getAsJsonObject();
            if (!standingJson.get("league").isJsonNull())  {
                JsonObject leagueMember = standingJson.get("league").getAsJsonObject();
                JsonArray standingMembers = leagueMember.getAsJsonArray("standings").get(0).getAsJsonArray();
                for(JsonElement standingMember : standingMembers) {
                    if (!standingMember.isJsonNull()) {
                        JsonElement teamMember = standingMember.getAsJsonObject().get("team");
                        TeamDto dto = new TeamDto();
                        if (teamMember != null) {
                            JsonObject teamJson = teamMember.getAsJsonObject();
                            dto.setId(null);
                            dto.setTeamId(teamJson.get("id").getAsString());
                            dto.setName(teamJson.get("name").getAsString());
                            if(!teamJson.get("logo").isJsonNull()) {
                                dto.setLogo(teamJson.get("logo").getAsString());
                            }
                            dtos.add(dto);
                        }
                    }
                }

            }

        }

        return dtos;
    }

}
