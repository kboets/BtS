package boets.bts.backend.web.league;

import boets.bts.backend.domain.League;
import boets.bts.backend.service.LeagueService;
import boets.bts.backend.web.exception.GeneralException;
import boets.bts.backend.web.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/league/")
public class LeagueResource {

    private Logger logger = LoggerFactory.getLogger(LeagueResource.class);
    private LeagueService leagueService;

    public LeagueResource(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping("leaguesCurrentSeason")
    public List<LeagueDto> getLeaguesCurrentSeason() {
        return leagueService.getCurrentLeagues();
    }

    @GetMapping("get/{id}")
    public LeagueDto getLeagueById(@PathVariable("id") Long id)  {
        logger.info("GetLeagueById with id {} ",id);

        try {
            Optional<LeagueDto> leagueDto = leagueService.getLeagueDtoById(id);
            if(!leagueDto.isPresent()) {
                throw new NotFoundException(String.format("No league found with id %s", id));
            }
            return leagueDto.get();
        } catch (Exception e) {
            throw new GeneralException(e.getMessage());
        }


    }

    @PutMapping("/toSelected")
    public List<LeagueDto> updateToSelectedLeagues(@RequestBody List<String> leagueIds) {
        List<Long> ids = leagueIds.stream().map(Long::parseLong).collect(Collectors.toList());
        return leagueService.updateLeagueAvailableOrSelectable(ids, true);
    }

    @PutMapping("/toAvailable")
    public List<LeagueDto> updateToAvailableLeagues(@RequestBody List<String> leagueIds) {
        List<Long> ids = leagueIds.stream().map(Long::parseLong).collect(Collectors.toList());
        return leagueService.updateLeagueAvailableOrSelectable(ids, false);
    }

}
