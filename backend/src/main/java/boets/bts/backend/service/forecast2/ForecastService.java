package boets.bts.backend.service.forecast2;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.forecast.ForecastRepository;
import boets.bts.backend.repository.forecast.ForecastSpecs;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.repository.round.RoundSpecs;
import boets.bts.backend.service.admin.AdminService;
import boets.bts.backend.service.forecast2.calculator.ForecastCalculatorManager;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.forecast.ForecastDetailDto;
import boets.bts.backend.web.forecast.ForecastDto;
import boets.bts.backend.web.forecast.ForecastMapper;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
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
import java.util.Optional;
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
    private final RoundRepository roundRepository;
    private final RoundMapper roundMapper;
    private final ForecastRepository forecastRepository;
    private final ForecastMapper forecastMapper;
    private final ForecastCalculatorManager forecastCalculatorManager;

    private final ResultRepository resultRepository;


    public ForecastService(LeagueRepository leagueRepository, AdminService adminService, AlgorithmRepository algorithmRepository, RoundService roundService, RoundRepository roundRepository, ForecastRepository forecastRepository,
                           ForecastCalculatorManager forecastCalculatorManager, ForecastMapper forecastMapper, RoundMapper roundMapper, ResultRepository resultRepository) {
        this.leagueRepository = leagueRepository;
        this.adminService = adminService;
        this.algorithmRepository = algorithmRepository;
        this.roundService = roundService;
        this.forecastRepository = forecastRepository;
        this.forecastCalculatorManager = forecastCalculatorManager;
        this.forecastMapper = forecastMapper;
        this.resultRepository = resultRepository;
        this.roundRepository = roundRepository;
        this.roundMapper = roundMapper;
    }

    public List<ForecastDto> getReviewForecastForAlgorithm(Long algorithmId) {
        Algorithm algorithm = algorithmRepository.getReferenceById(algorithmId);
        List<Forecast> reviewForecasts = new ArrayList<>();
        List<League> getSelectedLeagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeasonAndSelected(WebUtils.getCurrentSeason(), true));
        for(League league: getSelectedLeagues) {
            List<Integer> rounds = calculateReviewRoundNumbers(league);
            reviewForecasts.addAll(forecastRepository.findAll(ForecastSpecs.forRounds(rounds).and(ForecastSpecs.forAlgorithm(algorithm).and(ForecastSpecs.forLeague(league)))));
        }
        return forecastMapper.toDtos(reviewForecasts);
    }

    public ForecastDto getReviewForecast(Long algorithmId, Long leagueId, Integer roundNumber) {
        Algorithm algorithm = algorithmRepository.getReferenceById(algorithmId);
        League league = leagueRepository.getReferenceById(leagueId);
        Optional<Forecast> forecastOptional = forecastRepository.findAll(ForecastSpecs.forRound(roundNumber).and(ForecastSpecs.forLeague(league).and(ForecastSpecs.forAlgorithm(algorithm))))
                .stream().findFirst();
        return forecastOptional.map(forecastMapper::toDto).orElse(null);
    }

    public List<ForecastDto> getReviewForecasts(Long algorithmId, Long leagueId) {
        Algorithm algorithm = algorithmRepository.getReferenceById(algorithmId);
        League league = leagueRepository.getReferenceById(leagueId);
        List<Forecast> forecasts = forecastRepository.findAll(ForecastSpecs.forLeague(league).and(ForecastSpecs.forAlgorithm(algorithm)));
        return forecastMapper.toDtos(forecasts);
    }

    public List<RoundDto> getReviewRounds(Long leagueId) {
        League league = leagueRepository.getReferenceById(leagueId);
        return roundMapper.toRoundDtos(calculateReviewRounds(league));
    }

    /**
     * Retrieves all the forecasts.
     * @return List of ForecastDto
     */
    public List<ForecastDto> getAllForecasts() {
        List<Forecast> allForecasts = forecastRepository.findAll();
        List<Forecast> filteredForecasts = allForecasts.stream()
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
    public List<ForecastDto> getRequestedForecasts(List<Integer> scores, Long algorithmId)  {
        List<ForecastDto> currentForecasts = forecastMapper.toDtos(getCurrentForecasts(algorithmId));
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
    public List<Forecast> getCurrentForecasts(Long algorithmId) {
        List<Forecast> currentForecasts = new ArrayList<>();
        int season = adminService.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeasonAndSelected(season, true));
        Algorithm algorithm = algorithmRepository.getReferenceById(algorithmId);
        for(League league: leagues) {
            Round nextRound = getLatestRound(league);
            // check if not last round
            Round lastRound = roundService.getLastRound(league.getId());
            if (!nextRound.getRoundNumber().equals(lastRound.getRoundNumber())) {
                currentForecasts.addAll(forecastRepository.findAll(ForecastSpecs.forRound(nextRound.getRoundNumber()).and(ForecastSpecs.forLeague(league).and(ForecastSpecs.forAlgorithm(algorithm)))));
            }
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
                List<Forecast> forecasts = forecastCalculatorManager.calculateForecasts(league, roundNumbers, algorithm);
                if (!forecasts.isEmpty()) {
                    forecastRepository.saveAll(forecasts);
                    forecastRepository.flush();
                }
                index++;
            }
        } catch (Exception e) {
            logger.error("Calculating the forecast for league {} throws an exception {}", league.getName(), e.toString());
        }

    }

    public void deleteForecast(Long forecastId) {
        forecastRepository.deleteById(forecastId);
    }
    public boolean deleteByAlgorithm(Algorithm algorithm) {
        forecastRepository.deleteByAlgorithm(algorithm);
        return true;
    }


    /**
     * Each half hour, starting at 7 minutes after the hour, on Tuesday and Thursday.
     */
    @Scheduled(cron ="0 7/30 * * * TUE-THU")
    protected void scheduleForecast() {
        logger.info("Start scheduleForecast");
        this.initCalculateForecasts();
    }

    public boolean initCalculateForecasts() {
        List<Algorithm> algorithms = algorithmRepository.findAll();
        return initCalculateForecast(algorithms);
    }


    public boolean initForecastWithNewAlgorithm(Algorithm algorithm) {
        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(algorithm);
        return initCalculateForecast(algorithms);
    }

    private boolean initCalculateForecast(List<Algorithm> algorithms) {
        int season = adminService.getCurrentSeason();
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeasonAndSelected(season, true));
        int index = 0;

        while (index < leagues.size()) {
            League league = leagues.get(index);
            Round nextRound = roundService.getNextRound(league.getId());
            if (nextRound.getRoundNumber() > 6) {
                this.calculateForecast(league, calculateRounds(league), algorithms);
            }
            index++;
        }
        return true;
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

    /**
     * Calculates all rounds except the last.
     * @param league -
     * @return List- a list with all rounds
     */
    protected List<Round> calculateReviewRounds(League league) {
        List<Round> rounds = new ArrayList<>();
        List<Integer> roundNumbers = calculateReviewRoundNumbers(league);
        for (Integer roundNumber: roundNumbers) {
            rounds.addAll(roundRepository.findAll(RoundSpecs.roundNumber(roundNumber).and(RoundSpecs.league(league))));
        }
        return rounds;
    }

    protected List<Integer> calculateReviewRoundNumbers(League league) {
        List<Integer> roundNumbers = new ArrayList<>();
        int start = 7;
        Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), WebUtils.getCurrentSeason());
        if (WebUtils.isWeekend()) {
            IntStream.range(start, currentRound.getRoundNumber()).forEach(roundNumbers::add);
        } else {
            IntStream.rangeClosed(start, currentRound.getRoundNumber()).forEach(roundNumbers::add);
        }

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


    private Round getLatestRound(League league) {
        LocalDate now = LocalDate.now();
        DayOfWeek today = now.getDayOfWeek();
        Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), league.getSeason());
        if (WebUtils.isWeekend() || today.equals(DayOfWeek.MONDAY)) {
            return currentRound;
        }
        // if current round does not contain any finished matches, return current, otherwise return next
        List<Result> nextResults = resultRepository.findAll(ResultSpecs.forLeague(league)
                .and(ResultSpecs.forRound(currentRound.getRoundNumber())));
        if (nextResults.stream().anyMatch(result -> result.getMatchStatus().equalsIgnoreCase("Match Finished"))) {
            return roundService.getNextRound(league.getId());
        } else {
            return currentRound;
        }

    }
}
