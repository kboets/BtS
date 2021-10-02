package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.Admin;
import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.LeagueService;
import boets.bts.backend.service.round.RoundService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("integration")
@Transactional
public class ForecastServiceIntegrationTest {

    @Autowired
    private ForecastService forecastService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private RoundService roundService;

    private Admin adminSeason2020;
    private List<League> leagues;

    @Before
    public void init() {
        adminSeason2020 = new Admin(AdminKeys.SEASON);
        adminSeason2020.setDate(LocalDateTime.now());
        adminSeason2020.setValue("2020");
        leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(2020));
    }

    @Test
    public void calculateForecast_givenSeason2020JupilerLeagueCurrentRound10_shouldReturnForecast() throws Exception {
        //setup
        adminService.updateAdmin(adminSeason2020);
        assertThat(adminService.getCurrentSeason()).isEqualTo(2020);
        //make sure Jupiler League is the only league to be able to retrieve forecasts by setting current round to 10, and 1 for the others..
        League jupilerLeague = leagues.stream().filter(league -> league.getId().equals(2660L)).findFirst().orElseThrow(() -> new IllegalStateException(String.format("Could not retrieve Jupiler League with id %s", 2260)));
        setUpLeaguesWithRound(new ArrayList<>(Collections.singletonList(jupilerLeague)), 10);
        List<League> otherLeagues = leagues.stream().filter(league -> !league.getId().equals(2660L)).collect(Collectors.toList());
        setUpLeaguesWithRound(otherLeagues, 1);
        // calculate forecast
        List<Forecast> forecasts = forecastService.calculateForecast();
        assertThat(forecasts.size()).isEqualTo(1);
    }

    public void setUpLeaguesWithRound(List<League> leagues, int roundNumber) {
        for(League league: leagues) {
            List<Round> rounds = roundService.getAllRoundsForLeague(league.getId());
            Round round = rounds.stream().filter(round1 -> round1.getRoundNumber() == roundNumber).findFirst().orElseGet(Round::new);
            roundService.updateCurrentRound(round.getId(), league.getId());
        }
    }
}