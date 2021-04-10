package boets.bts.backend.service.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.team.TeamSpecs;
import boets.bts.backend.web.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines
 */
public interface ResultHandler {

    boolean accepts(List<Result> allResults);

    List<Result> getResult(League league) throws Exception;


}
