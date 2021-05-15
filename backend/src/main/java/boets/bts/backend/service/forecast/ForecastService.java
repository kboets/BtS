package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.round.RoundMapper;
import boets.bts.backend.web.team.TeamDto;
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
    private TeamPerformanceDefiner teamPerformanceDefiner;

    public ForecastService(RoundService roundService, AdminService adminService, ResultRepository resultRepository,
                           LeagueRepository leagueRepository, RoundMapper roundMapper, ResultMapper resultMapper, LeagueMapper leagueMapper) {
        this.roundService = roundService;
        this.adminService = adminService;
        this.resultRepository = resultRepository;
        this.leagueRepository = leagueRepository;
        this.roundMapper = roundMapper;
        this.resultMapper = resultMapper;
        this.leagueMapper = leagueMapper;
        this.teamPerformanceDefiner = new TeamPerformanceDefiner();
    }

    public void calculateForecasts() {
        List<Forecast> forecasts = new ArrayList<>();
        //1.get all leagues for this season
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason()));
        List<LeagueDto> leagueDtos = leagueMapper.toLeagueDtoList(leagues);
        //2. get last 6 results for each league
        Map<LeagueDto, List<ResultDto>> resultsForLeague = this.getRecentFinishedResults(leagueDtos);
        //3. get for each league the last 6 results for each team
        for(Map.Entry<LeagueDto, List<ResultDto>> entries: resultsForLeague.entrySet()) {
            forecasts.add(initForecastWithTeams(entries.getKey(), entries.getValue()));
        }
        //4. get the next result for the team
        for(Forecast forecast: forecasts) {
            List<ForecastDetail> details = forecast.getForecastDetails();
            for(ForecastDetail forecastDetail: details) {
                forecastDetail.setNextResult(this.getNextResult(forecast.getLeague(), forecastDetail.getTeam()));
                if(forecastDetail.getNextResult() != null) {
                    ResultDto nextResult = forecastDetail.getNextResult();
                    TeamDto opponent = nextResult.getHomeTeam().equals(forecastDetail.getTeam())?nextResult.getAwayTeam():nextResult.getHomeTeam();
                    forecastDetail.setNextOpponent(opponent);
                }
            }
        }
        //5. determine the performance of each team
        Map<String, TeamPerformanceQualifier> teamPerformanceQualifierMap = new HashMap<>();
        for(Forecast forecast: forecasts) {
            List<ForecastDetail> details = forecast.getForecastDetails();
            for(ForecastDetail forecastDetail: details) {
                TeamPerformanceQualifier teamPerformanceQualifier = teamPerformanceDefiner.determinePerformance(forecastDetail.getTeam(), forecastDetail.getResults());
                teamPerformanceQualifierMap.put(forecastDetail.getTeam().getId(), teamPerformanceQualifier);
            }
        }

        //6. calculate the score / team

        logger.info("test");

    }

    private ResultDto getNextResult(LeagueDto league, TeamDto team) {
        Round currentRound = roundService.getCurrentRoundForLeague(Long.parseLong(league.getLeague_id()), adminService.getCurrentSeason());
        String nextRound = getRequestedRound(league.getRoundDtos(), currentRound.getRoundNumber()+1);
        if(nextRound != null) {
            List<Result> results = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(leagueMapper.toLeague(league), nextRound));
            Optional<Result> optionalResult = results.stream().filter(result -> result.getHomeTeam().equals(team) || result.getAwayTeam().equals(team)).findFirst();
            if(optionalResult.isPresent()) {
                return resultMapper.toResultDto(optionalResult.get());
            }
        }
        return null;
    }

    protected Forecast initForecastWithTeams(LeagueDto league, List<ResultDto> results) {
        Forecast forecast = new Forecast(ForecastType.WIN);
        forecast.setLeague(league);
        List<TeamDto> teams = league.getTeamDtos();
        for(TeamDto team: teams) {
            ForecastDetail forecastDetail = new ForecastDetail();
            forecastDetail.setTeam(team);
            forecastDetail.setResults(results.stream().filter(result -> result.getHomeTeam().equals(team) || result.getAwayTeam().equals(team)).collect(Collectors.toList()));
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
            if(currentRound.getRoundNumber()<6) {
                logger.info("Not enough results to do forecast for league {}", league.getName());
                continue;
            }
            List<String> requestedRounds = getLastRounds(league, currentRound);
            List<Result> limitedResults = resultRepository.findAll(ResultSpecs.getFinishedResultForRounds(leagueMapper.toLeague(league), requestedRounds));
            resultForLeague.put(league, resultMapper.toResultDtos(limitedResults));
        }
        return resultForLeague;
    }

    private List<String> getLastRounds(LeagueDto league, Round currentRound) {
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
