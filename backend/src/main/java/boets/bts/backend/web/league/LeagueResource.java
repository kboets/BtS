package boets.bts.backend.web.league;

import boets.bts.backend.service.LeagueService;
import boets.bts.backend.web.dto.LeagueDto;
import boets.bts.backend.web.exception.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/")
public class LeagueResource {

    private LeagueService leagueService;

    public LeagueResource(LeagueService leagueService) {
        this.leagueService = leagueService;
    }


    @GetMapping("currentLeagueForCountry/{countryId}")
    public List<LeagueDto> getCurrentLeaguesForCountry(@PathVariable(name = "countryId", required = false) String countryId) {
        if(countryId == null) {
            countryId = "BE";
        }
        return leagueService.getCurrentSeasonLeaguesForCountry(countryId);
    }

    @GetMapping("getLeagueById/{leagueId}")
    public LeagueDto getLeagueById(@PathVariable(name = "leagueId") Long leagueId) {
         Optional<LeagueDto> leagueDtoOptional = leagueService.getLeagueById(leagueId);
         if(!leagueDtoOptional.isPresent()) {
             throw new NotFoundException(String.format("No league found with id %s", leagueId));
         }

         return leagueDtoOptional.get();
    }
}
