package boets.bts.backend.service.standing;

import boets.bts.backend.domain.Standing;
import boets.bts.backend.web.standing.StandingDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("integration")
@Transactional
public class StandingServiceIntegrationTest {

    @Autowired
    private StandingService standingService;

    @Test
    public void getStandingsForLeagueByRound_givenJupilerLeague2020_Round19_shouldReturnCorrectStanding() {
        List<Standing> standings = standingService.getStandingsForLeagueByRound(2660L, 2020, 19);
        assertThat(standings.isEmpty()).isFalse();
    }

    @Test
    public void getCurrentStandingForLeague() {
        List<StandingDto> standings = standingService.getCurrentStandingForLeague(3450L);
        assertThat(standings.isEmpty()).isFalse();
    }

}