package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.web.league.LeagueDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LeagueServiceIntegrationTest {


    @Autowired
    private LeagueService leagueService;


    @Test
    public void testCurrentSeasonLeaguesForCountry_given3Leagues_shouldOnlyReturnBettingAvailableLeagues() {
        List<LeagueDto> result = leagueService.getLeaguesForCurrentSeasonForCountry("BE");
        //assert
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.stream().anyMatch(LeagueDto::isCurrent));
    }

    @Test
    public void testGetLeaguesForCountryAndSeason_given2018_shouldReturnOneResult() {
        List<League> result = leagueService.getLeaguesForCountryAndSeason("BE", 2018);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testGetLeaguesForCountryAndSeason_given2019_shouldReturnThreeLeagues() {
        List<League> result = leagueService.getLeaguesForCountryAndSeason("BE", 2019);
        assertThat(result.size()).isEqualTo(3);
    }

//    @Test
//    public void testGetLeagues_shouldReturnTwoLeaguesForBelgian() {
//        List<LeagueDto> result = leagueService.getLeaguesForCountryAndSeason();
//        //test for Belgian
//        List<LeagueDto> belgianLeagues = result.stream()
//                .filter(leagueDto -> leagueDto.getCountryCode().equals("BE"))
//                .collect(Collectors.toList());
//        assertThat(belgianLeagues.size()).isEqualTo(2);
//    }


}