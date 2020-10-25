package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.results.ResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Retrieves all result until the current round.  
 */
@Component
public class NonEmptyResultHandler implements ResultHandler {

    private ResultMapper resultMapper;
    private RoundService roundService;
    private Logger logger = LoggerFactory.getLogger(NonEmptyResultHandler.class);

    @Override
    public List<Result> getResultForLeague(Long leagueId) {
        try {
            Round currentRound = roundService.getCurrentRoundForLeagueAndSeason(leagueId, WebUtils.getCurrentSeason());

        } catch (Exception e) {
            logger.error("Could not retrieve current round for league with id {}", leagueId);
        }
        return null;
    }
}
