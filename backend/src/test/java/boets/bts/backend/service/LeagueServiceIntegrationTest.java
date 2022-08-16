package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.web.league.LeagueDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("mock")
public class LeagueServiceIntegrationTest {
    @Autowired
    private LeagueService leagueService;

    @Test
    public void testGetLeagues_shouldReturnOneLeagueForBelgian() {
        List<LeagueDto> result = leagueService.getCurrentLeagues();
        //test for Belgian
        List<LeagueDto> belgianLeagues = result.stream()
                .filter(leagueDto -> leagueDto.getCountryCode().equals("BE"))
                .collect(Collectors.toList());
        assertThat(belgianLeagues.size()).isEqualTo(1);
    }

    @Test
    public void testUpdateAvailableOrSelectable_givenJupilerLeague() {
        List<Long> availableLeagueIds = new ArrayList<>();
        League jupilerLeague = leagueService.getLeagueById(2660L);
        //assert setup
        assertThat(jupilerLeague).isNotNull();
        assertThat(jupilerLeague.isSelected()).isFalse();
        availableLeagueIds.add(jupilerLeague.getId());
        //do test
        leagueService.updateLeagueAvailableOrSelectable(availableLeagueIds, true);
        jupilerLeague = leagueService.getLeagueById(2660L);
        //assert test
        assertThat(jupilerLeague.isSelected()).isTrue();
    }


    @Test
    public void testGetCurrentLeagues_shouldAddRoundsAndTeams() {
        //test
        List<LeagueDto> currentLeagues = leagueService.getCurrentLeagues();
        assertThat(currentLeagues.size()).isEqualTo(1);
        assertThat(currentLeagues.get(0).getRoundDtos().size()).isGreaterThan(0) ;
        assertThat(currentLeagues.get(0).getTeamDtos().size()).isGreaterThan(0);
    }


}