package boets.bts.backend.service.forecast.calculator;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultMapper;
import boets.bts.backend.web.round.RoundMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class ForecastDataCollector {

    private static final Logger logger = LoggerFactory.getLogger(ForecastDataCollector.class);

    private final RoundService roundService;
    private final ResultRepository resultRepository;
    private final LeagueMapper leagueMapper;
    private final RoundMapper roundMapper;
    private final ResultMapper resultMapper;
    private final LeagueRepository leagueRepository;
    private final RoundRepository roundRepository;

    public ForecastDataCollector(RoundService roundService, ResultRepository resultRepository, LeagueMapper leagueMapper,
                                 RoundMapper roundMapper, ResultMapper resultMapper, LeagueRepository leagueRepository, RoundRepository roundRepository) {
        this.roundService = roundService;
        this.resultRepository = resultRepository;
        this.leagueMapper = leagueMapper;
        this.roundMapper = roundMapper;
        this.resultMapper = resultMapper;
        this.leagueRepository = leagueRepository;
        this.roundRepository = roundRepository;
    }

    public ForecastData collectForecastData(League league) {
        League persistedLeague = leagueRepository.findById(league.getId()).orElseThrow(() -> new IllegalStateException(String.format("Could not retrieve League with id %s", league.getId())));
        int currentSeason = persistedLeague.getSeason();
        ForecastData forecastData = new ForecastData();
        forecastData.setLeague(leagueMapper.toLeagueDto(persistedLeague));
        // handle next game
        handleNextRound(forecastData, persistedLeague, currentSeason);
        // get current round
        Round currentRound = getCurrentRound(league, currentSeason);
        forecastData.setCurrentRound(roundMapper.toRoundDto(currentRound));
        // get all played games
        List<Result> playedGames = resultRepository.findAll(ResultSpecs.allFinishedResultsCurrentRoundIncluded(league, currentRound.getRoundNumber()));
        playedGames.sort(Comparator.comparing(Result::getRoundNumber, Comparator.reverseOrder()));
        if(currentRound.getRoundNumber() > 10) {
            // make sure only 10 rounds are taken into account
            int startRound = currentRound.getRoundNumber()-10;
            List<Result> filteredPlayedGames = playedGames.stream().filter(result -> result.getRoundNumber() >= startRound).collect(Collectors.toList());
            forecastData.setFinishedResults(resultMapper.toResultDtos(filteredPlayedGames));
        } else {
            forecastData.setFinishedResults(resultMapper.toResultDtos(playedGames));
        }
        return forecastData;
    }

    /**
     * Sets the next round with results, taking into account the weekend. In that case, the current round and results are taken.
     * @param forecastData
     */
    private void handleNextRound(ForecastData forecastData, League league, int currentSeason) {
        List<Result> nextGames;
        Round nextRound;
        if(WebUtils.isWeekend()) {
            // take the current round
            nextRound = roundService.getCurrentRoundForLeague(league.getId(), currentSeason);
        } else {
            nextRound = roundService.getNextRound(league.getId());
        }
        nextGames = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(league, nextRound.getRound()));
        forecastData.setNextResults(resultMapper.toResultDtos(nextGames));
    }

    /**
     * Retrieves the current round, taking into account the weekend. In that case, the previous round is taken.
     * @param league - the league
     * @param currentSeason - the current season of the league
     * @return Round - during the week, the current round, in weekend previous
     */
    private Round getCurrentRound(League league, int currentSeason) {
        if(WebUtils.isWeekend()) {
            return roundService.getPreviousRound(league.getId());
        } else {
            return roundService.getCurrentRoundForLeague(league.getId(), currentSeason);
        }
    }

}
