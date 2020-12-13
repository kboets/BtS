package boets.bts.backend.web.round;

import boets.bts.backend.domain.Round;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.standing.StandingResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/round/")
public class RoundResource {

    private Logger logger = LoggerFactory.getLogger(RoundResource.class);

    private RoundService roundService;
    private RoundMapper roundMapper;

    public RoundResource(RoundService roundService, RoundMapper roundMapper) {
        this.roundService = roundService;
        this.roundMapper = roundMapper;
    }

    @GetMapping("/current/{league_id}")
    public RoundDto getCurrentRoundForLeague(@PathVariable("league_id") Long league_id) {
        Round currentRound = roundService.getCurrentRoundForLeague(league_id);
        if(currentRound == null) {
            throw new NotFoundException("Could not found current round for league "+league_id);
        }
        return roundMapper.toRoundDto(currentRound);

    }
}
