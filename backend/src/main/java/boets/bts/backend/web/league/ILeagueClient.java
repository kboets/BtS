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

    List<LeagueDto> allLeaguesForSeason(int year);

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    default JsonArray parseAllLeaguesRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return jsonObject.getAsJsonArray("response");
    }

    default List<LeagueDto> mapJsonToLeagueDto(JsonArray jsonArray) {
        List<LeagueDto> dtos = new ArrayList<>();
        for(JsonElement leagueJsonElement : jsonArray) {
            JsonObject leagueJson = leagueJsonElement.getAsJsonObject();
            LeagueDto dto = new LeagueDto();
            if (!leagueJson.get("league").isJsonNull())  {
                JsonObject leagueMember = leagueJson.get("league").getAsJsonObject();
                if (!leagueMember.get("id").isJsonNull()) {
                    dto.setLeague_id(leagueMember.get("id").getAsString());
                }
                if (!leagueMember.get("logo").isJsonNull()) {
                    dto.setLogo(leagueMember.get("logo").getAsString());
                }
                if (!leagueMember.get("name").isJsonNull()) {
                    dto.setName(leagueMember.get("name").getAsString());
                }
            }
            if (!leagueJson.get("country").isJsonNull())  {
                JsonObject countryMember = leagueJson.get("country").getAsJsonObject();
                if (!countryMember.get("code").isJsonNull()) {
                    dto.setCountryCode(countryMember.get("code").getAsString());
                }
                if (!countryMember.get("flag").isJsonNull()) {
                    dto.setFlag(countryMember.get("flag").getAsString());
                }
            }
            if (!leagueJson.get("seasons").isJsonNull())  {
                JsonObject seasonMember = leagueJson.getAsJsonArray("seasons").get(0).getAsJsonObject();
                if (!seasonMember.get("start").isJsonNull()) {
                    dto.setStartSeason(LocalDate.parse(seasonMember.get("start").getAsString(), dateFormatter));
                }
                if (!seasonMember.get("end").isJsonNull()) {
                    dto.setEndSeason(LocalDate.parse(seasonMember.get("end").getAsString(), dateFormatter));
                }
                if (!seasonMember.get("year").isJsonNull()) {
                    dto.setSeason(seasonMember.get("year").getAsInt());
                }
                if (!seasonMember.get("current").isJsonNull()) {
                    dto.setCurrent(seasonMember.get("current").getAsBoolean());
                }

            }
            dtos.add(dto);
        }

        return dtos;
    }


}
