package boets.bts.backend.service.forecast2.validator;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastResult;
import boets.bts.backend.service.admin.AdminService;
import org.springframework.stereotype.Component;

/**
 * Rule to check if current version is not in historic data mode.
 */
@Component
public class NotHistoricDataRule implements ForecastRule {

    private final AdminService adminService;
    public static final String errorMessage = "In historic mode";

    public NotHistoricDataRule(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public boolean validate(Forecast forecast) {
        boolean isHistoric = adminService.isHistoricData();
        if (isHistoric) {
            forecast.setForecastResult(ForecastResult.FATAL);
            forecast.setMessage(errorMessage);
        }
        return !isHistoric;
    }
}
