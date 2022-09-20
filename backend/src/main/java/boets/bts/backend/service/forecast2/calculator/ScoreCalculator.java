package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastDetail;

public interface ScoreCalculator {

    void calculate(ForecastDetail forecastDetail);


}
