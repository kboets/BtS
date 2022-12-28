package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.algorithm.AlgorithmSpecs;
import boets.bts.backend.repository.forecast.ForecastRepository;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.forecast2.validator.AllDataUpdatedRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
@Sql(scripts = "/boets/bts/backend/service/forecast/calculator/valid-be-league.sql")
@Transactional
public class ForecastCalculatorManagerTest {

    @Autowired
    private AlgorithmRepository algorithmRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private ForecastRepository forecastRepository;
    @Autowired
    private ForecastCalculatorManager forecastCalculatorManager;
    @Autowired
    private AdminService adminService;
    private League league;
    private Algorithm algorithm;

    @Before
    public void init() {
        adminService.executeAdmin(AdminKeys.CRON_RESULTS, "OK");
        adminService.executeAdmin(AdminKeys.CRON_ROUNDS, "OK");
        adminService.executeAdmin(AdminKeys.CRON_STANDINGS, "OK");
        // Jupiler pro league
        league = leagueRepository.getById(4366L);
        algorithm = algorithmRepository.findAll(AlgorithmSpecs.current()).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Could not find current algorithm"));
    }

    @Test
    public void leagueAlreadyCalculated_givenNotCalculated_shouldReturnForecast() {
        Optional<Forecast> forecastOptional = forecastCalculatorManager.leagueAlreadyCalculated(league, 7, algorithm);
        assertThat(forecastOptional.isPresent()).isTrue();
        assertThat(forecastOptional.get().getRound()).isEqualTo(7);
    }

    @Test
    public void leagueAlreadyCalculated_givenCorrectCalculated_shouldReturnNoForecast() {
        Forecast forecast = new Forecast(league, 7, algorithm);
        forecast.setForecastResult(ForecastResult.OK);
        forecastRepository.save(forecast);
        assertThat(forecastRepository.findAll().size()).isEqualTo(1);
        Optional<Forecast> forecastOptional = forecastCalculatorManager.leagueAlreadyCalculated(league, 7, algorithm);
        assertThat(forecastOptional.isPresent()).isFalse();
    }

    @Test
    public void leagueAlreadyCalculated_givenWrongCalculated_shouldReturnNoForecast() {
        Forecast forecast = new Forecast(league, 7, algorithm);
        forecast.setForecastResult(ForecastResult.WARNING);
        forecastRepository.save(forecast);
        assertThat(forecastRepository.findAll().size()).isEqualTo(1);
        Optional<Forecast> forecastOptional = forecastCalculatorManager.leagueAlreadyCalculated(league, 7, algorithm);
        assertThat(forecastOptional.isPresent()).isTrue();
        assertThat(forecastOptional.get().getLeague().getName()).isEqualTo(league.getName());
    }

    @Test
    public void validateLeague_givenNotAllValid_shouldReturnForecastWithErrorMessage() {
        adminService.executeAdmin(AdminKeys.CRON_RESULTS, "NOK");
        Forecast forecast = new Forecast(league, 7, algorithm);
        Forecast forecastValidated = forecastCalculatorManager.validateLeague(forecast);
        assertThat(forecastValidated.getForecastResult()).isEqualTo(ForecastResult.FATAL);
        assertThat(forecastValidated.getMessage()).isEqualTo(AllDataUpdatedRule.errorMessage);
    }

    @Test
    public void validateLeague_givenAllValid_shouldReturnForecastWithoutErrorMessage() {
        Forecast forecast = new Forecast(league, 7, algorithm);
        Forecast forecastValidated = forecastCalculatorManager.validateLeague(forecast);
        assertThat(forecastValidated.getForecastResult()).isNull();
    }


    @Test
    public void createForecastDetail_givenValidForecast_shouldReturnForecastWithDetails() {
        Forecast forecast = new Forecast(league, 7, algorithm);
        Forecast forecastWithDetails = forecastCalculatorManager.createForecastDetail(forecast);
        assertThat(forecastWithDetails.getForecastResult()).isNull();
        assertThat(forecastWithDetails.getForecastDetails().size()).isEqualTo(18);
        // check OH LEUVEN -> next game (=7th round) should be against anderlecht
        Optional<ForecastDetail> forecastDetailOpt = forecastWithDetails.getForecastDetails().stream()
                .filter(forecastDetail -> forecastDetail.getTeam().getName().equalsIgnoreCase("OH Leuven"))
                .findFirst();
        assertThat(forecastDetailOpt.isPresent()).isTrue();
        assertThat(forecastDetailOpt.get().getNextGame().getHomeTeam().getName()).isEqualTo("Anderlecht");
    }

    @Test
    public void calculateScore_givenValidForecast_shouldReturnForecastScore() {
        // update admin
        Forecast forecast = new Forecast(league, 7, algorithm);
        Forecast forecastWithDetails = forecastCalculatorManager.createForecastDetail(forecast);
        assertThat(forecastWithDetails.getForecastResult()).isNull();
        assertThat(forecastWithDetails.getForecastDetails().size()).isEqualTo(18);
        // check OH LEUVEN -> next game (=7th round) should be against anderlecht
        Optional<ForecastDetail> forecastDetailOpt = forecastWithDetails.getForecastDetails().stream()
                .filter(forecastDetail -> forecastDetail.getTeam().getName().equalsIgnoreCase("OH Leuven"))
                .findFirst();
        assertThat(forecastDetailOpt.isPresent()).isTrue();
        assertThat(forecastDetailOpt.get().getNextGame().getHomeTeam().getName()).isEqualTo("Anderlecht");
    }
}