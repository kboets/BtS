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

    Optional<List<ResultDto>> retrieveAllResultForLeagueAndRound(Long leagueId, String round, int season);

    default JsonArray parseAllResultsRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return jsonObject.getAsJsonArray("response");
    }

    default Optional<List<ResultDto>> mapJsonToResultDto(JsonArray jsonArray) {
        List<ResultDto> dtos = new ArrayList<>();
        for(JsonElement resultJsonElement : jsonArray) {
            JsonObject resultJson = resultJsonElement.getAsJsonObject();
            ResultDto dto = new ResultDto();
            if (!resultJson.get("fixture").isJsonNull())  {
                JsonObject fixtureMember = resultJson.get("fixture").getAsJsonObject();
                String eventDate = fixtureMember.get("date").getAsString();
                String eventDateRemoved = StringUtils.substringBefore(eventDate, "T");
                dto.setEventDate(LocalDate.parse(eventDateRemoved, dateFormatter));
                if (fixtureMember.get("status") != null) {
                    JsonObject statusMember = fixtureMember.get("status").getAsJsonObject();
                    dto.setMatchStatus(statusMember.get("short").getAsString());
                }
            }
            if (!resultJson.get("league").isJsonNull())  {
                JsonObject leagueMember = resultJson.get("league").getAsJsonObject();
                dto.setRound(leagueMember.get("round").getAsString());
            }
            if (!resultJson.get("teams").isJsonNull())  {
                JsonObject teamsMember = resultJson.get("teams").getAsJsonObject();
                if (!teamsMember.get("home").isJsonNull()) {
                    JsonObject homeTeamMember = teamsMember.get("home").getAsJsonObject();
                    TeamDto homeTeamDto = new TeamDto();
                    homeTeamDto.setTeamId(homeTeamMember.get("id").getAsString());
                    homeTeamDto.setName(homeTeamMember.get("name").getAsString());
                    dto.setHomeTeam(homeTeamDto);
                }
                if (!teamsMember.get("away").isJsonNull()) {
                    JsonObject awayTeamMember = teamsMember.get("away").getAsJsonObject();
                    TeamDto awayTeamDto = new TeamDto();
                    awayTeamDto.setTeamId(awayTeamMember.get("id").getAsString());
                    awayTeamDto.setName(awayTeamMember.get("name").getAsString());
                    dto.setAwayTeam(awayTeamDto);
                }
            }
            if (!resultJson.get("goals").isJsonNull())  {
                JsonObject goalsMember = resultJson.get("goals").getAsJsonObject();
                if (!goalsMember.get("home").isJsonNull()) {
                    dto.setGoalsHomeTeam(goalsMember.get("home").getAsInt());
                }
                if (!goalsMember.get("away").isJsonNull()) {
                    dto.setGoalsAwayTeam(goalsMember.get("away").getAsInt());
                }
            }
            dtos.add(dto);
        }
        return Optional.of(dtos);
    }
}
