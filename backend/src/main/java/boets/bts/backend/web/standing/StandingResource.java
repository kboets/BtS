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

    @GetMapping("/league/{league_id}")
    public List<StandingDto> getAllStandingForLeague(@PathVariable("league_id") String league_id) {


        return Collections.emptyList();
    }
}
