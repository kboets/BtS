package boets.bts.backend.web.league;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public interface ILeagueClient {

    List<LeagueDto> allLeaguesFromCountryForSeason(String countryCode, int year);

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");

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
            int isCurrent = leagueJson.get("is_current").getAsInt();
            dto.setCurrent(isCurrent != 0);
            String endDate = leagueJson.get("season_end").getAsString();
            dto.setEndSeason(LocalDate.parse(endDate, dateFormatter));
            String startDate = leagueJson.get("season_start").getAsString();
            dto.setStartSeason(LocalDate.parse(startDate, dateFormatter));
            dto.setFlag(leagueJson.get("flag").getAsString());
            if(!leagueJson.get("logo").isJsonNull()) {
                dto.setLogo(leagueJson.get("logo").getAsString());
            }
            dto.setName(leagueJson.get("name").getAsString());
            dto.setCountryCode(leagueJson.get("country_code").getAsString());
            dto.setSeason(Integer.parseInt(leagueJson.get("season").getAsString()));
            dtos.add(dto);
        }

        return dtos;
    }
}
