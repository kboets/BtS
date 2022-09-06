package boets.bts.backend.service.forecast.score;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.service.algorithm.AlgorithmService;
import boets.bts.backend.service.forecast.calculator.ForecastData;
import boets.bts.backend.service.forecast.ForecastDetailDto;
import boets.bts.backend.service.standing.StandingService;
import boets.bts.backend.web.algorithm.AlgorithmDto;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.standing.StandingMapper;
import boets.bts.backend.web.team.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
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
public class AwayGameCalculator extends AbstractGameCalculator {

    private final static  Logger logger = LoggerFactory.getLogger(AwayGameCalculator.class);
    private int awayWinScore;
    private int awayDrawScore;
    private int awayLoseScore;
    private final StandingService standingService;
    private final StandingMapper standingMapper;

    public AwayGameCalculator(StandingService standingService, StandingMapper standingMapper, AlgorithmService algorithmService) {
        super(algorithmService);
        this.standingService = standingService;
        this.standingMapper = standingMapper;
        //default values
        this.awayWinScore = 30;
        this.awayDrawScore = 15;
        this.awayLoseScore = 10;
    }

    @Override
    public void calculateScore(ForecastDetailDto forecastDetail, ForecastData forecastData, List<ForecastDetailDto> forecastDetails) {
        AlgorithmDto current = super.getCurrentAlgorithm();
        if(current != null) {
            this.awayWinScore = current.getAwayPoints().getWin();
            this.awayDrawScore = current.getAwayPoints().getDraw();
            this.awayLoseScore = current.getAwayPoints().getLose();
        }
        StringBuilder infoMessage = new StringBuilder();
        infoMessage.append(createInitMessage());
        //retrieve number of teams
        LeagueDto leagueDto = forecastData.getLeague();
        int totalTeams = leagueDto.getTeamDtos().size();
        List<ResultDto> allResults = forecastDetail.getResults().stream().sorted(Comparator.comparing(ResultDto::getRoundNumber).reversed()).collect(Collectors.toList());
        TeamDto awayTeam = forecastDetail.getTeam();
        // get latest away games for this team
        List<ResultDto> awayResultsForTeam = latestAwayResultForTeam(allResults, forecastData.getCurrentRound().getRoundNumber(), awayTeam);
        // calculate score
        BigInteger currentScore = forecastDetail.getResultScore();
        BigInteger awayScore = BigInteger.valueOf(0);
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
                    BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, awayTeam));
                    BigInteger opponentScore = BigInteger.valueOf(opponentStanding.getRank());
                    BigInteger lostScoreResult = initialScore.subtract(opponentScore);
                    awayScore = awayScore.add(lostScoreResult);
                } else {
                    BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, awayTeam));
                    BigInteger opponentScore = BigInteger.valueOf(totalTeams - opponentStanding.getRank());
                    BigInteger notLostScoreResult = initialScore.add(opponentScore);
                    awayScore = awayScore.add(notLostScoreResult);
                }
            }
            if(opponentStanding != null) {
                infoMessage.append(createInfoMessage(resultDto, awayTeam, opponentStanding, forecastDetail, totalTeams));
            }
        }
        currentScore = currentScore.add(awayScore);
        forecastDetail.setResultScore(currentScore);
        infoMessage.append("<br>")
                .append("<h5>")
                .append("Eind score uit wedstrijden : ")
                .append(awayScore)
                .append("</h5>");
        infoMessage.append("<br>");

        StringBuilder builder = new StringBuilder();
        builder.append(forecastDetail.getInfo());
        builder.append(infoMessage.toString());

        forecastDetail.setInfo(builder.toString());

    }

    private String createInfoMessage(ResultDto resultDto, TeamDto awayTeam, StandingDto opponentStanding, ForecastDetailDto forecastDetail, int totalTeams) {
        StringBuilder infoMessage = new StringBuilder();
        if(hasWon(resultDto,awayTeam)) {
            BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, awayTeam));
            BigInteger notLost = BigInteger.valueOf(totalTeams - opponentStanding.getRank());
            BigInteger notLostScoreResult = initialScore.add(notLost);
            infoMessage.append(appendResultMessage("winst", resultDto));
            infoMessage.append(appendScoreMessage(totalTeams, opponentStanding, "winst", notLostScoreResult));
        } else if(hasLost(resultDto, awayTeam)) {
            BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, awayTeam));
            BigInteger lostScore = BigInteger.valueOf(opponentStanding.getRank());
            BigInteger lostScoreResult = initialScore.subtract(lostScore);
            infoMessage.append(appendResultMessage("verlies", resultDto));
            infoMessage.append(appendScoreLostMessage(opponentStanding, lostScoreResult));
        } else {
            BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, awayTeam));
            BigInteger notLost = BigInteger.valueOf(totalTeams - opponentStanding.getRank());
            BigInteger notLostScoreResult = initialScore.add(notLost);
            infoMessage.append(appendResultMessage("gelijk", resultDto));
            infoMessage.append(appendScoreMessage(totalTeams, opponentStanding, "draw", notLostScoreResult));
        }
        return infoMessage.toString();
    }

    private String appendScoreMessage(int totalTeams, StandingDto opponentStanding, String result, BigInteger score) {
        StringBuilder infoMessage = new StringBuilder();
        int initScore;
        if (result.equals("winst")) {
            initScore = awayWinScore;
        } else {
            initScore = awayDrawScore;
        }
        infoMessage
                .append("<br>")
                .append("Score: ")
                .append(initScore)
                .append("+ (")
                .append(totalTeams).append("-")
                .append(opponentStanding.getRank())
                .append(") = ")
                .append(score);
        return infoMessage.toString();
    }

    private String appendScoreLostMessage(StandingDto opponentStanding, BigInteger score) {
        StringBuilder infoMessage = new StringBuilder();
        infoMessage
                .append("<br>")
                .append("Score: ")
                .append(awayLoseScore)
                .append(" - ")
                .append(opponentStanding.getRank())
                .append(" = ")
                .append(score);
        return infoMessage.toString();
    }


    private String appendResultMessage(String result, ResultDto resultDto) {
        StringBuilder infoMessage = new StringBuilder();
        infoMessage.append("<br>")
                .append(result)
                .append(" tegen ")
                .append(resultDto.getHomeTeam().getName()).append(" : ")
                .append(resultDto.getGoalsHomeTeam())
                .append(" - ")
                .append(resultDto.getGoalsAwayTeam());
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

    private String createInitMessage() {
        StringBuilder infoMessage = new StringBuilder();
        infoMessage.append("Uitwedstrijden ")
                .append("<br>")
                .append("Berekening winst uit match :  ")
                .append(awayWinScore)
                .append(" punten - (aantal teams - rank tegenstrever)")
                .append("<br>")
                .append("Berekening verlies uit match : ")
                .append(awayLoseScore)
                .append(" punten - (rank tegenstrever)")
                .append("<br>")
                .append("Berekening gelijk spel uit match :  ")
                .append(awayDrawScore)
                .append(" punten  - (aantal teams - rank tegenstrever)")
                .append("<br>");
        return infoMessage.toString();
    }
}
