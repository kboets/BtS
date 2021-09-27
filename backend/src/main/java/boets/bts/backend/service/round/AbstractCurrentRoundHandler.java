package boets.bts.backend.service.round;

import boets.bts.backend.domain.Round;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.round.IRoundClient;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public abstract class AbstractCurrentRoundHandler implements CurrentRoundHandler {

    private IRoundClient roundClient;
    private RoundMapper roundMapper;

    public AbstractCurrentRoundHandler(IRoundClient roundClient, RoundMapper roundMapper) {
        this.roundClient = roundClient;
        this.roundMapper = roundMapper;
    }

    protected Round getCurrentClientRound(long leagueId, int season)  {
        Optional<RoundDto> currentRoundOptional = roundClient.getCurrentRoundForLeagueAndSeason(leagueId, season);
        RoundDto roundDto = currentRoundOptional.orElseThrow(() -> new NotFoundException(String.format("Could not find current round for league %s from season %s", leagueId, season)));
        return roundMapper.toRound(roundDto);
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
                StringBuilder builder = new StringBuilder();
                builder.append(strippedRound);
                builder.append("_");
                builder.append(previous);
                clientRound.setRound(builder.toString());
            }
        }
        return clientRound;
    }


}
