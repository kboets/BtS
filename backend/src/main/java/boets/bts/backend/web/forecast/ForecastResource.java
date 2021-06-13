package boets.bts.backend.web.forecast;

import boets.bts.backend.service.forecast.Forecast;
import boets.bts.backend.service.forecast.ForecastService;
import boets.bts.backend.web.league.LeagueResource;
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

    @GetMapping("win")
    public List<Forecast> getAllWinForecast() {
        logger.info("Retrieving all WIN forecast");
        return forecastService.calculateForecasts();
    }
}
