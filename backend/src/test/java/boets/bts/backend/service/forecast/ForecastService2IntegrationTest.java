package boets.bts.backend.service.forecast;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("integration")
@Transactional
public class ForecastService2IntegrationTest {

    @Autowired
    private ForecastService2 forecastService;

    @Test
    public void calculateForecast_givenLeagueInPast_shouldReturnForecast() {

    }
}