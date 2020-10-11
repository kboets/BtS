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
public class ResultClient implements IResultsClient {

    private Logger logger = LoggerFactory.getLogger(ResultClient.class);


    public Optional<List<ResultDto>> retrieveAllResultForLeague(Long leagueId, int season) {
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

}
