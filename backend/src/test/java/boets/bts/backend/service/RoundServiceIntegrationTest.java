package boets.bts.backend.service;

import boets.bts.backend.domain.Round;
import boets.bts.backend.service.round.RoundService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RoundServiceIntegrationTest {

    @Autowired
    private RoundService roundService;

    @Test
    public void testGetCurrentRoundForLeagueAndSeason_givenJupilerLeague2019_shouldReturnRound() throws  Exception {
        Round currentRound = roundService.getCurrentRoundForLeagueAndSeason( 656L,219);
        assertThat(currentRound).isNotNull();
        assertThat(currentRound.isCurrent()).isTrue();
    }
}