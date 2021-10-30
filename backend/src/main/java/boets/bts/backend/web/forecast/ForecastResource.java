package boets.bts.backend.web.forecast;

import boets.bts.backend.service.forecast.Forecast;
import boets.bts.backend.service.forecast.ForecastService;
import boets.bts.backend.web.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/forecast/")
public class ForecastResource {

    private final ForecastService forecastService;
    private Logger logger = LoggerFactory.getLogger(ForecastResource.class);

    public ForecastResource(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping("all")
    public List<Forecast> getAllWinForecast() {
        logger.info("Retrieving all forecast");
        try {
            List<Forecast> forecasts = forecastService.calculateForecast();
            return forecasts;
        } catch (Exception e) {
            logger.error("Exception while calculating forecasts {} ", e.getMessage(), e);
            throw new GeneralException(e.getMessage());
        }
    }
}
