package boets.bts.backend.service.forecast2;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Team;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.algorithm.AlgorithmSpecs;
import boets.bts.backend.repository.league.LeagueRepository;

import boets.bts.backend.repository.result.ResultRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
@Transactional
public class ForecastService2Test {

    @Autowired
    private AlgorithmRepository algorithmRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private ForecastService2 forecastService2;
    @Autowired
    private ResultRepository resultRepository;

//    @Test
//    @Sql(scripts = "/boets/bts/backend/service/forecast/calculator/valid-be-league.sql")
//    public void calculateForecast_givenNewValidForecast_shouldPersistForecast() {
//        // Jupiler pro league
//        League league = leagueRepository.getById(4366L);
//        Algorithm algorithm = algorithmRepository.findAll(AlgorithmSpecs.current()).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Could not find current algorithm"));
//        Forecast forecast = forecastService2.calculateForecast(league, 7, algorithm);
//        assertThat(forecast).isNotNull();
//        assertThat(forecast.getForecastResult()).isNull();
//        assertThat(forecast.getForecastDetails().size()).isEqualTo(18);
//    }
}