package boets.bts.backend.web.forecast;

import boets.bts.backend.service.forecast2.ForecastService;
import boets.bts.backend.web.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            List<ForecastDto> result = forecastService.getRequestedForecasts(scores);
            return result;
        } catch (Exception e) {
            logger.error("Exception while calculating forecasts {} ", e.getMessage(), e);
            throw new GeneralException(e.getMessage());
        }
    }
}
