package boets.bts.backend.service.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;

import java.util.Optional;

public interface CurrentRoundHandler {

    boolean accept(Optional<Round> roundOptional);

    Round save(Round round, League league, int season);

}
