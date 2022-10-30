package boets.bts.backend.service.forecast2.validator;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ForecastValidator {

    private List<ForecastRule> rules;

    @Autowired
    public ForecastValidator(List<ForecastRule> rules) {
        this.rules = rules;
    }

    public boolean validate(Forecast forecast) {
        boolean isValid = true;
        for (ForecastRule rule: rules) {
            if (!rule.validate(forecast)) {
                isValid = false;
                break;
            }
        }
        if (isValid) {
            forecast.setForecastResult(ForecastResult.OK);
        }
        return isValid;
    }
}
