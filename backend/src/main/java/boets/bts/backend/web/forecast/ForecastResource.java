package boets.bts.backend.web.forecast;
import boets.bts.backend.service.forecast2.ForecastService;
import boets.bts.backend.web.exception.GeneralException;
import boets.bts.backend.web.round.RoundDto;
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

    @PutMapping("requested/{algorithmId}")
    public List<ForecastDto> getRequestedForecasts(@RequestBody List<Integer> scores, @PathVariable("algorithmId") Long algorithmId) {
        try {
            return forecastService.getRequestedForecasts(scores, algorithmId);
        } catch (Exception e) {
            logger.error("Exception while calculating forecasts {} ", e.getMessage(), e);
            throw new GeneralException(e.getMessage());
        }
    }

    @GetMapping("all")
    public List<ForecastDto> getAllForecasts() {
        return forecastService.getAllForecasts();
    }

    @GetMapping("recalculate")
    public boolean calculateForecasts() {
        return this.forecastService.initCalculateForecasts();

    }
    @GetMapping("reviewForAlgorithm/{algorithmId}")
    public List<ForecastDto> getReviewForecast(@PathVariable("algorithmId") Long algorithmId) {
        return forecastService.getReviewForecastForAlgorithm(algorithmId);
    }

    @GetMapping("reviewForAlgorithmAndLeague/{algorithmId}/{leagueId}")
    public List<ForecastDto> getReviewForecasts(@PathVariable("algorithmId") Long algorithmId, @PathVariable("leagueId") Long leagueId) {
        return forecastService.getReviewForecasts(algorithmId, leagueId);
    }

    @GetMapping("review/{algorithmId}/{leagueId}/{round}")
    public ForecastDto getRequestedReviewForecast(@PathVariable("algorithmId") Long algorithmId, @PathVariable("leagueId") Long leagueId, @PathVariable("round") Integer round) {
        return forecastService.getReviewForecast(algorithmId, leagueId, round);
    }

    @GetMapping("reviewRounds/{leagueId}")
    public List<RoundDto> getReviewRounds(@PathVariable ("leagueId") Long leagueId) {
        return forecastService.getReviewRounds(leagueId);
    }
}
