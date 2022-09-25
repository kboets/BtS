package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Team;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCalculator implements ScoreCalculator {


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
