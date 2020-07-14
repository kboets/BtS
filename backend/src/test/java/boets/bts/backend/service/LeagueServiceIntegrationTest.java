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
        List<LeagueDto> result = leagueService.getLeaguesCurrentSeason(false);
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
    public void testGetCurrentSelectedLeagues_givenJupilerLeague_shouldAddRoundsAndTeams() {
        //setup
        List<Long> availableLeagueIds = new ArrayList<>();
        League jupilerLeague = leagueService.getLeagueById(656L);
        availableLeagueIds.add(jupilerLeague.getId());
        leagueService.updateLeagueAvailableOrSelectable(availableLeagueIds, true);
        //test
        List<LeagueDto> currentSelectedLeagues = leagueService.getCurrentSelectedLeagues();
        assertThat(currentSelectedLeagues.size()).isEqualTo(1);
        assertThat(currentSelectedLeagues.get(0).getLeague_id()).isEqualTo("656");

        //reset test
        leagueService.updateLeagueAvailableOrSelectable(availableLeagueIds, false);
    }


}