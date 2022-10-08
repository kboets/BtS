package boets.bts.backend.service.forecast2;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastResult;
import boets.bts.backend.domain.League;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.forecast.ForecastRepository;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.forecast2.calculator.ForecastCalculatorManager2;
import boets.bts.backend.service.forecast2.validator.ForecastValidator;
import boets.bts.backend.service.round.RoundService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ForecastService2 {

    private final LeagueRepository leagueRepository;
    private final AdminService adminService;
    private final AlgorithmRepository algorithmRepository;
    private final RoundService roundService;
    private final ForecastRepository forecastRepository;
    private final ForecastValidator validator;
    private final ForecastCalculatorManager2 forecastCalculatorManager2;


    public ForecastService2(LeagueRepository leagueRepository, AdminService adminService, AlgorithmRepository algorithmRepository, RoundService roundService, ForecastRepository forecastRepository,
                            ForecastValidator validator, ForecastCalculatorManager2 forecastCalculatorManager2) {
        this.leagueRepository = leagueRepository;
        this.adminService = adminService;
        this.algorithmRepository = algorithmRepository;
        this.roundService = roundService;
        this.forecastRepository = forecastRepository;
        this.validator = validator;
        this.forecastCalculatorManager2 = forecastCalculatorManager2;
    }

    /**
     * In case the forecast is not yet persisted or contains warnings,
     * calculates and persist a new forecast for this league and round and algorithm.
     *
     * @param league - the league
     * @param roundNumber - the round number
     * @param algorithms - the requested algorithms
     * @return Forecast  -  the persisted forecast
     */
    public boolean calculateForecast(League league, int roundNumber, List<Algorithm> algorithms) {
////        Optional<Forecast> optionalForecast = forecastRepository.findAll(Specification.where(ForecastSpecs.forAlgorithm(algorithm)).and(ForecastSpecs.forLeague(league)).and(ForecastSpecs.forRound(roundNumber)))
////                .stream()
////                .findFirst();
//        Optional<Forecast> optionalForecast = Optional.empty();
//        Forecast forecast = optionalForecast.orElseGet(() -> null);
//        //1. validate forecast
//        if (!validateForecast(forecast)) {
//            // forecast is not valid, no calculation can be done
//            forecast.setDate(LocalDateTime.now());
//            forecastRepository.save(forecast);
//        }
        List<Forecast> forecasts = forecastCalculatorManager2.calculateForecasts(league, roundNumber, algorithms);

        //2. check if forecast needs to re-calculated
//        if (!isForecastCorrectCalculated(forecast)) {
//            forecast.setSeason(WebUtils.getCurrentSeason());
//            forecast.setDate(LocalDateTime.now());
//            try {
//                forecast =
//            } catch (Exception e) {
//                // TODO handle correct exception
//            }
//            forecastRepository.save(forecast);
//        }
        return true;
    }

    //@Scheduled(cron ="0 0 * * * TUE-FRI")
    @Scheduled(cron ="* * * * * *")
    protected void scheduleForecasts() {
        int season = adminService.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(season));
        List<Algorithm> algorithms = algorithmRepository.findAll();

//        for(League league: leagues) {
//            Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), season);
//            // start calculating forecast for each round, starting from round 7
//            for (int i = 7; i<=currentRound.getRoundNumber(); i++) {
//                this.calculateScheduledForecast4EachAlgorithm(league, i);
//            }
//        }
    }

//    protected void calculateScheduledForecast4EachAlgorithm(League league, int roundNumber) {
//        List<Algorithm> algorithms = algorithmRepository.findAll();
//        for(Algorithm algorithm: algorithms) {
//            this.calculateForecast(league, roundNumber, algorithm);
//        }
//    }

    /**
     * Validates the forecast.  A forecast is valid when it has result ok or when validators return true.
     * @param forecast - the current forecast
     * @return boolean - true if forecast is valid
     */
    private boolean validateForecast(Forecast forecast) {
        ForecastResult result = forecast.getForecastResult();
        if (result != null && result.equals(ForecastResult.OK)) {
            return true;
        }
        //re-validate the forecast
        forecast.setForecastResult(null);
        return validator.validate(forecast);
    }

    private boolean isForecastCorrectCalculated(Forecast forecast) {
        if (forecast.getForecastDetails().isEmpty()) {
            //no details yet, needs to be calculated
            return false;
        }
        return forecast.getForecastDetails().stream()
                .anyMatch(forecastDetail -> forecastDetail.getForecastResult().equals(ForecastResult.FATAL));
    }




}
