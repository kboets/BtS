package boets.bts.backend.service.standing;

import boets.bts.backend.web.standing.StandingDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class StandingDtoComparator implements Comparator<StandingDto> {

    private static final Logger logger = LoggerFactory.getLogger(StandingDtoComparator.class);
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
            if (standingA.getTeam() != null && standingB.getTeam() != null) {
                return standingA.getTeam().getName().compareTo(standingB.getTeam().getName());
            } else {
                logger.warn("Could not compare team of standing {} and other standing {} ", standingA.getPoints(), standingB.getPoints());
                return goalsA;
            }
        } else {
            return Math.max(goalsA, goalsB);
        }
    }
}
