package boets.bts.backend.service.round;

import boets.bts.backend.domain.Round;

public interface CurrentRoundHandler {

    Round save(Round round, long leagueId, int season) throws Exception;
}
