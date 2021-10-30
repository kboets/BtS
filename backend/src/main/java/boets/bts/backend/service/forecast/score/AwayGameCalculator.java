package boets.bts.backend.service.forecast.score;

import boets.bts.backend.domain.Standing;
import boets.bts.backend.service.forecast.calculator.ForecastData;
import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.service.standing.StandingService;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.standing.StandingMapper;
import boets.bts.backend.web.team.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculates result of all the away games.
 * The result is based on :
 * result of the away game + (number of teams - rank of opponent)
 * eg Away game of Club Brugge :
 * Charleroi - Club-Brugge : 1 - 1
 * -> 15 + (18 - 10) = 15 + 8 = 23
 */
@Component
@Order(2)
@Transactional
public class AwayGameCalculator implements ScoreCalculator {

    private final static  Logger logger = LoggerFactory.getLogger(AwayGameCalculator.class);
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
        StringBuilder infoMessage = new StringBuilder(forecastDetail.getInfo());
        //retrieve number of teams
        LeagueDto leagueDto = forecastData.getLeague();
        int totalTeams = leagueDto.getTeamDtos().size();
        List<ResultDto> allResults = forecastDetail.getResults().stream().sorted(Comparator.comparing(ResultDto::getRoundNumber).reversed()).collect(Collectors.toList());
        TeamDto awayTeam = forecastDetail.getTeam();
        // get latest away games for this team
        List<ResultDto> awayResultsForTeam = latestAwayResultForTeam(allResults, forecastData.getCurrentRound().getRoundNumber(), awayTeam);
        // calculate score
        int currentScore = forecastDetail.getScore();
        int score = 0;
        infoMessage.append("Uitwedstrijden ")
                .append("<br>")
                .append("Berekening winst uit match : 30 punten - (aantal teams - rank tegenstrever)")
                .append("<br>")
                .append("Berekening verlies uit match : 10 punten - (rank tegenstrever)")
                .append("<br>")
                .append("Berekening gelijk spel uit match : 15 punten  - (aantal teams - rank tegenstrever)")
                .append("<br>");
        for(ResultDto resultDto: awayResultsForTeam) {
            Long leagueId = Long.parseLong(forecastData.getLeague().getLeague_id());
            List<Standing> standings = standingService.getStandingsForLeagueByRound(leagueId, forecastData.getLeague().getSeason(), resultDto.getRoundNumber());
            TeamDto nextOpponent = resultDto.getHomeTeam();
            // get standing opponent
            StandingDto opponentStanding = getStandingOpponent(standingMapper.toStandingDtos(standings), nextOpponent);
            if(opponentStanding == null) {
                logger.warn("Could not find standing for team {}", nextOpponent.getName());
            } else {
                if(hasLost(resultDto, awayTeam)) {
                    score = getInitialScore(resultDto, awayTeam) - opponentStanding.getPoints();
                } else {
                    score = getInitialScore(resultDto, awayTeam) + (totalTeams - opponentStanding.getRank());
                }
                currentScore = currentScore+score;
                forecastDetail.setResultScore(forecastDetail.getResultScore() + score);
            }
            infoMessage.append(createInfoMessage(resultDto, awayTeam, opponentStanding, forecastDetail, totalTeams));
        }
        infoMessage.append("<br>")
                .append("<h5>")
                .append("Eind score uit wedstrijden : ")
                .append(score)
                .append("</h5>");
        infoMessage.append("<br>");

        forecastDetail.setInfo(infoMessage.toString());

    }

    private String createInfoMessage(ResultDto resultDto, TeamDto awayTeam, StandingDto opponentStanding, ForecastDetail forecastDetail, int totalTeams) {
        StringBuilder infoMessage = new StringBuilder();
        if(hasWon(resultDto,awayTeam)) {
            int score = getInitialScore(resultDto, awayTeam) + (totalTeams - opponentStanding.getRank());
            infoMessage.append("<br>");
            infoMessage.append("Winst tegen ").append(resultDto.getHomeTeam().getName()).append(" : ").append(resultDto.getGoalsHomeTeam())
                    .append(" - ").append(resultDto.getGoalsAwayTeam());
            infoMessage.append("<br>");
            infoMessage.append("Score : 30  + (").append(totalTeams).append("-").append(opponentStanding.getRank()).append(") = ").append(score);
        } else if(hasLost(resultDto, awayTeam)) {
            int score = (getInitialScore(resultDto, awayTeam) - opponentStanding.getPoints());
            infoMessage.append("<br>");
            infoMessage.append("Verlies tegen ").append(resultDto.getHomeTeam().getName()).append(" : ").append(resultDto.getGoalsHomeTeam())
                    .append(" - ").append(resultDto.getGoalsAwayTeam());
            infoMessage.append("<br>");
            infoMessage.append("Score : ")
                    .append(getInitialScore(resultDto, awayTeam))
                    .append(" - ")
                    .append(opponentStanding.getRank()).append(" = ")
                    .append(score);
        } else {
            int score = getInitialScore(resultDto, awayTeam) + (totalTeams - opponentStanding.getRank());
            infoMessage.append("<br>");
            infoMessage.append("Gelijk tegen ").append(resultDto.getHomeTeam().getName()).append(" : ").append(resultDto.getGoalsHomeTeam())
                    .append(" - ").append(resultDto.getGoalsAwayTeam());
            infoMessage.append("<br>");
            infoMessage.append("Score : 15  + (").append(totalTeams).append("-").append(opponentStanding.getRank()).append(") = ").append(score);
        }
        return infoMessage.toString();
    }

    private int getInitialScore(ResultDto resultDto, TeamDto awayTeam) {
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
