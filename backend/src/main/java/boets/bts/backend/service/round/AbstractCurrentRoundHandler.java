package boets.bts.backend.service.round;

import boets.bts.backend.domain.Round;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.round.IRoundClient;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public abstract class AbstractCurrentRoundHandler implements CurrentRoundHandler {

    private IRoundClient roundClient;
    private RoundMapper roundMapper;

    public AbstractCurrentRoundHandler(IRoundClient roundClient, RoundMapper roundMapper) {
        this.roundClient = roundClient;
        this.roundMapper = roundMapper;
    }

    protected Optional<Round> getCurrentClientRound(long leagueId, int season)  {
        Optional<RoundDto> currentRoundOptional = roundClient.getCurrentRoundForLeagueAndSeason(leagueId, season);
        if (currentRoundOptional.isPresent()) {
            RoundDto roundDto = currentRoundOptional.get();
            return Optional.of(roundMapper.toRound(roundDto));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Checks if today is not a friday, saterday or sunday. In that case, the clientRound is not the current round, but the next round.
     * So it retrieves the previous round.
     * @param clientRound
     * @return
     */
    protected Round verifyRetrievedRound(Round clientRound) {
        if (!WebUtils.isWeekend())  {
            String round = clientRound.getRound();
            String strippedRound = StringUtils.substringBeforeLast(round, "_");
            int roundVal = Integer.parseInt(StringUtils.substringAfterLast(round, "_"));
            if(roundVal != 1) {
                String previous = Integer.toString(roundVal-1);
                String builder = strippedRound +
                        "_" +
                        previous;
                clientRound.setRound(builder);
            }
        }
        return clientRound;
    }

    protected Round getLastRound(Set<Round> rounds) {
        List<Round> allRounds = new ArrayList<>(rounds);
        allRounds.sort(Comparator.comparing(Round::getRoundNumber));
        return allRounds.get(allRounds.size()-1);
    }


}
