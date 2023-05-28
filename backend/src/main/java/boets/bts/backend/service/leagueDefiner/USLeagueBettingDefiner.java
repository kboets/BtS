package boets.bts.backend.service.leagueDefiner;

import boets.bts.backend.domain.League;

import java.util.List;
import java.util.stream.Collectors;

public class USLeagueBettingDefiner implements LeagueBettingDefiner {
    @Override
    public List<League> retrieveAllowedBettingLeague(List<League> allLeagues) {
        return allLeagues.stream()
                .filter(league -> league.getName().contains("Major League Soccer") && !league.getName().contains("Women") )
                .collect(Collectors.toList());
    }
}
