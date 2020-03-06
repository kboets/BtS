package boets.bts.backend.service;

import boets.bts.backend.web.league.LeagueDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LeagueServiceIntegrationTest {


    @Autowired
    private LeagueService leagueService;


    @Test
    public void testCurrentSeasonLeaguesForCountry_given3Leagues_shouldOnlyReturnBettingAvailableLeagues() {
        List<LeagueDto> result = leagueService.getCurrentSeasonLeaguesForCountry("BE");
        //assert
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.stream().anyMatch(LeagueDto::isCurrent));
    }

    @Test
    public void testGetLeaguesForCountryAndSeason_given2018_shouldReturnOneResult() {
        List<LeagueDto> result = leagueService.getLeaguesForCountryAndSeason("BE", 2018);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testGetLeaguesForCountryAndSeason_given2019_shouldReturnThreeLeagues() {
        List<LeagueDto> result = leagueService.getLeaguesForCountryAndSeason("BE", 2019);
        assertThat(result.size()).isEqualTo(3);
    }


}