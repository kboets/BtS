package boets.bts.backend.service.leagueDefiner;

import boets.bts.backend.web.league.LeagueDto;

import java.util.List;
import java.util.stream.Collectors;

public class ITLeagueBettingDefiner implements LeagueBettingDefiner {
    @Override
    public List<LeagueDto> retieveAllowedBettingLeague(List<LeagueDto> allLeagues) {
        return allLeagues.stream()
                .filter(leagueDto -> leagueDto.getName().contains("Serie A") && !leagueDto.getName().contains("Women"))
                .collect(Collectors.toList());
    }
}
