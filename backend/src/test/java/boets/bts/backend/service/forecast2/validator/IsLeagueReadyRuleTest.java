package boets.bts.backend.service.forecast2.validator;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.League;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("mock")
@Transactional
public class IsLeagueReadyRuleTest {

    @Autowired
    private IsLeagueReadyRule rule;
    private Forecast forecast;

    @Before
    public void setUp() {
        League league = new League();
        league.setId(4366L);
        forecast = new Forecast();
        forecast.setLeague(league);
    }

    @Test
    @Sql(scripts = {"/boets/bts/backend/service/forecast/validator/league-be.sql", "/boets/bts/backend/service/forecast/validator/result-be-valid.sql"})
    public void givenValidBELeague_shouldReturnTrue() {
        assertThat(rule.validate(forecast)).isTrue();
    }

    @Test
    @Sql(scripts = {"/boets/bts/backend/service/forecast/validator/league-be.sql", "/boets/bts/backend/service/forecast/validator/result-be-invalid.sql"})
    public void givenInValidBELeague_shouldReturnFalse() {
        assertThat(rule.validate(forecast)).isFalse();
    }
}