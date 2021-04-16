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
import boets.bts.backend.service.round.RoundService;
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
    private final AdminService adminService;
    private final ResultRepository resultRepository;
    private final LeagueRepository leagueRepository;

    public ForecastService(RoundService roundService, AdminService adminService, ResultRepository resultRepository, LeagueRepository leagueRepository) {
        this.roundService = roundService;
        this.adminService = adminService;
        this.resultRepository = resultRepository;
        this.leagueRepository = leagueRepository;
    }

    public void calculateForecasts() {
        List<Forecast> forecasts = new ArrayList<>();
        //1.get all leagues for this season
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason()));
        //2. get last 5 or 6 results for each league
        Map<League, List<Result>> resultsForLeague = this.getRecentFinishedResults(leagues);
        //3. sort for each league the result / team
        for(Map.Entry<League, List<Result>> entries: resultsForLeague.entrySet()) {
            forecasts.add(createForecast(entries.getKey(), entries.getValue()));
        }
        //4. get the next result for the team
        for(Forecast forecast: forecasts) {
            List<ForecastDetail> details = forecast.getForecastDetails();
            for(ForecastDetail forecastDetail: details) {
                forecastDetail.setNextResult(this.getNextResult(forecast.getLeague(), forecastDetail.getTeam()));
                if(forecastDetail.getNextResult() != null) {
                    Result nextResult = forecastDetail.getNextResult();
                    Team opponent = nextResult.getHomeTeam().equals(forecastDetail.getTeam())?nextResult.getAwayTeam():nextResult.getHomeTeam();
                    forecastDetail.setNextOpponent(opponent);
                }
            }
        }
        //5. determine the type of team
        //6. calculate the score / team

        logger.info("test");

    }

    private Result getNextResult(League league, Team team) {
        Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), adminService.getCurrentSeason());
        String nextRound = getRequestedRound(league.getRounds(), currentRound.getRoundNumber()+1);
        if(nextRound != null) {
            List<Result> results = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(league, nextRound));
            Optional<Result> optionalResult = results.stream().filter(result -> result.getHomeTeam().equals(team) || result.getAwayTeam().equals(team)).findFirst();
            return optionalResult.orElse(null);
        }
        return null;
    }

    private Forecast createForecast(League league, List<Result> results) {
        Forecast forecast = new Forecast(ForecastType.WIN);
        forecast.setLeague(league);
        List<Team> teams = league.getTeams();
        for(Team team: teams) {
            ForecastDetail forecastDetail = new ForecastDetail();
            forecastDetail.setTeam(team);
            forecastDetail.setResults(results.stream().filter(result -> result.getHomeTeam().equals(team) || result.getAwayTeam().equals(team)).collect(Collectors.toList()));
            forecast.getForecastDetails().add(forecastDetail);
        }
        return forecast;
    }


    /**
     * Retrieves all the results for the last 5 (or 6 if available) rounds
     * @param leagues
     * @return
     */
    private Map<League, List<Result>> getRecentFinishedResults(List<League> leagues) {
        Map<League, List<Result>> resultForLeague = new HashMap<>();
        for(League league: leagues) {
            Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), adminService.getCurrentSeason());
            if(currentRound.getRoundNumber()<5) {
                logger.info("Not enough results to do forecast for league {}", league.getName());
                continue;
            }
            List<String> requestedRounds = getLastRounds(league, currentRound);
            List<Result> limitedResults = resultRepository.findAll(ResultSpecs.getFinishedResultForRounds(league, requestedRounds));
            resultForLeague.put(league, limitedResults);
        }
        return resultForLeague;
    }

    private List<String> getLastRounds(League league, Round currentRound) {
        List<String> requestedRounds = new ArrayList<>();
        int currentRoundNumber = currentRound.getRoundNumber();
        int reverse = currentRoundNumber != 5?5:6;
        int startRoundNumber = currentRoundNumber - reverse;
        for(int i = startRoundNumber;i<=currentRoundNumber;i++) {
            requestedRounds.add(getRequestedRound(league.getRounds(), i));
        }
        return requestedRounds;
    }

    private String getRequestedRound(List<Round> rounds, int roundNumber) {
        Optional<String> roundStringOptional = rounds.stream().filter(round -> round.getRoundNumber() == roundNumber).map(Round::getRound).findFirst();
        return roundStringOptional.orElse(null);
    }
}
