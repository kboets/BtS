package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.domain.Team;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.forecast.score.ScoreCalculatorHandler;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
import boets.bts.backend.web.team.TeamDto;
import boets.bts.backend.web.team.TeamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ForecastService {

    private Logger logger = LoggerFactory.getLogger(ForecastService.class);

    private final RoundService roundService;
    private final RoundMapper roundMapper;
    private final AdminService adminService;
    private final ResultRepository resultRepository;
    private final ResultMapper resultMapper;
    private final LeagueRepository leagueRepository;
    private final LeagueMapper leagueMapper;
    private final TeamMapper teamMapper;
    private final ScoreCalculatorHandler scoreCalculatorHandler;
    private TeamPerformanceDefiner teamPerformanceDefiner;

    public ForecastService(RoundService roundService, AdminService adminService, ResultRepository resultRepository,
                           LeagueRepository leagueRepository, RoundMapper roundMapper, ResultMapper resultMapper,
                           LeagueMapper leagueMapper, TeamMapper teamMapper, ScoreCalculatorHandler scoreCalculatorHandler) {
        this.roundService = roundService;
        this.adminService = adminService;
        this.resultRepository = resultRepository;
        this.leagueRepository = leagueRepository;
        this.roundMapper = roundMapper;
        this.resultMapper = resultMapper;
        this.leagueMapper = leagueMapper;
        this.teamMapper = teamMapper;
        this.scoreCalculatorHandler = scoreCalculatorHandler;
        this.teamPerformanceDefiner = new TeamPerformanceDefiner();
    }

    public List<Forecast> calculateForecasts() {
        List<Forecast> forecasts = new ArrayList<>();
        //1.get all leagues for this season
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason()));
        //2. verify if leagues are fit to do forecast
        List<LeagueDto> leagueDtos = checkAllowedLeagues(leagues);
        //3. get last 6 results for each league
        Map<LeagueDto, List<ResultDto>> resultsForLeague = this.getRecentFinishedResults(leagueDtos);
        //4. get for each league the last 6 results for each team
        for(Map.Entry<LeagueDto, List<ResultDto>> entries: resultsForLeague.entrySet()) {
            forecasts.add(initForecastWithTeams(entries.getKey(), entries.getValue()));
        }
        //4. get the next result for the team
        for(Forecast forecast: forecasts) {
            List<ForecastDetail> details = forecast.getForecastDetails();
            for(Iterator<ForecastDetail> iterator = details.iterator(); iterator.hasNext();) {
                ForecastDetail forecastDetail = iterator.next();
                ResultDto nextResultDto = this.getNextResult(forecast.getLeague(), forecastDetail.getTeam());
                if(nextResultDto == null) {
                    logger.warn("Could not find next result for team with id {} and league id {} ", forecastDetail.getTeam().getId(), forecast.getLeague().getLeague_id());
                    iterator.remove();
                    continue;
                }
                forecastDetail.setNextResult(nextResultDto);
                TeamDto opponent = nextResultDto.getHomeTeam().equals(forecastDetail.getTeam())?nextResultDto.getAwayTeam():nextResultDto.getHomeTeam();
                forecastDetail.setNextOpponent(opponent);
            }
        }
        //5. determine the performance of each team
        Map<String, TeamPerformanceQualifier> teamPerformanceQualifierMap = new HashMap<>();
        for(Forecast forecast: forecasts) {
            List<ForecastDetail> details = forecast.getForecastDetails();
            for(ForecastDetail forecastDetail: details) {
                TeamPerformanceQualifier teamPerformanceQualifier = teamPerformanceDefiner.determinePerformance(forecastDetail.getTeam(), forecastDetail.getResults());
                teamPerformanceQualifierMap.put(forecastDetail.getTeam().getId(), teamPerformanceQualifier);
                forecastDetail.setPerformanceQualifier(teamPerformanceQualifier);
            }
        }

        //6. calculate the score / team
        for(Forecast forecast: forecasts) {
            List<ForecastDetail> details = forecast.getForecastDetails();
            for(ForecastDetail forecastDetail: details) {
                scoreCalculatorHandler.calculateScore(forecastDetail, teamPerformanceQualifierMap);
            }
        }

        return forecasts;

    }

    private List<LeagueDto> checkAllowedLeagues(List<League> leagues) {
        List<League> allowedLeagues = new ArrayList<>();
        for(League league: leagues) {
            boolean isAllowed = true;
            //league should have at least 6 results
            Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), adminService.getCurrentSeason());
            if(currentRound.getRoundNumber()<6) {
                logger.info("Not enough results to do forecast for league {}", league.getName());
                isAllowed = false;
            }
            //if last round, no forecast possible
            Round lastRound = roundService.getLastRound(league.getId());
            if(currentRound.getRoundNumber().equals(lastRound.getRoundNumber())) {
                logger.info("League {} is current round is the last one, no forecast possible ", league.getName());
                isAllowed = false;
            }
            if(isAllowed) {
                allowedLeagues.add(league);
            }
        }
        return leagueMapper.toLeagueDtoList(allowedLeagues);


    }

    protected ResultDto getNextResult(LeagueDto leagueDto, TeamDto teamDto) {
        League league = leagueMapper.toLeague(leagueDto);
        Team team = teamMapper.toTeam(teamDto);
        Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), adminService.getCurrentSeason());
        //String nextRound = getRequestedRound(leagueDto.getRoundDtos(), currentRound.getRoundNumber()+1);
        if(currentRound.getRound() != null) {
            List<Result> results = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(league, currentRound.getRound()));
            Optional<Result> optionalResult = results.stream()
                    .filter(result -> result.getHomeTeam().equals(team) || result.getAwayTeam().equals(team))
                    .findFirst();
            if(optionalResult.isPresent()) {
                return resultMapper.toResultDto(optionalResult.get());
            }
        }
        logger.warn("Could not find next result for league {} and team {} ", leagueDto.getName(), teamDto.getName());
        return null;
    }

    protected Forecast initForecastWithTeams(LeagueDto league, List<ResultDto> results) {
        Forecast forecast = new Forecast(ForecastType.WIN);
        forecast.setLeague(league);
        List<TeamDto> teams = league.getTeamDtos();
        for(TeamDto team: teams) {
            ForecastDetail forecastDetail = new ForecastDetail();
            forecastDetail.setTeam(team);
            forecastDetail.setResults(results.stream()
                    .filter(result -> result.getHomeTeam().getId().equals(team.getId()) || result.getAwayTeam().getId().equals(team.getId()))
                    .collect(Collectors.toList()));
            forecast.getForecastDetails().add(forecastDetail);
        }
        return forecast;
    }


    /**
     * Retrieves all the results for the last 6 rounds
     * @param leagues
     * @return
     */
    protected Map<LeagueDto, List<ResultDto>> getRecentFinishedResults(List<LeagueDto> leagues) {
        Map<LeagueDto, List<ResultDto>> resultForLeague = new HashMap<>();
        for(LeagueDto league: leagues) {
            Round currentRound = roundService.getCurrentRoundForLeague(Long.parseLong(league.getLeague_id()), adminService.getCurrentSeason());
            List<String> requestedRounds = getRequest6LastRounds(league, currentRound);
            List<Result> limitedResults = resultRepository.findAll(ResultSpecs.getFinishedResultForRounds(leagueMapper.toLeague(league), requestedRounds));
            resultForLeague.put(league, resultMapper.toResultDtos(limitedResults));
        }
        return resultForLeague;
    }

    private List<String> getRequest6LastRounds(LeagueDto league, Round currentRound) {
        List<String> requestedRounds = new ArrayList<>();
        int currentRoundNumber = currentRound.getRoundNumber();
        int startRoundNumber = currentRoundNumber - 6;
        for(int i = startRoundNumber;i<currentRoundNumber;i++) {
            requestedRounds.add(getRequestedRound(league.getRoundDtos(), i));
        }
        return requestedRounds;
    }

    private String getRequestedRound(List<RoundDto> rounds, int roundNumber) {
        Optional<String> roundStringOptional = rounds.stream().filter(round -> round.getRoundNumber() == roundNumber).map(RoundDto::getRound).findFirst();
        return roundStringOptional.orElse(null);
    }
}
