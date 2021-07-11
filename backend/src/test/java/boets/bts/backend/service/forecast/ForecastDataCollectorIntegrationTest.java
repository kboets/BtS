package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.League;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Results depends on current situation.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@ActiveProfiles("integration")
public class ForecastDataCollectorIntegrationTest {

    @Autowired
    public ForecastDataCollector forecastDataCollector;

    @Test
    public void retrieveAllowedLeagues_given2019_shouldReturnLeague() {
        List<League> leagues = forecastDataCollector.retrieveQualifiedLeagues(2019);
        assertThat(leagues.isEmpty()).isFalse();
        //TODO add more assertions
    }

    @Test
    public void collectForecastData_givenLeagues_shouldReturnCorrectForecastData() {
        List<ForecastData> forecastDataList = forecastDataCollector.collectForecastData();
        forecastDataList.stream()
                .peek(forecastData -> {
                   assertThat(forecastData.getCurrentRound().getRound()).isEqualTo(forecastData.getCurrentRound());
                });
    }


}