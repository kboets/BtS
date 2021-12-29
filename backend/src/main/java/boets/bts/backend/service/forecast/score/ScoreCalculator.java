package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.calculator.ForecastData;
import boets.bts.backend.service.forecast.ForecastDetailDto;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.team.TeamDto;

import java.util.List;

public interface ScoreCalculator {

    void calculateScore(ForecastDetailDto forecastDetail, ForecastData forecastData, List<ForecastDetailDto> forecastDetails);

    default StandingDto getStandingOpponent(List<StandingDto> standings, TeamDto opponent) {
        return  standings.stream().filter(standing -> standing.getTeam().getId().equals(opponent.getId()))
                .findFirst()
                .orElse(null);
    }

    default TeamDto getOpponent(ResultDto resultDto, TeamDto teamDto) {
        if(resultDto.getHomeTeam().getTeamId().equals(teamDto.getTeamId())) {
            return resultDto.getAwayTeam();
        } else {
            return resultDto.getHomeTeam();
        }
    }

    default boolean hasWon(ResultDto result, TeamDto team) {
        if(result.getGoalsHomeTeam() > result.getGoalsAwayTeam()) {
            return team.getTeamId().equals(result.getHomeTeam().getTeamId());
        } else if(result.getGoalsAwayTeam() > result.getGoalsHomeTeam()) {
            return team.getTeamId().equals(result.getAwayTeam().getTeamId());
        }
        return false;
    }

    default boolean hasLost(ResultDto result, TeamDto team) {
        if(result.getGoalsHomeTeam() > result.getGoalsAwayTeam()) {
            return team.getTeamId().equals(result.getAwayTeam().getTeamId());
        } else if(result.getGoalsAwayTeam() > result.getGoalsHomeTeam()) {
            return team.getTeamId().equals(result.getHomeTeam().getTeamId());
        }
        return false;
    }
}
