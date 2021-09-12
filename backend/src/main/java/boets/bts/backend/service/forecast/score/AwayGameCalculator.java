package boets.bts.backend.service.forecast.score;

import boets.bts.backend.domain.Standing;
import boets.bts.backend.service.forecast.ForecastData;
import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.service.standing.StandingService;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.standing.StandingMapper;
import boets.bts.backend.web.team.TeamDto;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculates result of all the home games.
 * The result is based on :
 * result of the home game + (number of teams - rank of opponent)
 * eg
 * Charleroi - Club-Brugge : 1 - 1
 * -> 15 + (18 - 10) = 15 + 8 = 23
 */
@Component
@Order(2)
public class AwayGameCalculator implements ScoreCalculator {

    private final int awayWinScore;
    private final int awayDrawScore;
    private final int awayLoseScore;
    private final StandingService standingService;
    private final StandingMapper standingMapper;
    private final LeagueMapper leagueMapper;

    public AwayGameCalculator(StandingService standingService, StandingMapper standingMapper, LeagueMapper leagueMapper) {
        this.awayWinScore = 30;
        this.awayDrawScore = 15;
        this.awayLoseScore = 10;
        this.standingService = standingService;
        this.standingMapper = standingMapper;
        this.leagueMapper = leagueMapper;
    }

    @Override
    public void calculateScore(ForecastDetail forecastDetail, ForecastData forecastData, List<ForecastDetail> forecastDetails) {
        //retrieve number of teams
        LeagueDto leagueDto = leagueMapper.toLeagueDto(forecastData.getLeague());
        int totalTeams = leagueDto.getTeamDtos().size();
        List<ResultDto> allResults = forecastDetail.getResults().stream().sorted(Comparator.comparing(ResultDto::getRoundNumber).reversed()).collect(Collectors.toList());
        TeamDto awayTeam = forecastDetail.getTeam();
        // get latest away games for this team
        List<ResultDto> awayResultsForTeam = latestAwayResultForTeam(allResults, forecastData.getCurrentRound().getRoundNumber(), awayTeam);
        // calculate score
        for(ResultDto resultDto: awayResultsForTeam) {
            List<Standing> standings = standingService.getStandingsForLeagueByRound(forecastData.getLeague().getId(), forecastData.getLeague().getSeason(), resultDto.getRoundNumber());
            TeamDto nextOpponent = resultDto.getHomeTeam();
            // get standing opponent
            StandingDto opponentStanding = getStandingOpponent(standingMapper.toStandingDtos(standings), nextOpponent);
            int score = getResultScore(resultDto, awayTeam);
            if(hasLost(resultDto, awayTeam)) {
                score = score - opponentStanding.getPoints();
            } else {
                score = score + (totalTeams - opponentStanding.getRank());
            }
            forecastDetail.setResultScore(forecastDetail.getResultScore() + score);
        }

    }

    private int getResultScore(ResultDto resultDto, TeamDto awayTeam) {
        // calculate initial points based on result
        int score;
        if(hasWon(resultDto, awayTeam)) {
            score = awayWinScore;
        } else if(hasLost(resultDto, awayTeam)) {
            score = awayLoseScore;
        } else {
            score = awayDrawScore;
        }
        return score;
    }

    private List<ResultDto> latestAwayResultForTeam(List<ResultDto> resultDtos, int currentRound, TeamDto teamDto) {
        int allowedGames;
        if(currentRound < 10 ) {
            allowedGames = 3;
        } else {
            allowedGames = 5;
        }
        return resultDtos.stream()
                .filter(resultDto -> resultDto.getAwayTeam().getTeamId().equals(teamDto.getTeamId()))
                .limit(allowedGames)
                .collect(Collectors.toList());
    }
}
