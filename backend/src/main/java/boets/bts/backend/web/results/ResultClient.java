package boets.bts.backend.web.results;

import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.team.TeamDto;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResultClient {

    private Logger logger = LoggerFactory.getLogger(ResultClient.class);
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    public Optional<List<ResultDto>> retrieveAllResultForLeague(Long leagueId) {
        //1. make call
        String url = WebUtils.buildUrl("fixtures", "league", leagueId.toString());
        Request request = WebUtils.createRequest(url);
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //2. parse data
                JsonArray resultJsonArray = parseAllResultsRawJson(response.body().string());
                //3. map data to dto
                return mapJsonToResultDto(resultJsonArray);
            }
        } catch (IOException e) {
            logger.warn("Exception on calling retrieveAllResults" + e);
        } finally {
            if(response != null && response.body()!= null) {
                response.body().close();
            }
        }
        return Optional.empty();
    }

    private JsonArray parseAllResultsRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject api =  jsonObject.getAsJsonObject("api");
        return api.getAsJsonArray("fixtures");
    }

    private Optional<List<ResultDto>> mapJsonToResultDto(JsonArray jsonArray) {
        List<ResultDto> dtos = new ArrayList<>();
        for(JsonElement resultJsonElement : jsonArray) {
            JsonObject resultJson = resultJsonElement.getAsJsonObject();
            if(resultJson.get("status").getAsString().equals("Match Finished")) {
                ResultDto dto = new ResultDto();
                LeagueDto leagueDto = new LeagueDto();
                leagueDto.setLeague_id(resultJson.get("league_id").getAsString());
                dto.setLeague(leagueDto);
                String eventDate = resultJson.get("event_date").getAsString();
                String eventDateRemoved = StringUtils.substringBefore(eventDate, "T");
                dto.setEventDate(LocalDate.parse(eventDateRemoved, dateFormatter));
                RoundDto roundDto = new RoundDto();
                String roundName = resultJson.get("round").getAsString();
                roundDto.setRound(StringUtils.replace(roundName," ", "_" ));
                dto.setRound(roundDto);
                dto.setGoalsAwayTeam(resultJson.get("goalsAwayTeam").getAsInt());
                dto.setGoalsHomeTeam(resultJson.get("goalsHomeTeam").getAsInt());
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
        }
        return Optional.of(dtos);
    }
}
