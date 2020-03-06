package boets.bts.backend.service.leagueDefiner;

import boets.bts.backend.web.league.LeagueDto;

import java.util.List;
import java.util.stream.Collectors;

public class FRLeagueBettingDefiner implements LeagueBettingDefiner {
    @Override
    public List<LeagueDto> retieveAllowedBettingLeague(List<LeagueDto> allLeagues) {
        return allLeagues.stream()
                .filter(leagueDto -> leagueDto.getName().contains("Ligue 1"))
                .collect(Collectors.toList());
    }
}
