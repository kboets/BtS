package boets.bts.backend.web.team;

import boets.bts.backend.web.WebUtils;
import com.google.gson.JsonArray;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Profile("mock")
@Service
public class TeamMockClient implements ITeamClient {

    private static String allTeamsForLeaguesFile = "allTeams_jupilerLeague_2020.json";

    @Override
    public Optional<List<TeamDto>> retrieveTeamsOfLeague(Long leagueId) {
        //1. make fake call
        String dataJson = WebUtils.readJsonFileFromApi(allTeamsForLeaguesFile, 2020).orElseGet(String::new);
        //2. parse data
        JsonArray teamsRawJson = parseTeamsRawJson(dataJson);
        //3. map data to dto
        return Optional.ofNullable(mapJsonToTeamDto(teamsRawJson));
    }
}
