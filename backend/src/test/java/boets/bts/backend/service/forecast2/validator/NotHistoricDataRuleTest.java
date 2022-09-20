package boets.bts.backend.service.forecast2.validator;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastResult;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("mock")
public class NotHistoricDataRuleTest {

    @Autowired
    private NotHistoricDataRule rule;
    private Forecast forecast;

    @Before
    public void init() {
        forecast = new Forecast();
    }
    @Ignore //ignored because the data should be adapted to make test work
    @Test
    @Sql(scripts = "/boets/bts/backend/service/forecast/validator/admin-data.sql")
    public void givenNonHistoricData_shouldReturnTrue() {
        assertThat(rule.validate(forecast)).isTrue();
    }

    @Test
    @Sql(scripts = "/boets/bts/backend/service/forecast/validator/admin-historic-data.sql")
    public void givenNonHistoricData_shouldReturnFalse() {
        assertThat(rule.validate(forecast)).isFalse();
        assertThat(forecast.getForecastResult()).isEqualTo(ForecastResult.FATAL);
        assertThat(forecast.getMessage()).isEqualTo(NotHistoricDataRule.errorMessage);
    }
}