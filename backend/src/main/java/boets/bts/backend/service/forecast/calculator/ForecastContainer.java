package boets.bts.backend.service.forecast.calculator;

import boets.bts.backend.service.forecast.ForecastDto;

/**
 * Container object to hold the combination of a forecast and the forecast data.
 */
public class ForecastContainer {

    private ForecastData forecastData;
    private ForecastDto forecastDto;

    public ForecastContainer(ForecastData forecastData, ForecastDto forecastDto) {
        this.forecastData = forecastData;
        this.forecastDto = forecastDto;
    }

    public ForecastData getForecastData() {
        return forecastData;
    }

    public ForecastDto getForecast() {
        return forecastDto;
    }
}
