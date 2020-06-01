package boets.bts.backend.web.league;

import boets.bts.backend.service.LeagueService;
import boets.bts.backend.web.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/league/")
public class LeagueResource {

    private Logger logger = LoggerFactory.getLogger(LeagueResource.class);
    private LeagueService leagueService;

    public LeagueResource(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping("availableCurrentSeason")
    public List<LeagueDto> getCurrentAvailableLeagues() {
        return leagueService.getLeaguesCurrentSeason(false);
    }

    @GetMapping("selectedCurrentSeason")
    public List<LeagueDto> getCurrentSelectedLeagues() {
        return leagueService.getCurrentSelectedLeagues();
    }

    @GetMapping("get/{id}")
    public LeagueDto getLeagueById(@PathVariable("id") Long id) {
        logger.info("Arrived in the getLeagueById with {} ",id);
        return leagueService.getLeagueById(id);
    }



}
