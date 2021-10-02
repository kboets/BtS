package boets.bts.backend.service.forecast.calculator;

import boets.bts.backend.domain.Admin;
import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.forecast.Forecast;
import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.service.forecast.calculator.ForecastCalculatorManager;
import boets.bts.backend.service.forecast.calculator.ForecastData;
import boets.bts.backend.service.round.RoundService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("integration")
@Transactional
public class ForecastCalculatorManagerTest {

    @Autowired
    private AdminService adminService;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private RoundService roundService;
    @Autowired
    private ForecastCalculatorManager forecastCalculatorManager;

    private Admin adminSeason2020;
    private Admin getAdminSeason2021;
    private List<League> leagues;

    @Before
    public void init() {
//        adminSeason2020 = new Admin(AdminKeys.SEASON);
//        adminSeason2020.setDate(LocalDateTime.now());
//        adminSeason2020.setValue("2020");
        leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(2021));
        //adminService.updateAdmin(adminSeason2020);
    }

    @Test
    public void isLeagueReadyToForecast_givenSeason2020JupilerLeague_shouldReturnTrue() {
        //setup
        League jupilerLeague = leagues.stream().filter(league -> league.getId().equals(2660L)).findFirst().orElseThrow(() -> new IllegalStateException(String.format("Could not retrieve Jupiler League with id %s", 2260)));
        setUpLeaguesWithRound(new ArrayList<>(Collections.singletonList(jupilerLeague)), 10);
        assertThat(forecastCalculatorManager.isLeagueReadyToForecast(jupilerLeague)).isTrue();
    }

    @Test
    public void retrieveForecastData_givenSeason2020JupilerLeague_shouldReturnForecastData() {
        //setup
        League jupilerLeague = leagues.stream().filter(league -> league.getId().equals(2660L)).findFirst().orElseThrow(() -> new IllegalStateException(String.format("Could not retrieve Jupiler League with id %s", 2260)));
        setUpLeaguesWithRound(new ArrayList<>(Collections.singletonList(jupilerLeague)), 10);
        ForecastData forecastData = forecastCalculatorManager.retrieveForecastData(jupilerLeague);
        assertThat(forecastData.getLeague().getName().equals(jupilerLeague.getName()));
        assertThat(forecastData.getCurrentRound().getRoundNumber() == 10);
        assertThat(forecastData.getFinishedResults().size() == (9*10));
        assertThat(forecastData.getNextResults().size() == 9);
    }

    @Test
    public void retrieveForecast_givenSeason2020JupilerLeagueForecastData_shouldReturnForecast() {
        //setup
        League jupilerLeague = leagues.stream().filter(league -> league.getId().equals(2660L)).findFirst().orElseThrow(() -> new IllegalStateException(String.format("Could not retrieve Jupiler League with id %s", 2260)));
        setUpLeaguesWithRound(new ArrayList<>(Collections.singletonList(jupilerLeague)), 11);
        ForecastData forecastData = forecastCalculatorManager.retrieveForecastData(jupilerLeague);
        assertThat(forecastData.getLeague().getName().equals(jupilerLeague.getName())).isTrue();
        //test forecast creation
        ForecastContainer forecastContainer = forecastCalculatorManager.calculateForecast(forecastData);
        Forecast forecast = forecastContainer.getForecast();
        assertThat(forecast.getLeague().getLeague_id().equals(jupilerLeague.getId().toString())).isTrue();
        assertThat(forecast.getForecastDetails().size() == 18).isTrue();//number of teams
        ForecastDetail forecastDetail = forecast.getForecastDetails().get(0);
        assertThat(forecastDetail.getResults().size()).isEqualTo(10);
        assertThat(forecastDetail.getNextResult().getRoundNumber() == 12).isTrue();
    }

    @Test
    public void calculateForecasts_givenTwoLeagues_shouldReturnTwoForecasts() throws Exception {
        List<League> requestedLeagues = new ArrayList<>();
        //setup
        League jupilerLeague = leagues.stream().filter(league -> league.getId().equals(2660L)).findFirst().orElseThrow(() -> new IllegalStateException(String.format("Could not retrieve Jupiler League with id %s", 2260)));
        setUpLeaguesWithRound(new ArrayList<>(Collections.singletonList(jupilerLeague)), 11);
        League league1 = leagues.stream().filter(league -> league.getId().equals(2664L)).findFirst().orElseThrow(() -> new IllegalStateException(String.format("Could not retrieve Jupiler League with id %s", 2260)));
        setUpLeaguesWithRound(new ArrayList<>(Collections.singletonList(league1)), 11);
        requestedLeagues.add(jupilerLeague);
        requestedLeagues.add(league1);
        List<Forecast> forecasts = forecastCalculatorManager.calculateForecasts(requestedLeagues);
        assertThat(forecasts.size()).isEqualTo(2);

    }
    @Test
    public void test() throws Exception {
        List<League> requestedLeagues = new ArrayList<>();
        League league1 = leagues.stream().filter(league -> league.getId().equals(3506L)).findFirst().orElseThrow(() -> new IllegalStateException(String.format("Could not retrieve League 1 with id %s", 3506)));
        requestedLeagues.add(league1);
        List<Forecast> forecasts = forecastCalculatorManager.calculateForecasts(requestedLeagues);
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