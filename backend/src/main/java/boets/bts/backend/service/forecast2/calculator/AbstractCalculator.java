package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.standing.StandingRepository;
import boets.bts.backend.repository.standing.StandingSpecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCalculator implements ScoreCalculator {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCalculator.class);
    public static final String WIN = "winst";
    public static final String LOST = "verlies";
    public static final String DRAW = "gelijk";
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
        List<Result> gamesForTeam;
        List<Result> allFinished = getLast6FinishedResults(forecast, team);
        if (isHomeGames) {
            //take only home results
            gamesForTeam = allFinished.stream().filter(result -> result.getHomeTeam().getId().equals(team.getId())).collect(Collectors.toList());
        } else {
            //take only away results
            gamesForTeam= allFinished.stream().filter(result -> result.getAwayTeam().getId().equals(team.getId())).collect(Collectors.toList());
        }
        return handleResult(forecast, team, isHomeGames, gamesForTeam);

    }

    protected List<Result> handleResult(Forecast forecast, Team team, boolean isHomeGames, List<Result> gamesForTeam) {
        int size = gamesForTeam.size();
        if (size == 3) {
            return gamesForTeam;
        } else if (size > 3){
            // more as 3 games, get only 3
            return gamesForTeam.stream().sorted(Comparator.comparing(Result::getRoundNumber).reversed())
                    .limit(3).collect(Collectors.toList());
        } else {
            // result is less a three, retry 2 times until result is met
            int index = 1;
            boolean getNext = true;
            while (getNext) {
                // add extra round
                List<Result> extraResult =  getLastFinishedResults(forecast, team, index);
                if (isHomeGames) {
                    gamesForTeam.addAll(extraResult.stream().filter(result -> result.getHomeTeam().getId().equals(team.getId())).collect(Collectors.toList()));
                } else {
                    gamesForTeam.addAll(extraResult.stream().filter(result -> result.getAwayTeam().getId().equals(team.getId())).collect(Collectors.toList()));
                }
                index++;
                getNext = !(index == 3 || gamesForTeam.size() == 3);
            }
            return gamesForTeam;
        }
    }

    /**
     * Retrieves the last 6 finished games for this team.
     * @param forecast
     * @return List
     */
    protected List<Result> getLast6FinishedResults(Forecast forecast, Team team) {
        int round = forecast.getRound();
        // calculate the 6 previous rounds
        List<Integer> rounds = calculateRequestedRounds(round);
        List<Result> allResults = resultRepository.findAll(ResultSpecs.forLeague(forecast.getLeague()).and(ResultSpecs.forTeam(team).and(ResultSpecs.forRounds(rounds))));
        // get all finished results
        List<Result> gamesForTeam = allResults.stream().filter(result -> result.getMatchStatus().equals("Match Finished")).collect(Collectors.toList());
        if (gamesForTeam.size() != 6) {
            // get the round(s) with the not finished
            List<Integer> unfinishedRounds = allResults.stream().filter(result -> !result.getMatchStatus().equals("Match Finished")).map(Result::getRoundNumber).collect(Collectors.toList());
            rounds = calculateRequestedRounds(round, unfinishedRounds);
            allResults = resultRepository.findAll(ResultSpecs.forLeague(forecast.getLeague()).and(ResultSpecs.forTeam(team).and(ResultSpecs.forRounds(rounds))));
            gamesForTeam = allResults.stream().filter(result -> result.getMatchStatus().equals("Match Finished")).collect(Collectors.toList());
        }

        return gamesForTeam;
    }

    protected List<Result> getLastFinishedResults(Forecast forecast, Team team, int index) {
        int round = forecast.getRound();;
        List<Integer> rounds = calculateRequestedRounds(round);
        // get the first round of the retrieved results
        int firstRetrievedRound = rounds.get(0);
        if (firstRetrievedRound < 2) {
            // first try, can't take any other round
            return Collections.EMPTY_LIST;
        } else {
            round = firstRetrievedRound - index;
            if (round < 1) {
                // reached first round
                return Collections.EMPTY_LIST;
            }
            return resultRepository.findAll(ResultSpecs.forLeague(forecast.getLeague()).and(ResultSpecs.forTeam(team).and(ResultSpecs.forRound(round))));
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


    /**
     * Calculates previous 6 round numbers starting from the given round number, without the badRounds
     * @param round - the current round
     * @return List
     */
    protected synchronized List<Integer> calculateRequestedRounds(int round, List<Integer> badRounds) {
        List<Integer> rounds = new ArrayList<>();
        int totalBadRounds = badRounds.size();
        int startRound = round - 6;
        if ((startRound - totalBadRounds) < 1) {
            logger.warn("Could not calculate 6 rounds with finished results");
            return this.calculateRequestedRounds(round);
        } else {
            startRound = startRound - totalBadRounds;
            for (int i = startRound; i<round; i++) {
                if (!badRounds.contains(i)) {
                    rounds.add(i);
                }
            }
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


    protected String appendResultMessage(String winOrLose, Result result, boolean isHomeGame) {
        StringBuilder infoMessage = new StringBuilder();
        String opponent = isHomeGame?result.getAwayTeam().getName():result.getHomeTeam().getName();
        infoMessage.append("<br>")
                .append("<b>")
                .append(winOrLose)
                .append(" tegen ")
                .append(opponent)
                .append(" : ")
                .append(result.getGoalsHomeTeam())
                .append(" - ")
                .append(result.getGoalsAwayTeam())
                .append("</b>");
        return infoMessage.toString();
    }

    protected String appendScoreMessageWinDraw(int totalTeams, int opponentStanding, String result, int score, Algorithm algorithm, boolean isHome) {
        StringBuilder infoMessage = new StringBuilder();
        int initScore;
        if (result.equals(WIN)) {
            initScore = isHome?algorithm.getHomePoints().getWin():algorithm.getAwayPoints().getWin();
        } else {
            initScore = isHome?algorithm.getHomePoints().getDraw():algorithm.getAwayPoints().getDraw();
        }
        infoMessage
                .append("<br>")
                .append("score: ")
                .append(initScore)
                .append(" + (")
                .append(totalTeams).append("-")
                .append(opponentStanding)
                .append(") = ")
                .append(score);
        return infoMessage.toString();
    }

    protected String appendScoreLostMessage(int opponentStanding, int totalScore, Algorithm algorithm, boolean isHome) {
        StringBuilder infoMessage = new StringBuilder();
        int points = isHome?algorithm.getHomePoints().getLose():algorithm.getAwayPoints().getLose();
        infoMessage
                .append("<br>")
                .append("score: ")
                .append(points)
                .append(" - ")
                .append(opponentStanding)
                .append(" = ")
                .append(totalScore);
        return infoMessage.toString();
    }

}
