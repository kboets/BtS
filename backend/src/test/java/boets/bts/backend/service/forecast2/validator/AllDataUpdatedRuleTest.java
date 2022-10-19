package boets.bts.backend.service.forecast2.validator;

import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.service.AdminService;
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
public class AllDataUpdatedRuleTest {

    @Autowired
    public AllDataUpdatedRule rule;
    @Autowired
    private AdminService adminService;
    private Forecast forecast;

    @Before
    public void init() {
        forecast = new Forecast();
    }

    @Test
    @Sql(scripts = "/boets/bts/backend/service/forecast/validator/admin-data.sql")
    public void givenTodayUpdated_shouldReturnTrue() {
        // update admin
        adminService.executeAdmin(AdminKeys.CRON_RESULTS, "OK");
        adminService.executeAdmin(AdminKeys.CRON_ROUNDS, "OK");
        adminService.executeAdmin(AdminKeys.CRON_STANDINGS, "OK");
        assertThat(rule.validate(forecast)).isTrue();
    }

    @Test
    @Sql(scripts = "/boets/bts/backend/service/forecast/validator/admin-historic-data.sql")
    public void givenTodayNotUpdated_shouldReturnFalse() {
        assertThat(rule.validate(forecast)).isFalse();
    }

}