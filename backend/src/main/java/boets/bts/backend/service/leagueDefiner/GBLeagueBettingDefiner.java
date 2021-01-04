package boets.bts.backend.service.leagueDefiner;

import boets.bts.backend.domain.League;
import boets.bts.backend.web.league.LeagueDto;

import java.util.List;
import java.util.stream.Collectors;

public class GBLeagueBettingDefiner implements LeagueBettingDefiner {

    @Override
    public List<League> retrieveAllowedBettingLeague(List<League> allLeagues) {
        return allLeagues.stream()
                .filter(league -> league.getName().startsWith("Premier League") && !league.getName().contains("Women"))
                .collect(Collectors.toList());
    }
}
