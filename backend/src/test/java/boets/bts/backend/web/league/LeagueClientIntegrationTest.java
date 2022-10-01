package boets.bts.backend.web.league;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@Ignore
@SpringBootTest
@RunWith(SpringRunner.class)
public class LeagueClientIntegrationTest {

    @Autowired
    private LeagueClient leagueClient;

    @Test
    public void testRetrieveAllLeaguesFromCountry_givenCorrectFile_shouldReturnDto() {
        List<LeagueDto> leagues = leagueClient.allLeaguesFromCountryForSeason("BE", 2019);
        assertThat(leagues.size()).isGreaterThan(1);
    }

    @Test
    public void testRetrieveAllLeagues_givenCorrectYear_shouldReturnDto() {
        List<LeagueDto> leagues = leagueClient.allLeaguesForSeason(2019);
        assertThat(leagues.size()).isGreaterThan(1);
    }


}