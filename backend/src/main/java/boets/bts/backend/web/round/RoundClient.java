package boets.bts.backend.web.round;

import boets.bts.backend.service.admin.AdminService;
import boets.bts.backend.web.ParentClient;
import boets.bts.backend.web.WebUtils;
import com.google.gson.JsonArray;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Profile("!mock")
public class RoundClient extends ParentClient implements IRoundClient{

    private Logger logger = LoggerFactory.getLogger(RoundClient.class);

    public RoundClient(AdminService adminService) {
        super(adminService);
    }

    public Optional<RoundDto> getCurrentRoundForLeagueAndSeason(long leagueId, int season) {
        //1. make call
        OkHttpClient client = new OkHttpClient();
        String url = WebUtils.buildUrl("fixtures", "rounds", Long.toString(leagueId), "current");
        Request request = createRequest(url);
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //2. parse data
                JsonArray leagueJsonArray = parseAllRoundsForLeaguesRawJson(response.body().string());
                //3. map data to dto
                List<RoundDto> roundDtos = mapJsonToRoundDto(leagueJsonArray, season);
                RoundDto roundDto = roundDtos.get(0);
                if(roundDto != null) {
                    roundDto.setCurrent(true);
                    roundDto.setCurrentDate(LocalDate.now());
                }
                return Optional.ofNullable(roundDto);
            }
        } catch (IOException e) {
            logger.warn("Exception on calling getAllRoundsForLeagueAndSeason" + e);
        } finally {
            if(response != null && response.body()!= null) {
                response.body().close();
            }
        }
        return Optional.empty();
    }

    public Optional<List<RoundDto>> getAllRoundsForLeagueAndSeason(int season, long leagueId) {
        //1. make call
        OkHttpClient client = new OkHttpClient();
        String url = WebUtils.buildUrl("fixtures", "rounds", Long.toString(leagueId));
        Request request = createRequest(url);
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //2. parse data
                JsonArray leagueJsonArray = parseAllRoundsForLeaguesRawJson(response.body().string());
                //3. map data to dto
                return Optional.ofNullable(mapJsonToRoundDto(leagueJsonArray,season));
            }
        } catch (IOException e) {
            logger.warn("Exception on calling getAllRoundsForLeagueAndSeason" + e);
        } finally {
            if(response != null && response.body()!= null) {
                response.body().close();
            }
        }
        return Optional.empty();
    }


}
