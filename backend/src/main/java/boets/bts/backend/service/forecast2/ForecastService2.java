package boets.bts.backend.service.forecast2;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.forecast.ForecastRepository;
import boets.bts.backend.repository.forecast.ForecastSpecs;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.forecast2.validator.ForecastValidator;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ForecastService2 {

    private final LeagueRepository leagueRepository;
    private final AdminService adminService;
    private final AlgorithmRepository algorithmRepository;
    private final RoundService roundService;
    private final ForecastRepository forecastRepository;
    private final ForecastValidator validator;


    public ForecastService2(LeagueRepository leagueRepository, AdminService adminService, AlgorithmRepository algorithmRepository, RoundService roundService, ForecastRepository forecastRepository, ForecastValidator validator) {
        this.leagueRepository = leagueRepository;
        this.adminService = adminService;
        this.algorithmRepository = algorithmRepository;
        this.roundService = roundService;
        this.forecastRepository = forecastRepository;
        this.validator = validator;
    }

    /**
     * Calculates and persist the forecast for this league and round and algorithm.
     * @param league - the league
     * @param roundNumber - the round number
     * @param algorithm - the requested algorithm
     */
    public void calculateForecast(League league, int roundNumber, Algorithm algorithm) {
        Optional<Forecast> optionalForecast = forecastRepository.findAll(Specification.where(ForecastSpecs.forAlgorithm(algorithm)).and(ForecastSpecs.forLeague(league)).and(ForecastSpecs.forRound(roundNumber)))
                .stream().findFirst();
        Forecast forecast = optionalForecast.orElseGet(() -> new Forecast(league, roundNumber, algorithm));
        //1. validate forecast
        if (!validateForecast(forecast)) {
            // forecast is not valid, no calculation can be done
            forecast.setDate(LocalDateTime.now());
            forecastRepository.save(forecast);
        } else if (forecast.getForecastDetails() == null) {
            // forecast is valid, but not yet calculated
            forecast.setSeason(WebUtils.getCurrentSeason());
        }



    }

    @Scheduled(cron ="0 0 * * * TUE-FRI")
    protected void scheduleForecasts() {
        int season = adminService.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(season));
        for(League league: leagues) {
            Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), season);
            // start calculating forecast for each round, starting from round 7
            for (int i = 7; i<=currentRound.getRoundNumber(); i++) {
                this.calculateScheduledForecast4EachAlgorithm(league, i);
            }
        }
    }

    protected void calculateScheduledForecast4EachAlgorithm(League league, int roundNumber) {
        List<Algorithm> algorithms = algorithmRepository.findAll();
        for(Algorithm algorithm: algorithms) {
            this.calculateForecast(league, roundNumber, algorithm);
        }
    }

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





}
