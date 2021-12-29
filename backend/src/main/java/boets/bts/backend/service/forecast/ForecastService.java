package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.domain.League;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.forecast.calculator.ForecastCalculatorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class ForecastService {
    private static final Logger logger = LoggerFactory.getLogger(ForecastService.class);

    private final ForecastCalculatorManager forecastCalculatorManager;
    private final AdminService adminService;
    private final LeagueRepository leagueRepository;

    private ConcurrentHashMap<LocalDate, List<Forecast>> forecastMap;

    public ForecastService(ForecastCalculatorManager forecastCalculatorManager, AdminService adminService, LeagueRepository leagueRepository) {
        this.forecastCalculatorManager = forecastCalculatorManager;
        this.adminService = adminService;
        this.leagueRepository = leagueRepository;
        this.forecastMap = new ConcurrentHashMap<>();
    }

    public List<Forecast> calculateForecast() throws Exception {
        LocalDate localDate = LocalDate.now();
        List<Forecast> forecasts = new ArrayList<>();
        //before calculating, all rounds, results and standings must be updated.
        if(adminService.isHistoricData()) {
            List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason()));
            forecasts.addAll(forecastCalculatorManager.calculateForecasts(leagues));
            forecastMap.put(localDate, forecasts);
            return forecasts;
        }
        if(!adminService.isTodayExecuted(AdminKeys.CRON_RESULTS)) {
            logger.warn("Could not yet calculate forecasts as result is not yet up to date");
            forecastMap.clear();
            return forecasts;
        }
        if(!adminService.isTodayExecuted(AdminKeys.CRON_ROUNDS)) {
            logger.warn("Could not yet calculate forecasts as round is not yet up to date");
            forecastMap.clear();
            return forecasts;
        }
        if(!adminService.isTodayExecuted(AdminKeys.CRON_STANDINGS)) {
            logger.warn("Could not yet calculate forecasts as standings is not yet up to date");
            forecastMap.clear();
            return forecasts;
        }
        List<Forecast> savedForecasts = forecastMap.get(localDate);
        if(savedForecasts != null && !savedForecasts.isEmpty()) {
            return savedForecasts;
        } else {
            List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason()));
            forecasts.addAll(forecastCalculatorManager.calculateForecasts(leagues));
            //setting it in comment, as a workaround for the force button (as it is always forced to recalculate the forecasts)
            //forecastMap.put(localDate, forecasts);
        }

        return forecasts;
    }

    /**
     * retrieves all the forecasts with the details that has a score that is lower or equal as the given score(s)
     * @param scores
     * @return
     */
    public List<Forecast> getRequestedForecasts(List<Integer> scores) throws Exception {
        List<Forecast> forecasts = calculateForecast();
        if (!scores.isEmpty()) {
            for (Forecast forecast : forecasts) {
                forecast.setForecastDetails(forecast.getForecastDetails().stream()
                        .filter(forecastDetail -> forecastDetailScore(scores, forecastDetail))
                        .collect(Collectors.toList()));
            }
            return forecasts;
        }
        return forecasts;

    }

    private boolean forecastDetailScore(List<Integer> scores, ForecastDetail forecastDetail) {
        int forecastScore = forecastDetail.getScore();
        boolean isValid = false;
        for(int score: scores) {
            if(score == 50 && forecastScore < score) {
                isValid = true;
                break;
            } else if(score > 150 && forecastScore > 150) {
                isValid = true;
                break;
            } else if(forecastScore >= (score-50) && forecastScore < score){
                isValid = true;
                break;
            }
        }
        return isValid;
    }

}
