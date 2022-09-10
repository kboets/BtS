package boets.bts.backend.service.forecast2.validator;

import boets.bts.backend.domain.Forecast;
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
public class ForecastValidatorTest {

    @Autowired
    public ForecastValidator validator;
    private Forecast forecast;

    @Before
    public void init() {
        forecast = new Forecast();
    }
    @Ignore // ignored as admin data needs to be updated to make it work
    @Test
    @Sql(scripts = {"/boets/bts/backend/service/forecast/validator/admin-data.sql"})
    public void givenValidSituation_shouldReturnTrue() {
        assertThat(validator.validate(forecast)).isTrue();
    }
}