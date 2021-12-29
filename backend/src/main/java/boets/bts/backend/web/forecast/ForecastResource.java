package boets.bts.backend.web.forecast;

import boets.bts.backend.service.forecast.Forecast;
import boets.bts.backend.service.forecast.ForecastService;
import boets.bts.backend.web.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/api/forecast/")
public class ForecastResource {

    private final ForecastService forecastService;
    private static Logger logger = LoggerFactory.getLogger(ForecastResource.class);

    public ForecastResource(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping("all")
    public List<Forecast> getAllWinForecast() {
        try {
            return  forecastService.calculateForecast();
        } catch (Exception e) {
            logger.error("Exception while calculating forecasts {} ", e.getMessage(), e);
            throw new GeneralException(e.getMessage());
        }
    }

    @PutMapping("requested")
    public List<Forecast> getRequestedForecasts(@RequestBody List<Integer> scores) {
        try {
            List<Forecast> result = forecastService.getRequestedForecasts(scores);
            result.stream().sorted(Comparator.comparing(forecast -> forecast.getLeague().getName()));
            return result;
        } catch (Exception e) {
            logger.error("Exception while calculating forecasts {} ", e.getMessage(), e);
            throw new GeneralException(e.getMessage());
        }
    }
}
