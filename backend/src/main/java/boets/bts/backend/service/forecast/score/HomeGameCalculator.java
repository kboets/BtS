package boets.bts.backend.service.forecast.score;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.service.algorithm.AlgorithmService;
import boets.bts.backend.service.forecast.calculator.ForecastData;
import boets.bts.backend.service.forecast.ForecastDetailDto;
import boets.bts.backend.service.standing.StandingService;
import boets.bts.backend.web.algorithm.AlgorithmDto;
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

import java.math.BigInteger;
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
@Transactional
public class HomeGameCalculator extends AbstractGameCalculator {

    private final static Logger logger = LoggerFactory.getLogger(HomeGameCalculator.class);
    private int homeWinScore;
    private int homeDrawScore;
    private int homeLoseScore;
    private final StandingService standingService;
    private final StandingMapper standingMapper;

    public HomeGameCalculator(StandingService standingService, StandingMapper standingMapper, AlgorithmService algorithmService) {
        super(algorithmService);
        this.standingService = standingService;
        this.standingMapper = standingMapper;
        //default values in case no algorithm is set
        this.homeWinScore = 20;
        this.homeDrawScore = 10;
        this.homeLoseScore = 5;
    }

    @Override
    public void calculateScore(ForecastDetailDto forecastDetail, ForecastData forecastData, List<ForecastDetailDto> forecastDetails) {
        AlgorithmDto current = super.getCurrentAlgorithm();
        if (current != null) {
            this.homeWinScore = current.getHomePoints().getWin();
            this.homeDrawScore = current.getHomePoints().getDraw();
            this.homeLoseScore = current.getHomePoints().getLose();
        }
        TeamDto homeTeam = forecastDetail.getTeam();
        StringBuilder infoMessage = new StringBuilder();
        infoMessage.append(createInitMessage());
        //retrieve number of teams
        LeagueDto leagueDto = forecastData.getLeague();
        int totalTeams = leagueDto.getTeamDtos().size();
        List<ResultDto> allResults = forecastDetail.getResults().stream().sorted(Comparator.comparing(ResultDto::getRoundNumber).reversed()).collect(Collectors.toList());
        // get latest home games for this team
        List<ResultDto> homeResultForTeam = latestHomeResultForTeam(allResults, forecastData.getCurrentRound().getRoundNumber(), homeTeam);
        // calculate score
        BigInteger score = BigInteger.valueOf(0);
        for(ResultDto resultDto: homeResultForTeam) {
            Long leagueId = Long.parseLong(forecastData.getLeague().getLeague_id());
            List<Standing> standings = standingService.getStandingsForLeagueByRound(leagueId, forecastData.getLeague().getSeason(), resultDto.getRoundNumber());
            TeamDto nextOpponent = resultDto.getAwayTeam();
            // get standing opponent
            StandingDto opponentStanding = getStandingOpponent(standingMapper.toStandingDtos(standings), nextOpponent);
            if(opponentStanding == null) {
                logger.warn("Could not find standing for team {}", nextOpponent.getName());
            } else {
                if(hasLost(resultDto, homeTeam)) {
                    BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, homeTeam));
                    BigInteger lostScore = BigInteger.valueOf(opponentStanding.getRank());
                    BigInteger lostScoreResult = initialScore.subtract(lostScore);
                    score = score.add(lostScoreResult);
                } else {
                    BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, homeTeam));
                    BigInteger notLost = BigInteger.valueOf(totalTeams - opponentStanding.getRank());
                    BigInteger notLostScoreResult = initialScore.add(notLost);
                    score = score.add(notLostScoreResult);
                }
            }
            if(opponentStanding != null) {
                // create info message
                infoMessage.append(createInfoMessage(resultDto, homeTeam, opponentStanding, totalTeams));
            }
        }
        forecastDetail.setResultScore(score);
        infoMessage.append("<br>")
                .append("<h5>")
                .append("Eind score thuis wedstrijden : ")
                .append(forecastDetail.getResultScore())
                .append("</h5>");
        forecastDetail.setInfo(infoMessage.toString());
    }

    private String createInfoMessage(ResultDto resultDto, TeamDto homeTeam, StandingDto opponentStanding, int totalTeams) {
        StringBuilder infoMessage = new StringBuilder();
        if(hasWon(resultDto,homeTeam)) {
            BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, homeTeam));
            BigInteger notLost = BigInteger.valueOf(totalTeams - opponentStanding.getRank());
            BigInteger notLostScoreResult = initialScore.add(notLost);
            infoMessage.append(appendResultMessage("winst", resultDto));
            infoMessage.append(appendScoreMessage(totalTeams, opponentStanding, "winst", notLostScoreResult));
        } else if(hasLost(resultDto, homeTeam)) {
            BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, homeTeam));
            BigInteger lostScore = BigInteger.valueOf(opponentStanding.getRank());
            BigInteger lostScoreResult = initialScore.subtract(lostScore);
            infoMessage.append(appendResultMessage("verlies", resultDto));
            infoMessage.append(appendScoreLostMessage(opponentStanding, lostScoreResult));
        } else {
            BigInteger initialScore = BigInteger.valueOf(getInitialScore(resultDto, homeTeam));
            BigInteger notLost = BigInteger.valueOf(totalTeams - opponentStanding.getRank());
            BigInteger notLostScoreResult = initialScore.add(notLost);
            infoMessage.append(appendResultMessage("gelijk", resultDto));
            infoMessage.append(appendScoreMessage(totalTeams, opponentStanding, "draw", notLostScoreResult));
        }
        return infoMessage.toString();
    }

    private String appendResultMessage(String result, ResultDto resultDto) {
        StringBuilder infoMessage = new StringBuilder();
        infoMessage.append("<br>")
                .append(result)
                .append(" tegen ")
                .append(resultDto.getAwayTeam().getName()).append(" : ")
                .append(resultDto.getGoalsHomeTeam())
                .append(" - ")
                .append(resultDto.getGoalsAwayTeam());
        return infoMessage.toString();
    }

    private String appendScoreMessage(int totalTeams, StandingDto opponentStanding, String result, BigInteger score) {
        StringBuilder infoMessage = new StringBuilder();
        int initScore;
        if (result.equals("winst")) {
            initScore = homeWinScore;
        } else {
            initScore = homeDrawScore;
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
                .append(homeLoseScore)
                .append(" - ")
                .append(opponentStanding.getRank())
                .append(" = ")
                .append(score);
        return infoMessage.toString();
    }

    private int getInitialScore(ResultDto resultDto, TeamDto homeTeam) {
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

    private String createInitMessage() {
        StringBuilder infoMessage = new StringBuilder();
        infoMessage.append("Thuiswedstrijden :")
                .append("<br>")
                .append("Berekening winst thuis match : ")
                .append(homeWinScore)
                .append(" punten - (aantal teams - rank tegenstrever)")
                .append("<br>")
                .append("Berekening verlies thuis match : ")
                .append(homeLoseScore)
                .append(" punten - (rank tegenstrever)")
                .append("<br>")
                .append("Berekening gelijk spel thuis match : ")
                .append(homeDrawScore)
                .append("punten  - (aantal teams - rank tegenstrever)")
                .append("<br>");
        return  infoMessage.toString();
    }



}
