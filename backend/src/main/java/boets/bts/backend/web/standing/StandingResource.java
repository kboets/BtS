package boets.bts.backend.web.standing;

import boets.bts.backend.service.StandingService;
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

    private StandingService standingService;

    public StandingResource(StandingService standingService) {
        this.standingService = standingService;
    }

    @GetMapping("/league/{league_id}")
    public List<StandingDto> getAllStandingForLeague(@PathVariable("league_id") Long league_id) {
        List<StandingDto> standingDtos = standingService.getStandingsForLeague(league_id);
        logger.info("Standings found {}", standingDtos.size());
        return standingDtos;
    }
}
