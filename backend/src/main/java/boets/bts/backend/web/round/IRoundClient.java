package boets.bts.backend.web.round;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface IRoundClient {

    Optional<RoundDto> getCurrentRoundForLeagueAndSeason(long leagueId, int season);

    Optional<List<RoundDto>> getAllRoundsForLeagueAndSeason(int season, long leagueId);

    default JsonArray parseAllRoundsForLeaguesRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return jsonObject.getAsJsonArray("response");
    }

    default List<RoundDto> mapJsonToRoundDto(JsonArray jsonArray, int season) {
        List<RoundDto> roundDtos = new ArrayList<>();
        for(JsonElement roundJsonElement : jsonArray) {
            RoundDto roundDto = new RoundDto();
            roundDto.setRound(roundJsonElement.getAsString());
            roundDto.setSeason(season);
            roundDtos.add(roundDto);
        }

        return roundDtos;
    }
}
