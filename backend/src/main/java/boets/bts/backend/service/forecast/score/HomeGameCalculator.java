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
 * Club-Brugge - Antwerp : 3 - 1
 * -> 20 + (18 - 5) = 20 + 13 = 33
 */
@Component
@Order(1)
public class HomeGameCalculator implements ScoreCalculator {

    private final int homeWinScore;
    private final int homeDrawScore;
    private final int homeLoseScore;
    private final StandingService standingService;
    private final StandingMapper standingMapper;
    private final LeagueMapper leagueMapper;

    public HomeGameCalculator(StandingService standingService, StandingMapper standingMapper, LeagueMapper leagueMapper) {
        this.standingService = standingService;
        this.standingMapper = standingMapper;
        this.leagueMapper = leagueMapper;
        this.homeWinScore = 20;
        this.homeDrawScore = 10;
        this.homeLoseScore = 5;
    }

    @Override
    public void calculateScore(ForecastDetail forecastDetail, ForecastData forecastData, List<ForecastDetail> forecastDetails) {
        //retrieve number of teams
        LeagueDto leagueDto = leagueMapper.toLeagueDto(forecastData.getLeague());
        int totalTeams = leagueDto.getTeamDtos().size();
        List<ResultDto> allResults = forecastDetail.getResults().stream().sorted(Comparator.comparing(ResultDto::getRoundNumber).reversed()).collect(Collectors.toList());
        TeamDto homeTeam = forecastDetail.getTeam();
        // get latest home games for this team
        List<ResultDto> homeResultForTeam = latestHomeResultForTeam(allResults, forecastData.getCurrentRound().getRoundNumber(), homeTeam);
        // calculate score
        for(ResultDto resultDto: homeResultForTeam) {
            List<Standing> standings = standingService.getStandingsForLeagueByRound(forecastData.getLeague().getId(), forecastData.getLeague().getSeason(), resultDto.getRoundNumber());
            TeamDto nextOpponent = resultDto.getAwayTeam();
            // get standing opponent
            StandingDto opponentStanding = getStandingOpponent(standingMapper.toStandingDtos(standings), nextOpponent);
            int score = getResultScore(resultDto, homeTeam);
            if(hasLost(resultDto, homeTeam)) {
                score = score - opponentStanding.getRank();
            } else {
                score = score + (totalTeams - opponentStanding.getRank());
            }
            forecastDetail.setResultScore(forecastDetail.getResultScore() + score);
        }
    }

    private int getResultScore(ResultDto resultDto, TeamDto homeTeam) {
        // calculate initial points based on result
        int score;
        if(hasWon(resultDto, homeTeam)) {
            score = homeWinScore;
        } else if(hasLost(resultDto, homeTeam)) {
            score = homeLoseScore;
        } else {
            score = homeDrawScore;
        }
        return score;
    }

    private List<ResultDto> latestHomeResultForTeam(List<ResultDto> resultDtos, int currentRound, TeamDto teamDto) {
        int allowedGames;
        if(currentRound < 10 ) {
            allowedGames = 3;
        } else {
            allowedGames = 5;
        }
        return resultDtos.stream()
                .filter(resultDto -> resultDto.getHomeTeam().getTeamId().equals(teamDto.getTeamId()))
                .limit(allowedGames)
                .collect(Collectors.toList());
    }
}
