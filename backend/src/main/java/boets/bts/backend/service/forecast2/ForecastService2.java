package boets.bts.backend.service.forecast2;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.forecast.ForecastRepository;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.forecast2.calculator.ForecastCalculatorManager2;
import boets.bts.backend.service.forecast2.validator.ForecastValidator;
import boets.bts.backend.service.round.RoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional
@Profile({"!integration"})
public class ForecastService2 {
    private static final Logger logger = LoggerFactory.getLogger(ForecastService2.class);
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
     * @param roundNumbers - the round number
     * @param algorithms - the requested algorithms
     */
    public void calculateForecast(League league, List<Integer> roundNumbers, List<Algorithm> algorithms) {
        int index = 0;

        try {
            while (index < algorithms.size()) {
                Algorithm algorithm = algorithms.get(index);
                List<Forecast> forecasts = forecastCalculatorManager2.calculateForecasts(league, roundNumbers, algorithm);
                forecastRepository.saveAll(forecasts);
                //logger.info("Forecast calculated :");
                index++;
            }
        } catch (Exception e) {
            logger.error("Calculating the forecast for league {} throws an exception {}", league.getName(), e.toString());
        }

    }
    @Scheduled(cron ="0 0/30 * * * TUE-THU")
    protected void scheduleForecast() {
        logger.info("Start scheduleForecast");
    }


    @Scheduled(cron ="* */5 * * * *")
    protected void scheduleForecasts2() {
        int season = adminService.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(season));
        List<Algorithm> algorithms = algorithmRepository.findAll();
        int index = 0;

        while (index < leagues.size()) {
            League league = leagues.get(index);
            Round nextRound = roundService.getNextRound(league.getId());
            if (nextRound.getRoundNumber() > 6) {
                this.calculateForecast(league, calculateRounds(league), algorithms);
            }
            index++;
        }

    }

    /**
     * Gets all the rounds that should be calculated. It starts from round 6 until the next round.
     * Finishes when the last round is reached.
     * @param league - the requested league
     * @return List- a list with all rounds
     */
    protected List<Integer> calculateRounds(League league) {
        List<Integer> roundNumbers = new ArrayList<>();
        int start = 7;
        Round nextRound = roundService.getNextRound(league.getId());
        IntStream.rangeClosed(start, nextRound.getRoundNumber()).forEach(roundNumbers::add);
        return roundNumbers;
    }

}
