package boets.bts.backend.web.standing;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@Ignore
@SpringBootTest
@RunWith(SpringRunner.class)
public class StandingClientIntegrationTest {

    @Autowired
    private StandingClient standingClient;

    @Test
    public void testRetrieveLatestStanding_givenJupilerLeague_shouldReturnList() {
        String jupilerLeagueId = "656";
        Optional<List<StandingDto>> latestStandForLeague = standingClient.getLatestStandForLeague(jupilerLeagueId);
        assertThat(latestStandForLeague.isPresent()).isTrue();
        assertThat(latestStandForLeague.get().size()).isEqualTo(16);
    }
}
