package boets.bts.backend.service.standing.retriever;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.domain.Standing;

import java.util.List;

public interface StandingRetriever {

    boolean accept(League league, Round currentRound, int roundNumber);

    List<Standing> getStanding(League league, Round currentRound, int roundNumber);
}
