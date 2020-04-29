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