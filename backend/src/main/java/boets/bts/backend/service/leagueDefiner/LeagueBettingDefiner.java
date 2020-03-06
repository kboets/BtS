package boets.bts.backend.service.leagueDefiner;

import boets.bts.backend.domain.League;
import boets.bts.backend.web.league.LeagueDto;

import java.util.List;

public interface LeagueBettingDefiner {

    /**
     * Retrieves the allowed leagues available for betting purpose
     * @param allLeagues - all leagues available
     * @return a list of Leagues allowed for betting purpose.
     */
    public List<LeagueDto> retieveAllowedBettingLeague(List<LeagueDto> allLeagues);
}
