package boets.bts.backend.web.results;

import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.team.TeamDto;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface IResultClient {


    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    Optional<List<ResultDto>> retrieveAllResultForLeague(Long leagueId, int season);

    Optional<List<ResultDto>> retrieveAllResultForLeagueAndRound(Long leagueId, String round);

    default JsonArray parseAllResultsRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject api =  jsonObject.getAsJsonObject("api");
        return api.getAsJsonArray("fixtures");
    }

    default Optional<List<ResultDto>> mapJsonToResultDto(JsonArray jsonArray) {
        List<ResultDto> dtos = new ArrayList<>();
        for(JsonElement resultJsonElement : jsonArray) {
            JsonObject resultJson = resultJsonElement.getAsJsonObject();
            String matchStatus = resultJson.get("status").getAsString();
            ResultDto dto = new ResultDto();
            dto.setMatchStatus(matchStatus);
            dto.setMatchStatus(resultJson.get("status").getAsString());
            LeagueDto leagueDto = new LeagueDto();
            leagueDto.setLeague_id(resultJson.get("league_id").getAsString());
            dto.setLeague(leagueDto);
            String eventDate = resultJson.get("event_date").getAsString();
            String eventDateRemoved = StringUtils.substringBefore(eventDate, "T");
            dto.setEventDate(LocalDate.parse(eventDateRemoved, dateFormatter));
            String roundName = resultJson.get("round").getAsString();
            dto.setRound(StringUtils.replace(roundName," ", "_" ));
            if(!resultJson.get("goalsAwayTeam").isJsonNull()) {
                dto.setGoalsAwayTeam(resultJson.get("goalsAwayTeam").getAsInt());
            }
            if(!resultJson.get("goalsHomeTeam").isJsonNull()) {
                dto.setGoalsHomeTeam(resultJson.get("goalsHomeTeam").getAsInt());
            }
            TeamDto awayTeamDto = new TeamDto();
            JsonObject awayTeamJsonObject = resultJson.getAsJsonObject("awayTeam");
            awayTeamDto.setTeamId(awayTeamJsonObject.get("team_id").getAsString());
            dto.setAwayTeam(awayTeamDto);
            TeamDto homeTeamDto = new TeamDto();
            JsonObject homeTeamJsonObject = resultJson.getAsJsonObject("homeTeam");
            homeTeamDto.setTeamId(homeTeamJsonObject.get("team_id").getAsString());
            dto.setHomeTeam(homeTeamDto);
            dtos.add(dto);
        }
        return Optional.of(dtos);
    }
}
