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
    public void getCurrentRoundForLeagueAndSeason_givenLeagueWithoutCurrentRound_shouldReturnCurrentRound() throws  Exception {
        Round currentRound = roundService.getCurrentRoundForLeagueAndSeason( 754L,219);
        assertThat(currentRound).isNotNull();
        assertThat(currentRound.isCurrent()).isTrue();
        //undo
        currentRound.setCurrent(false);
        currentRound.setCurrentDate(null);
    }
}