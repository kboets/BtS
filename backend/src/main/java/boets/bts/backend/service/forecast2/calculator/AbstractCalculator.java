package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.domain.Team;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.standing.StandingRepository;
import boets.bts.backend.repository.standing.StandingSpecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractCalculator implements ScoreCalculator {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCalculator.class);
    private final ResultRepository resultRepository;
    private final StandingRepository standingRepository;

    public AbstractCalculator(ResultRepository resultRepository, StandingRepository standingRepository) {
        this.resultRepository = resultRepository;
        this.standingRepository = standingRepository;
    }

    protected int getOpponentRanking(Forecast forecast, Team team, int round) {
        Optional<Standing> standing = standingRepository.findAll(StandingSpecs.forLeague(forecast.getLeague()).and(StandingSpecs.forRound(round).and(StandingSpecs.forTeam(team))))
                .stream()
                .findFirst();
        if(standing.isPresent()) {
            return standing.get().getRank();
        } else {
            logger.warn("Could not find a standing for team {} in round {} ", team.getName(), round);
            return 1;
        }
    }

    /**
     * Get the last 3 played games, depending on the request the home games or the away games.
     * @param forecast - the forecast
     * @param team - the requested team
     * @param isHomeGames - indicator for home or away games
     * @return List - requested 3 games.
     */
    protected List<Result> getPlayedGamesForTeam(Forecast forecast, Team team, boolean isHomeGames) {
        // get all results for requested rounds for this team
        List<Integer> rounds = calculateRequestedRounds(forecast.getRound());
        List<Result> allResults = resultRepository.findAll(ResultSpecs.forLeague(forecast.getLeague()).and(ResultSpecs.forTeam(team).and(ResultSpecs.forRounds(rounds))));
        if (isHomeGames) {
            //take only home results
            return allResults.stream().filter(result -> result.getHomeTeam().getTeamId().equals(team.getTeamId())).collect(Collectors.toList());
        } else {
            //take only away results
            return allResults.stream().filter(result -> result.getAwayTeam().getTeamId().equals(team.getTeamId())).collect(Collectors.toList());
        }
    }

    /**
     * Calculates previous 6 round numbers starting from the given round number.
     * @param round - the current round
     * @return List
     */
    protected synchronized List<Integer> calculateRequestedRounds(int round) {
        List<Integer> rounds = new ArrayList<>();
        int startRound = round - 6;
        for (int i = startRound; i<round; i++) {
            rounds.add(i);
        }
        return rounds;
    }

    protected synchronized boolean isWinGame(Team team, Result result) {
        if(result.getGoalsHomeTeam() > result.getGoalsAwayTeam()) {
            return team.getTeamId().equals(result.getHomeTeam().getTeamId());
        } else if(result.getGoalsAwayTeam() > result.getGoalsHomeTeam()) {
            return team.getTeamId().equals(result.getAwayTeam().getTeamId());
        }
        return false;
    }

    protected synchronized boolean isLoseGame(Team team, Result result) {
        if(result.getGoalsHomeTeam() > result.getGoalsAwayTeam()) {
            return team.getTeamId().equals(result.getAwayTeam().getTeamId());
        } else if(result.getGoalsAwayTeam() > result.getGoalsHomeTeam()) {
            return team.getTeamId().equals(result.getHomeTeam().getTeamId());
        }
        return false;
    }




}
