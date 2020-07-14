package boets.bts.backend.web.league;

import boets.bts.backend.service.LeagueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return leagueService.getLeagueDtoById(id);
    }

    @PutMapping("/toSelected")
    public void updateToSelectedLeagues(@RequestBody List<Long> leagueIds) {
        leagueService.updateLeagueAvailableOrSelectable(leagueIds, true);
    }

    @PutMapping("/toAvailable")
    public void updateToAvailableLeagues(@RequestBody List<Long> leagueIds) {
        leagueService.updateLeagueAvailableOrSelectable(leagueIds, false);
    }

}
