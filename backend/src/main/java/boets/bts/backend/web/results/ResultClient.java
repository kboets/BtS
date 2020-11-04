package boets.bts.backend.web.results;

import boets.bts.backend.web.WebUtils;
import com.google.gson.JsonArray;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Profile("!mock")
@Service
public class ResultClient implements IResultClient {

    private Logger logger = LoggerFactory.getLogger(ResultClient.class);


    public Optional<List<ResultDto>> retrieveAllResultForLeague(Long leagueId, int season) {
        //1. make call
        String url = WebUtils.buildUrl("fixtures", "league", leagueId.toString());
        return makeCallAndMap(url);
    }

    @Override
    public Optional<List<ResultDto>> retrieveAllResultForLeagueAndRound(Long leagueId, String round) {
        //1. make call
        String url = WebUtils.buildUrl("fixtures", "league", leagueId.toString(), round);
        return makeCallAndMap(url);
    }

    private Optional<List<ResultDto>> makeCallAndMap(String url) {
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
