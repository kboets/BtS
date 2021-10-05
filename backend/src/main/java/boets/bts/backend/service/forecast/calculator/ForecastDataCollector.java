package boets.bts.backend.service.forecast.calculator;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.LeagueService;
import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.service.round.RoundService;
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

    public ForecastDataCollector(RoundService roundService, ResultRepository resultRepository, LeagueMapper leagueMapper, RoundMapper roundMapper, ResultMapper resultMapper, LeagueRepository leagueRepository) {
        this.roundService = roundService;
        this.resultRepository = resultRepository;
        this.leagueMapper = leagueMapper;
        this.roundMapper = roundMapper;
        this.resultMapper = resultMapper;
        this.leagueRepository = leagueRepository;
    }

    public ForecastData collectForecastData(League league) {
        League persistedLeague = leagueRepository.findById(league.getId()).orElseThrow(() -> new IllegalStateException(String.format("Could not retrieve League with id %s", league.getId())));
        int currentSeason = persistedLeague.getSeason();
        ForecastData forecastData = new ForecastData();
        forecastData.setLeague(leagueMapper.toLeagueDto(persistedLeague));
        // get next game
        Round nextRound = roundService.getNextRound(league.getId());
        List<Result> nextGames = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(league, nextRound.getRound()));
        forecastData.setNextResults(resultMapper.toResultDtos(nextGames));
        // get current round
        Round currentRound = roundService.getCurrentRoundForLeague(league.getId(), currentSeason);
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

}
