package boets.bts.backend.web.standing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/standing/")
public class StandingResource {

    private Logger logger = LoggerFactory.getLogger(StandingResource.class);

    private StandingClient standingClient;

    public StandingResource(StandingClient standingClient) {
        this.standingClient = standingClient;
    }

    @GetMapping("/league/{league_id}")
    public List<StandingDto> getAllStandingForLeague(@PathVariable("league_id") Long league_id) {
        //TODO foresee a service who checks if the latest stand is in db, otherwise retrieve via client
        logger.info("Arrived in the getAllStandingForLeague {}", league_id);
        List<StandingDto> standingDtos = standingClient.getLatestStandForLeague(league_id.toString()).orElse(Collections.emptyList());
        logger.info("Standings found {}", standingDtos.size());
        return standingDtos;
    }
}
