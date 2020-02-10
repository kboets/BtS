package boets.bts.backend.web.league;

import boets.bts.backend.web.dto.LeagueDto;
import com.google.gson.JsonArray;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class LeagueClient implements ILeagueClient {

    Logger logger = LoggerFactory.getLogger(LeagueClient.class);

    private static String BASE_URL = "https://api-football-v1.p.rapidapi.com/v2/";

    @Override
    public List<LeagueDto> retrieveAllLeagues(String countryCode, int year) {
        //1. make call
        OkHttpClient client = new OkHttpClient();
        String url = buildUrl("leagues", "country", countryCode, Integer.toString(year));
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "6e79ed93c5msh894b4d2d1159c98p1d54d0jsn39da77eb31a6")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //2. parse data
                JsonArray leagues = parseAllBelgianLeaguesRawJson(response.body().string());
                //3. map data to dto
                return mapJsonToLeagueDto(leagues);
            }
        } catch (IOException e) {
            logger.warn("Exception on calling retrieveAllLeagues" + e);
        }
        return Collections.emptyList();
    }

    private String buildUrl(String... vars) {
        StringBuilder builder = new StringBuilder();
        builder.append(BASE_URL);
        for(String arg: vars) {
            builder.append(arg);
            builder.append("/");
        }
        return builder.toString();
    }
}
