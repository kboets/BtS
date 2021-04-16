package boets.bts.backend.service.forecast;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("integration")
public class ForecastServiceIntegrationTest {

    @Autowired
    private ForecastService forecastService;

    @Test
    public void calculateForecasts() {
        forecastService.calculateForecasts();
    }

}