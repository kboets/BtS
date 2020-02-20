package boets.bts.backend.web.league;

import boets.bts.backend.web.dto.LeagueDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LeagueClientIntegrationTest {

    @Autowired
    private LeagueClient leagueClient;

    @Test
    public void testRetrieveAllLeagues_givenCorrectFile_shouldReturnDto() {
        List<LeagueDto> leagues = leagueClient.retrieveAllLeagues("BE", 2019);
        assertThat(leagues.size()).isEqualTo(18);
    }
}