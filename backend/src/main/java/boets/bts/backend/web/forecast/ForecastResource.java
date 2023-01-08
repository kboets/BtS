package boets.bts.backend.web.forecast;

import boets.bts.backend.service.forecast2.ForecastService;
import boets.bts.backend.web.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/forecast/")
public class ForecastResource {

    private static final Logger logger = LoggerFactory.getLogger(ForecastResource.class);

    private final ForecastService forecastService;

    public ForecastResource(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @PutMapping("requested")
    public List<ForecastDto> getRequestedForecasts(@RequestBody List<Integer> scores) {
        try {
            return forecastService.getRequestedForecasts(scores);
        } catch (Exception e) {
            logger.error("Exception while calculating forecasts {} ", e.getMessage(), e);
            throw new GeneralException(e.getMessage());
        }
    }
    @GetMapping("allExceptCurrent")
    public List<ForecastDto> getAllButCurrentForecasts() {
        return forecastService.getAllExceptCurrentForecasts();
    }

    @GetMapping("all")
    public List<ForecastDto> getAllForecasts() {
        return forecastService.getAllForecasts();
    }
}
