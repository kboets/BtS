package boets.bts.backend.service;

import boets.bts.backend.domain.Round;
import boets.bts.backend.service.round.RoundService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
//@ActiveProfiles("mock")
public class RoundServiceIntegrationTest {

    @Autowired
    private RoundService roundService;

    @Test
    public void getCurrentRoundForLeagueAndSeason_givenLeagueWithoutCurrentRound_shouldReturnCurrentRound() throws  Exception {
        Round currentRound = roundService.retrieveCurrentRoundForLeagueAndSeason( 754L,219);
        assertThat(currentRound).isNotNull();
        assertThat(currentRound.isCurrent()).isTrue();
    }

    @Test
    public void getPreviousRoundForLeague_givenJupilerLeague_shouldReturnPreviousRound()  throws Exception {
        Round currentRound = roundService.retrieveCurrentRoundForLeagueAndSeason( 2660L,2020);
        assertThat(currentRound).isNotNull();
        assertThat(currentRound.isCurrent()).isTrue();
        Round previousCurrentRound = roundService.getPreviousCurrentRoundForLeague(currentRound.getLeague().getId());
        assertThat(previousCurrentRound.getId()).isEqualTo(currentRound.getId()-1);
    }
}