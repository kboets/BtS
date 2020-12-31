package boets.bts.backend.web.round;

import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.league.LeagueDto;
import com.google.gson.JsonArray;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Profile("mock")
public class RoundMockClient implements IRoundClient {

    private static String allRoundsForLeagueFile = "allRounds_jupilerLeague_2020.json";

    @Override
    public Optional<RoundDto> getCurrentRoundForLeagueAndSeason(long leagueId, int season) {
        RoundDto roundDto = new RoundDto();
        roundDto.setCurrent(true);
        roundDto.setRound("Regular_Season_-_3");
        LeagueDto leagueDto = new LeagueDto();
        leagueDto.setLeague_id(Long.toString(leagueId));
        roundDto.setLeagueDto(leagueDto);
        return Optional.of(roundDto);
    }

    @Override
    public Optional<List<RoundDto>> getAllRoundsForLeagueAndSeason(int season, long leagueId) {
        //1. make fake call
        String dataJson = WebUtils.readJsonFileFromApi(allRoundsForLeagueFile, 2020).orElseGet(String::new);
        //2. parse data
        JsonArray rounds = parseAllRoundsForLeaguesRawJson(dataJson);
        //3. map data to dto
        return Optional.ofNullable(mapJsonToRoundDto(rounds, season));


    }
}
