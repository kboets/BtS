package boets.bts.backend.service.forecast;

import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamPerformanceDefiner {

    private Logger logger = LoggerFactory.getLogger(TeamPerformanceDefiner.class);

    public synchronized TeamPerformanceQualifier determinePerformance(TeamDto team, List<ResultDto> teamResults) {

        Set<ResultDto> winningResults = teamResults.stream().filter(result -> hasWon(result, team)).collect(Collectors.toSet());
        Set<ResultDto> losingResults = teamResults.stream().filter(result -> hasLost(result, team)).collect(Collectors.toSet());
        Set<ResultDto> drawResults = teamResults.stream().filter(this::isDraw).collect(Collectors.toSet());

        //1. TOPPER
        if(isTopper(winningResults, drawResults)) {
            return TeamPerformanceQualifier.TOPPER;
        //2. UITSTEKEND
        } else if(isUitstekend(winningResults, drawResults, losingResults)) {
            return TeamPerformanceQualifier.UITSTEKEND;
        //3. ZEER GOED
        } else if(isZeerGoed(winningResults, drawResults, losingResults)) {
            return TeamPerformanceQualifier.ZEER_GOED;
        //4. GOED
        } else if(isGoed(winningResults, drawResults, losingResults)) {
            return TeamPerformanceQualifier.GOED;
        //5. BEHOORLIJK
        } else if(isBehoorlijk(winningResults, drawResults, losingResults)) {
            return TeamPerformanceQualifier.BEHOORLIJK;
        //6. MATIG
        } else if(isMatig(winningResults, drawResults, losingResults)) {
            return TeamPerformanceQualifier.MATIG;
        //7. ONDERMAATS
        } else if(isOndermaats(winningResults, drawResults, losingResults)) {
            return TeamPerformanceQualifier.ONDERMAATS;
        //8. SLECHT
        } else if(isSlecht(winningResults, drawResults, losingResults)) {
            return TeamPerformanceQualifier.SLECHT;
        //9. ZEER SLECHT
        } else if(isZeerSlecht(winningResults, drawResults, losingResults)) {
            return TeamPerformanceQualifier.ZEER_SLECHT;
        //10. FLOPPER
        } else if(isFlopper(winningResults, drawResults, losingResults)) {
            return TeamPerformanceQualifier.FLOPPER;
        }

        logger.warn("Could not find a Performance definer for following case : winning games {}, draw games {}, lost games {} ", winningResults.size(), drawResults.size(), losingResults.size());

        return null;
    }

   private boolean isTopper(Set<ResultDto> winningResults, Set<ResultDto> drawResults) {
        if(winningResults.size() > 5) {
            return true;
        }
        return winningResults.size() == 5 && drawResults.size() == 1;
    }

    private boolean isUitstekend(Set<ResultDto> winningResults, Set<ResultDto> drawResults, Set<ResultDto> lostResults) {
        if(winningResults.size() == 5 && lostResults.size() == 1) {
            return true;
        } else {
            return winningResults.size() == 4 && drawResults.size() == 2;
        }
    }

    private boolean isZeerGoed(Set<ResultDto> winningResults, Set<ResultDto> drawResults, Set<ResultDto> lostResults) {
        if(winningResults.size() == 4 && drawResults.size() == 1 && lostResults.size() == 1) {
            return true;
        }
        return false;
    }

    private boolean isGoed(Set<ResultDto> winningResults, Set<ResultDto> drawResults, Set<ResultDto> lostResults) {
        if(winningResults.size() == 4 && lostResults.size() == 2) {
            return true;
        } else {
            return winningResults.size() == 3 && drawResults.size() == 3;
        }
    }

    private boolean isBehoorlijk(Set<ResultDto> winningResults, Set<ResultDto> drawResults, Set<ResultDto> lostResults) {
        if(winningResults.size() == 3) {
           return true;
        } else if(winningResults.size() == 2 && drawResults.size() == 4) {
            return true;
        }
        return false;
    }

    private boolean isMatig(Set<ResultDto> winningResults, Set<ResultDto> drawResults, Set<ResultDto> lostResults) {
        if(winningResults.size() == 2 && drawResults.size() == 3) {
            return true;
        }
        return false;
    }

    private boolean isOndermaats(Set<ResultDto> winningResults, Set<ResultDto> drawResults, Set<ResultDto> lostResults) {
        if(winningResults.size() == 2 && (drawResults.size() == 2 || drawResults.size() == 1)) {
            return true;
        }
        return false;
    }

    private boolean isSlecht(Set<ResultDto> winningResults, Set<ResultDto> drawResults, Set<ResultDto> lostResults) {
        if(winningResults.size() == 2 && lostResults.size() == 4) {
            return true;
        } else if(winningResults.size() == 1 && drawResults.size() == 5) {
            return true;
        }
        return false;
    }

    private boolean isZeerSlecht(Set<ResultDto> winningResults, Set<ResultDto> drawResults, Set<ResultDto> lostResults) {
        if(winningResults.size() == 1 && lostResults.size() < 4) {
            return true;
        }
        return false;
    }

    private boolean isFlopper(Set<ResultDto> winningResults, Set<ResultDto> drawResults, Set<ResultDto> lostResults) {
        if(winningResults.size() == 1 && lostResults.size() >= 4) {
            return true;
        } else if(winningResults.size() == 0) {
            return true;
        }
        return false;
    }


    private boolean hasWon(ResultDto result, TeamDto team) {
        if(result.getGoalsHomeTeam() > result.getGoalsAwayTeam()) {
            return team.getTeamId().equals(result.getHomeTeam().getTeamId());
        } else if(result.getGoalsAwayTeam() > result.getGoalsHomeTeam()) {
            return team.getTeamId().equals(result.getAwayTeam().getTeamId());
        }
        return false;
    }

    private boolean hasLost(ResultDto result, TeamDto team) {
        if(result.getGoalsHomeTeam() > result.getGoalsAwayTeam()) {
            return team.getTeamId().equals(result.getAwayTeam().getTeamId());
        } else if(result.getGoalsAwayTeam() > result.getGoalsHomeTeam()) {
            return team.getTeamId().equals(result.getHomeTeam().getTeamId());
        }
        return false;
    }


    private boolean isDraw(ResultDto result) {
        return result.getGoalsHomeTeam() == result.getGoalsAwayTeam();
     }



}
