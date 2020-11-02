package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;

import java.util.List;

/**
 * Defines
 */
public interface ResultHandler {

    List<Result> getResultForLeague(Long leagueId) throws Exception;
}
