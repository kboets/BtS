package boets.bts.backend.service.forecast.calculator;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.forecast.ForecastDto;
import boets.bts.backend.service.forecast.ForecastDetailDto;
import boets.bts.backend.service.forecast.score.ScoreCalculatorHandler;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static boets.bts.backend.service.forecast.calculator.StreamOfFuturesCollector.toFuture;

@Component
public class ForecastCalculatorManager {

    private static final Logger logger = LoggerFactory.getLogger(ForecastCalculatorManager.class);

    private final LeagueMapper leagueMapper;
    private final RoundService roundService;
    private final AdminService adminService;
    private final ResultRepository resultRepository;
    private final ForecastDataCollector forecastDataCollector;
    private final ScoreCalculatorHandler scoreCalculatorHandler;

    public ForecastCalculatorManager(LeagueMapper leagueMapper, RoundService roundService, AdminService adminService, ResultRepository resultRepository,
                                     ForecastDataCollector forecastDataCollector, ScoreCalculatorHandler scoreCalculatorHandler) {
        this.leagueMapper = leagueMapper;
        this.roundService = roundService;
        this.adminService = adminService;
        this.resultRepository = resultRepository;
        this.forecastDataCollector = forecastDataCollector;
        this.scoreCalculatorHandler = scoreCalculatorHandler;
    }

    public List<ForecastDto> calculateForecasts(List<League> requestedLeagues) throws Exception {
        List<ForecastDto> forecastDtos = new ArrayList<>();
        CompletableFuture<Stream<Optional<ForecastDto>>> forecastCompletableFutureStream = requestedLeagues.stream()
                // async check if league has enough played games to get forecasted.
                .map(this::isLeagueReadyForForecastAsync)
                //async retrieve all forecast data for each league
                .map(this::retrieveAllForecastDataAsync)
                //async create forecast, returns a forecast container object
                .map(this::calculateForecastsAsync)
                // async calculate scores
                .map(this::calculateScoreAsync)
                // async calculate final score
                .map(forecast -> calculateFinalScoreAsync(forecast, forecastDtos))
                .collect(toFuture());
        return getForecastsFromStream(forecastCompletableFutureStream);
    }

    /**
     * Collects all Forecasts from stream of CompletableFuture.
     *
     * @param forecastStreamFuture - Stream of CompletableFuture.
     * @return List of Forecast
     * @throws Exception
     */
    private List<ForecastDto> getForecastsFromStream(CompletableFuture<Stream<Optional<ForecastDto>>> forecastStreamFuture) throws Exception {
        try {
            Stream<Optional<ForecastDto>> forecastDataStream =
                    //Wait until all the data have been collected and calculated
                    forecastStreamFuture.join();
            return forecastDataStream
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
        } catch (CompletionException clex) {
            throw new Exception(clex.getCause());
        }
    }

    /**
     * Asynchronously check if a League is ready to be forecasted.
     *
     * @param league - league to be checked
     * @return A completable future to null if not ready, else a completable future
     */
    private CompletableFuture<Optional<League>> isLeagueReadyForForecastAsync(League league) {
        return CompletableFuture
                // Asynchronously check if league has at least 6 played games.
                .supplyAsync(() -> {
                    boolean isLeagueReady = isLeagueReadyToForecast(league);
                    if(isLeagueReady) {
                        return Optional.of(league);
                    }
                    return Optional.empty();
                });
    }

    /**
     * Asynchronously create a ForecastData object for each league.
     *
     * @param dataSetFuture - CompletableFuture of a League ready to be forecasted
     * @return A Completable futures of the ForecastData ready to calculate Forecasts.
     */
    private CompletableFuture<Optional<Object>> retrieveAllForecastDataAsync(CompletableFuture<Optional<League>> dataSetFuture) {
        return dataSetFuture
                .thenApplyAsync(dataSetOpt ->
                    dataSetOpt.map(this::retrieveForecastData));
    }

    /**
     * Asynchronously creates a Forecast for each ForecastData object.
     *
     * @param dataSetFuture - CompletableFuture of a League ready to be forecasted
     * @return A Completable futures of the ForecastContainers to be handled further.
     */
    private CompletableFuture<Optional<Object>> calculateForecastsAsync(CompletableFuture<Optional<Object>> dataSetFuture) {
        return dataSetFuture
                .thenApplyAsync(dataSetOpt ->
                        dataSetOpt.map(dataSet -> calculateForecast((ForecastData) dataSet)));
    }

    /**
     * Asynchronously calculates a result score for each Forecast.
     *
     * @param dataSetFuture - CompletableFuture of a Forecast ready to calculate all result score
     * @return A Completable future of the Forecast with all result scores calculated
     */
    private CompletableFuture<Optional<Object>> calculateScoreAsync(CompletableFuture<Optional<Object>> dataSetFuture) {
        return dataSetFuture
                .thenApplyAsync(dataSetOpt ->
                        dataSetOpt.map(dataSet -> calculateForecastResultScore((ForecastContainer) dataSet)));
    }

    /**
     * Asynchronously calculates a result score for each Forecast.
     *
     * @param dataSetFuture - CompletableFuture of a Forecast ready to calculate all result score
     * @return A Completable future of the Forecast with all result scores calculated
     */
    private CompletableFuture<Optional<ForecastDto>> calculateFinalScoreAsync(CompletableFuture<Optional<Object>> dataSetFuture, List<ForecastDto> forecastDtos) {
        return dataSetFuture
                .thenApplyAsync(dataSetOpt ->
                        dataSetOpt.map(dataSet -> calculateForecastFinalScore((ForecastDto) dataSet))
                );
    }

    /**
     * Checks if each team of the league has at least played 6 games & the current round can not be the last.
     * @param league - the league to check
     * @return boolean - true if conditions are met.
     */
    protected boolean isLeagueReadyToForecast(League league) {
        // current round can not be the last
        Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), adminService.getCurrentSeason());
        Round lastRound = roundService.getLastRound(league.getId());
        if(currentRound.getRoundNumber().equals(lastRound.getRoundNumber())){
            return false;
        }
        // at least 6 rounds
        LeagueDto leagueDto = leagueMapper.toLeagueDto(league);
        int teams = leagueDto.getTeamDtos().size();
        int expectedResult = teams/2 * 6;
        List<Result> results = resultRepository.findAll(ResultSpecs.getAllFinishedResult(league.getId()));
        return results.size() >= expectedResult;
    }

    protected ForecastData retrieveForecastData(League league) {
        return forecastDataCollector.collectForecastData(league);
    }

    protected ForecastContainer calculateForecast(ForecastData forecastData) {
        ForecastDto forecastDto = new ForecastDto();
        LeagueDto leagueDto = forecastData.getLeague();
        forecastDto.setLeague(leagueDto);
        List<TeamDto> teams = leagueDto.getTeamDtos();
        //create forecast details
        for (TeamDto teamDto : teams) {
            ForecastDetailDto forecastDetail = createForecastDetail(forecastData, teamDto);
            forecastDto.getForecastDetails().add(forecastDetail);
        }
        return new ForecastContainer(forecastData, forecastDto);
    }

    protected ForecastDto calculateForecastResultScore(ForecastContainer forecastContainer) {
        ForecastDto forecastDto = forecastContainer.getForecast();
        List<ForecastDetailDto> forecastDetails = forecastDto.getForecastDetails();
        for(ForecastDetailDto forecastDetail : forecastDetails) {
            scoreCalculatorHandler.calculateScore(forecastDetail, forecastContainer.getForecastData(), forecastDetails);
        }
        return forecastDto;
    }

    protected ForecastDto calculateForecastFinalScore(ForecastDto forecastDto) {
        List<ForecastDetailDto> forecastDetails = forecastDto.getForecastDetails();
        for(ForecastDetailDto forecastDetail : forecastDetails) {
            TeamDto opponent = forecastDetail.getNextOpponent();
            if(opponent != null) {
                ForecastDetailDto otherTeamForecastDetail = forecastDetails.stream()
                        .filter(forecastDetail1 -> forecastDetail1.getTeam().getTeamId().equals(opponent.getTeamId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException(String.format("Could not find a team with teamid %s in the list of forecastdetail", opponent.getTeamId())));
                logger.info("The score of the own results :  {}", forecastDetail.getResultScore().intValue());
                logger.info("The score of the opponent results :  {}", otherTeamForecastDetail.getResultScore().intValue());
                BigInteger endScore = forecastDetail.getResultScore().subtract(otherTeamForecastDetail.getResultScore());
                logger.info("The end score :  {}", endScore.intValue());
                StringBuilder infoMessage = new StringBuilder();
                forecastDetail.setScore(forecastDetail.getScore() + endScore.intValue());
                infoMessage.append("<br>")
                        .append("<h5>")
                        .append("Eind score : ")
                        .append(forecastDetail.getResultScore().intValue())
                        .append(" - ")
                        .append(otherTeamForecastDetail.getResultScore().intValue())
                        .append(" = ")
                        .append(forecastDetail.getScore())
                        .append("</h5>");
                StringBuilder builder = new StringBuilder();
                builder.append(forecastDetail.getInfo())
                        .append(infoMessage);

                forecastDetail.setInfo(builder.toString());
            }
        }
        forecastDetails.sort(Comparator.comparing(ForecastDetailDto::getScore, Comparator.reverseOrder()));
        return forecastDto;
    }

    private ForecastDetailDto createForecastDetail(ForecastData forecastData, TeamDto teamDto) {
        ForecastDetailDto forecastDetail = new ForecastDetailDto();
        forecastDetail.setTeam(teamDto);
        List<ResultDto> allNextResults = forecastData.getNextResults();
        // get next result for team
        Optional<ResultDto> nextResultOptional = this.nextResultForTeam(teamDto, allNextResults);
        if(nextResultOptional.isPresent()) {
            ResultDto nextResultDto = nextResultOptional.get();
            forecastDetail.setNextResult(nextResultDto);
            // get next opponent
            if(nextResultDto.getHomeTeam().getTeamId().equals(teamDto.getTeamId())) {
                forecastDetail.setNextOpponent(nextResultDto.getAwayTeam());
            } else {
                forecastDetail.setNextOpponent(nextResultDto.getHomeTeam());
            }
        }
        // get all previous finished result for team
        List<ResultDto> resultDtos = forecastData.getFinishedResults();
        forecastDetail.setResults(resultDtos.stream().filter(resultDto -> resultDto.getHomeTeam().getTeamId().equals(teamDto.getTeamId())
                || resultDto.getAwayTeam().getTeamId().equals(teamDto.getTeamId())).collect(Collectors.toList()));

        return forecastDetail;
    }

    private Optional<ResultDto> nextResultForTeam(TeamDto teamDto, List<ResultDto> resultDtos) {
        return resultDtos.stream().filter(resultDto -> resultDto.getHomeTeam().getTeamId().equals(teamDto.getTeamId())
                || resultDto.getAwayTeam().getTeamId().equals(teamDto.getTeamId()))
                .findFirst();
    }
}
