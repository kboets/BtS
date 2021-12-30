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

    private ConcurrentHashMap<LocalDate, List<ForecastDto>> forecastMap;

    public ForecastService(ForecastCalculatorManager forecastCalculatorManager, AdminService adminService, LeagueRepository leagueRepository) {
        this.forecastCalculatorManager = forecastCalculatorManager;
        this.adminService = adminService;
        this.leagueRepository = leagueRepository;
        this.forecastMap = new ConcurrentHashMap<>();
    }

    public List<ForecastDto> getAllForecasts() throws Exception {
        LocalDate localDate = LocalDate.now();
        List<ForecastDto> forecastDtos = new ArrayList<>();

        List<ForecastDto> cachedForecastDtos = forecastMap.get(localDate);
        if(cachedForecastDtos != null && !cachedForecastDtos.isEmpty()) {
            return cachedForecastDtos;
        } else {
            forecastDtos.addAll(this.calculateForecasts());
            forecastMap.put(localDate, forecastDtos);
        }

        return forecastDtos;
    }

    /**
     * retrieves all the forecasts with the details that has a score that is lower or equal as the given score(s)
     * @param scores
     * @return
     */
    public List<ForecastDto> getRequestedForecasts(List<Integer> scores) throws Exception {
        List<ForecastDto> forecastDtos = getAllForecasts();
        if (!scores.isEmpty()) {
            List<ForecastDto> filteredForecasts = new ArrayList<>();
            for (ForecastDto forecastDto : forecastDtos) {
                ForecastDto copiedForecastDto = new ForecastDto(forecastDto);
                copiedForecastDto.setForecastDetails(copiedForecastDto.getForecastDetails().stream()
                        .filter(forecastDetail -> forecastDetailScore(scores, forecastDetail))
                        .collect(Collectors.toList()));
                filteredForecasts.add(copiedForecastDto);
            }
            return filteredForecasts;
        }
        return forecastDtos;

    }

    private List<ForecastDto> calculateForecasts() throws Exception {
        //before calculating, all rounds, results and standings must be updated.
        if(adminService.isHistoricData()) {
            List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason()));
            return  forecastCalculatorManager.calculateForecasts(leagues);
        }
        if(!adminService.isTodayExecuted(AdminKeys.CRON_RESULTS)) {
            logger.warn("Could not yet calculate forecasts as result is not yet up to date");
            return new ArrayList<>();
        }
        if(!adminService.isTodayExecuted(AdminKeys.CRON_ROUNDS)) {
            logger.warn("Could not yet calculate forecasts as round is not yet up to date");
            return new ArrayList<>();
        }
        if(!adminService.isTodayExecuted(AdminKeys.CRON_STANDINGS)) {
            logger.warn("Could not yet calculate forecasts as standings is not yet up to date");
            return new ArrayList<>();
        }
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason()));
        return forecastCalculatorManager.calculateForecasts(leagues);
    }


    private boolean forecastDetailScore(List<Integer> scores, ForecastDetailDto forecastDetail) {
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
