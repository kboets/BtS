package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.LeagueService;
import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.service.round.RoundService;
import liquibase.pro.packaged.L;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ForecastDataCollector {

    private static final Logger logger = LoggerFactory.getLogger(ForecastDataCollector.class);

    private final AdminService adminService;
    private final LeagueService leagueService;
    private final RoundService roundService;
    private final ResultService resultService;
    private final ResultRepository resultRepository;
    private final LeagueRepository leagueRepository;

    public ForecastDataCollector(AdminService adminService, LeagueService leagueService, LeagueRepository leagueRepository, RoundService roundService,
                                 ResultService resultService, ResultRepository resultRepository) {
        this.adminService = adminService;
        this.leagueService = leagueService;
        this.leagueRepository = leagueRepository;
        this.roundService = roundService;
        this.resultService = resultService;
        this.resultRepository = resultRepository;
    }

    public List<ForecastData> collectForecastData() {
        List<ForecastData> forecastDataList = new ArrayList<>();
        int currentSeason = adminService.getCurrentSeason();
        //1. get all qualified leagues
        List<League> qualifiedLeagues = retrieveQualifiedLeagues(currentSeason);
        for(League league: qualifiedLeagues) {
            ForecastData forecastData = new ForecastData();
            forecastData.setLeague(league);
            // get next game
            Round nextRound = roundService.getNextRound(league.getId());
            List<Result> nextGames = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(league, nextRound.getRound()));
            forecastData.setNextResults(nextGames);
            // get current round
            Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), currentSeason);
            forecastData.setCurrentRound(currentRound);
            // get all played games
            forecastData.setFinishedResults(resultRepository.findAll(ResultSpecs.allFinishedResultsCurrentRoundIncluded(league, currentRound.getRoundNumber())));
            forecastDataList.add(forecastData);
        }
        return forecastDataList;
    }

    /**
     * Retrieves the leagues that have at least 6 played games and current round is not last.
     */
    protected List<League> retrieveQualifiedLeagues(int currentSeason) {
        //1.get all leagues for this season
        List<League> leagues = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(currentSeason));
        //2. filter allowed leagues, a league should have at least 6 played games, current round can not be last
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
        return allowedLeagues;
    }
}
