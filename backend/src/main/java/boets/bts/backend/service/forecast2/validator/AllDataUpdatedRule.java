package boets.bts.backend.service.forecast2.validator;

import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastResult;
import boets.bts.backend.service.admin.AdminService;
import org.springframework.stereotype.Component;

/**
 * Rule to check if following data all necessary have been updated today.
 * These data are :
 * - Results
 * - Rounds
 * - Standings
 */
@Component
public class AllDataUpdatedRule implements ForecastRule {
    private final AdminService adminService;
    public static final String errorMessage = "Daily update of rounds or results of standings not finished";

    public AllDataUpdatedRule(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public boolean validate(Forecast forecast) {
        if (!adminService.isTodayExecuted(AdminKeys.CRON_RESULTS) || !adminService.isTodayExecuted(AdminKeys.CRON_ROUNDS) ||
                !adminService.isTodayExecuted(AdminKeys.CRON_STANDINGS)) {
            forecast.setForecastResult(ForecastResult.FATAL);
            forecast.setMessage(errorMessage);
            return false;
        }
        return true;
    }
}
