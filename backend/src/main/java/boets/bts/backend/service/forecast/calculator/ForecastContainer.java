package boets.bts.backend.service.forecast.calculator;

import boets.bts.backend.service.forecast.Forecast;

/**
 * Container object to hold the combination of a forecast and the forecast data.
 */
public class ForecastContainer {

    private ForecastData forecastData;
    private Forecast forecast;

    public ForecastContainer(ForecastData forecastData, Forecast forecast) {
        this.forecastData = forecastData;
        this.forecast = forecast;
    }

    public ForecastData getForecastData() {
        return forecastData;
    }

    public Forecast getForecast() {
        return forecast;
    }
}
