package boets.bts.backend.web.country;

import boets.bts.backend.service.admin.AdminService;
import boets.bts.backend.web.ParentClient;
import boets.bts.backend.web.WebUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CountryClient extends ParentClient {

    protected Logger logger = LoggerFactory.getLogger(CountryClient.class);

    public CountryClient(AdminService adminService) {
        super(adminService);
    }

    public Optional<List<CountryDto>> getAllCountries() {
        //1. make call
        OkHttpClient client = new OkHttpClient();
        String url = WebUtils.buildUrl("countries");
        Request request = createRequest(url);
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //2. parse data
                JsonArray countries = parseAllCountriesRawJson(response.body().string());
                //3. map data to dto
                return Optional.of(mapJsonToCountryDto(countries));
            }
        } catch (IOException e) {
            logger.warn("Exception on calling retrieveAllCountries" + e);
        }
        return Optional.empty();
    }

    private JsonArray parseAllCountriesRawJson(String jsonAsString) {
        JsonElement jsonElement = JsonParser.parseString(jsonAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject api =  jsonObject.getAsJsonObject("api");
        return api.getAsJsonArray("countries");
    }

    private List<CountryDto> mapJsonToCountryDto(JsonArray jsonArray) {
        List<CountryDto> dtos = new ArrayList<>();
        for(JsonElement leagueJsonElement : jsonArray) {
            JsonObject leagueJson = leagueJsonElement.getAsJsonObject();
            if(!leagueJson.get("code").isJsonNull()) {
                CountryDto dto = new CountryDto();
                dto.setCountry(leagueJson.get("country").getAsString());
                dto.setCountryCode(leagueJson.get("code").getAsString());
                if(!leagueJson.get("flag").isJsonNull()) {
                    dto.setFlag(leagueJson.get("flag").getAsString());
                }
                dtos.add(dto);
            }
        }
        return dtos;
    }

}
