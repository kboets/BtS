package boets.bts.backend.web.round;

import boets.bts.backend.web.dto.RoundDto;
import boets.bts.backend.web.dto.TeamDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RoundClientIntegrationTest {

    @Autowired
    private RoundClient roundClient;

    @Test
    public void testgetAllRoundsForLeagueAndSeason_givenJupilerLeague_shouldReturn30Round() {
        Optional<List<RoundDto>> roundDtos = roundClient.getAllRoundsForLeagueAndSeason(2019, 656L);
        assertThat(roundDtos.isPresent()).isTrue();
        assertThat(roundDtos.get().size()).isEqualTo(30);
    }


}