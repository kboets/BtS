package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.team.TeamSpecs;
import boets.bts.backend.web.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines
 */
public interface ResultHandler {

    List<Result> getResultForLeague(Long leagueId) throws Exception;


}
