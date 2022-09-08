package boets.bts.backend.service.forecast2.validator;

import boets.bts.backend.domain.Forecast;

public interface ForecastRule {

    boolean validate(Forecast forecast);
}
