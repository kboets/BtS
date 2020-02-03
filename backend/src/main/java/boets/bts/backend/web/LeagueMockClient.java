package boets.bts.backend.web;

import boets.bts.backend.dto.LeagueDto;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("mock")
public class LeagueMockClient implements ILeagueClient{

    @Override
    public List<LeagueDto> retrieveAllLeagues(String countryCode, int year) {
        return null;
    }
}
