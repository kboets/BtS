package boets.bts.backend.web.standing;

import boets.bts.backend.service.standing.StandingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/standing/")
public class StandingResource {

    private Logger logger = LoggerFactory.getLogger(StandingResource.class);

    private final StandingService standingService;

    public StandingResource(StandingService standingService) {
        this.standingService = standingService;
    }

    @GetMapping("/league/{league_id}")
    public List<StandingDto> getAllStandingForLeague(@PathVariable("league_id") Long league_id) {
        List<StandingDto> standingDtos = standingService.getCurrentStandingForLeague(league_id);
        logger.info("Standings found {}", standingDtos.size());
        return standingDtos;
    }

    @GetMapping("/league/{league_id}/{roundNumber}")
    public List<StandingDto> getStandingRoundAndLeague(@PathVariable("league_id") Long league_id, @PathVariable("roundNumber") Integer roundNumber) {
        List<StandingDto> standingDtos = standingService.getStandingRoundAndLeague(league_id, roundNumber);
        logger.info("Standing for round {} and league {} found ", roundNumber, league_id);
        return standingDtos;
    }
}
