package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.web.league.LeagueDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LeagueServiceIntegrationTest {


    @Autowired
    private LeagueService leagueService;


    @Test
    public void testGetLeagues_shouldReturnTwoLeaguesForBelgian() {
        List<LeagueDto> result = leagueService.getCurrentLeagues();
        //test for Belgian
        List<LeagueDto> belgianLeagues = result.stream()
                .filter(leagueDto -> leagueDto.getCountryCode().equals("BE"))
                .collect(Collectors.toList());
        assertThat(belgianLeagues.size()).isEqualTo(2);
    }

    @Test
    public void testUpdateAvailableOrSelectable_givenJupilerLeague() {
        List<Long> availableLeagueIds = new ArrayList<>();
        League jupilerLeague = leagueService.getLeagueById(656L);
        //assert setup
        assertThat(jupilerLeague).isNotNull();
        assertThat(jupilerLeague.isSelected()).isFalse();
        availableLeagueIds.add(jupilerLeague.getId());
        //do test
        leagueService.updateLeagueAvailableOrSelectable(availableLeagueIds, true);
        jupilerLeague = leagueService.getLeagueById(656L);
        //assert test
        assertThat(jupilerLeague.isSelected()).isTrue();
        //reset to original value
        leagueService.updateLeagueAvailableOrSelectable(availableLeagueIds, false);
    }


    @Test
    public void testGetCurrentLeagues_shouldAddRoundsAndTeams() {
        //test
        List<LeagueDto> currentLeagues = leagueService.getCurrentLeagues();
        assertThat(currentLeagues.size()).isEqualTo(9);
        assertThat(currentLeagues.get(0).getRoundDtos().size()).isGreaterThan(0) ;
        assertThat(currentLeagues.get(0).getTeamDtos().size()).isGreaterThan(0);
    }


}