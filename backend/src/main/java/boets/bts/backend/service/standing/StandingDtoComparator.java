package boets.bts.backend.service.standing;

import boets.bts.backend.web.standing.StandingDto;

import java.util.Comparator;

public class StandingDtoComparator implements Comparator<StandingDto> {
    @Override
    public int compare(StandingDto standingA, StandingDto standingB) {
        int result = standingA.getPoints().compareTo(standingB.getPoints());
        if (result != 0) {
            return result;
        }
        result = standingA.getAllSubStanding().getWin().compareTo(standingB.getAwaySubStanding().getWin());
        if (result != 0) {
            return result;
        }
        int goalsA = standingA.getAllSubStanding().getGoalsFor() - standingA.getAllSubStanding().getGoalsAgainst();
        int goalsB = standingB.getAllSubStanding().getGoalsFor() - standingB.getAllSubStanding().getGoalsAgainst();

        if(goalsA == goalsB) {
            return standingA.getTeam().getName().compareTo(standingB.getTeam().getName());
        } else {
            return Math.max(goalsA, goalsB);
        }
    }
}
