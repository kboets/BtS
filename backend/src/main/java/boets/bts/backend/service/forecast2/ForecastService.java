package boets.bts.backend.service.forecast2;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.algorithm.AlgorithmSpecs;
import boets.bts.backend.repository.forecast.ForecastRepository;
import boets.bts.backend.repository.forecast.ForecastSpecs;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.forecast2.calculator.ForecastCalculatorManager2;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.forecast.ForecastDetailDto;
import boets.bts.backend.web.forecast.ForecastDto;
import boets.bts.backend.web.forecast.ForecastMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@Profile({"!integration"})
public class ForecastService {
    private static final Logger logger = LoggerFactory.getLogger(ForecastService.class);
    private final LeagueRepository leagueRepository;
    private final AdminService adminService;
    private final AlgorithmRepository algorithmRepository;
    private final RoundService roundService;
    private final ForecastRepository forecastRepository;
    private final ForecastMapper forecastMapper;
    private final ForecastCalculatorManager2 forecastCalculatorManager2;


    public ForecastService(LeagueRepository leagueRepository, AdminService adminService, AlgorithmRepository algorithmRepository, RoundService roundService, ForecastRepository forecastRepository,
                           ForecastCalculatorManager2 forecastCalculatorManager2, ForecastMapper forecastMapper) {
        this.leagueRepository = leagueRepository;
        this.adminService = adminService;
        this.algorithmRepository = algorithmRepository;
        this.roundService = roundService;
        this.forecastRepository = forecastRepository;
        this.forecastCalculatorManager2 = forecastCalculatorManager2;
        this.forecastMapper = forecastMapper;
    }

    /**
     * Retrieves all the forecasts except the current.
     * @return List of ForecastDto
     */
    public List<ForecastDto> getAllForecasts() {
        List<Forecast> allForecasts = forecastRepository.findAll();
        List<Forecast> currentForecasts = this.getCurrentForecasts();
        List<Forecast> filteredForecasts = allForecasts.stream()
                .filter(forecast -> !currentForecasts.contains(forecast))
                .sorted(Comparator.comparingInt(Forecast::getRound).reversed())
                .sorted(Comparator.comparing(forecast -> forecast.getLeague().getName()))
                .collect(Collectors.toList());
        return forecastMapper.toDtos(filteredForecasts);

    }


    /**
     * Retrieves all the current forecasts with forecast details that have a score that is lower or equal as the given score(s)
     * @param scores - a list with scores, can be empty
     * @return List
     */
    public List<ForecastDto> getRequestedForecasts(List<Integer> scores)  {
        List<ForecastDto> currentForecasts = forecastMapper.toDtos(getCurrentForecasts());
        if (!scores.isEmpty()) {
            List<ForecastDto> filteredForecasts = new ArrayList<>();
            for (ForecastDto forecastDto : currentForecasts) {
                List<ForecastDetailDto> forecastDetailDtos = forecastDto.getForecastDetails();
                List<ForecastDetailDto> filteredDetails = forecastDetailDtos.stream()
                        .filter(forecastDetail -> forecastDetailScore(scores, forecastDetail))
                        .sorted(Comparator.comparingInt(ForecastDetailDto::getFinalScore).reversed())
                        .collect(Collectors.toList());
                forecastDto.setForecastDetails(filteredDetails);
                filteredForecasts.add(forecastDto);
            }
            return filteredForecasts;
        }
        return currentForecasts.stream()
                .peek(forecastDto -> {
                    forecastDto.setForecastDetails(forecastDto.getForecastDetails().stream()
                    .sorted(Comparator.comparingInt(ForecastDetailDto::getFinalScore).reversed())
                    .collect(Collectors.toList()));
                    })
                .collect(Collectors.toList());

    }

    /**
     * Retrieves all the forecasts for the next round.
     * @return List
     */
    public List<Forecast> getCurrentForecasts() {
        List<Forecast> currentForecasts = new ArrayList<>();
        int season = adminService.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(season));
        Algorithm algorithm = algorithmRepository.findAll(AlgorithmSpecs.current()).get(0);
        for(League league: leagues) {
            Round nextRound = getLatestRoundForForecast(league);
            currentForecasts.addAll(forecastRepository.findAll(ForecastSpecs.forRound(nextRound.getRoundNumber()).and(ForecastSpecs.forLeague(league).and(ForecastSpecs.forAlgorithm(algorithm)))));
        }
        return currentForecasts;
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


    @Scheduled(cron ="0 7/30 * * * TUE-THU")
    protected void scheduleForecast() {
        logger.info("Start scheduleForecast");
        this.initScheduleForecasts();
    }


    //@Scheduled(cron ="* */5 * * * *")
    protected void scheduleForecasts2() {
        logger.info("Start scheduleForecast2");
        this.initScheduleForecasts();
    }

    protected void initScheduleForecasts() {
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

    private boolean forecastDetailScore(List<Integer> scores, ForecastDetailDto forecastDetail) {
        int forecastScore = forecastDetail.getFinalScore();
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


    private Round getLatestRoundForForecast(League league) {
        LocalDate now = LocalDate.now();
        DayOfWeek today = now.getDayOfWeek();
        if (WebUtils.isWeekend() || today.equals(DayOfWeek.MONDAY)) {
            return roundService.getCurrentRoundForLeague(league.getId(), league.getSeason());
        } else {
            return roundService.getNextRound(league.getId());
        }
    }
}
