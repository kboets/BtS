package boets.bts.backend.web.league;

import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.dto.LeagueDto;
import com.google.gson.JsonArray;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class LeagueClient implements ILeagueClient {

    private Logger logger = LoggerFactory.getLogger(LeagueClient.class);

    @Override
    public List<LeagueDto> allLeaguesFromCountryForSeason(String countryCode, int year) {
        //1. make call
        OkHttpClient client = new OkHttpClient();
        String url = WebUtils.buildUrl("leagues", "country", countryCode, Integer.toString(year));
        Request request = WebUtils.createRequest(url);
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

}
