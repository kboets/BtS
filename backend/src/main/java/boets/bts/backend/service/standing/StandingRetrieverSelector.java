package boets.bts.backend.service.standing;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.service.standing.retriever.StandingRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StandingRetrieverSelector {

    private List<StandingRetriever> standingRetrieverList;

    @Autowired
    public StandingRetrieverSelector(List<StandingRetriever> standingRetrieverList) {
        this.standingRetrieverList = standingRetrieverList;
    }

    public Optional<StandingRetriever> select(League league, Round currentRound, int roundNumber) {
        return standingRetrieverList.stream().filter(standingRetriever1 -> standingRetriever1.accept(league,currentRound,roundNumber)).findFirst();
    }
}
