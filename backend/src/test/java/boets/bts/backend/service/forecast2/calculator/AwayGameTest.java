package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.algorithm.AlgorithmSpecs;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.repository.team.TeamSpecs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
@Transactional
public class AwayGameTest {
    @Autowired
    private AwayGame awayGame;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private AlgorithmRepository algorithmRepository;
    private ForecastDetail forecastDetail;
    private Forecast forecast;

    public void initTest() {
        // Jupiler pro league
        League league = leagueRepository.getById(4366L);
        // team OH LEUVEN
        Team team = teamRepository.findAll(TeamSpecs.getTeamByTeamId(260L, league)).stream().findFirst().orElseThrow(()-> new IllegalArgumentException("could not find OH Leuven in Jupiler League"));
        // get algorithm
        Algorithm algorithm = algorithmRepository.findAll(AlgorithmSpecs.current()).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Could not find current algorithm"));
        // create forecast
        forecast = new Forecast();
        forecast.setRound(7);
        forecast.setLeague(league);
        forecast.setAlgorithm(algorithm);
        Result nextResult = resultRepository.findAll(ResultSpecs.forLeague(forecast.getLeague()).and(ResultSpecs.forRound(forecast.getRound()).and(ResultSpecs.forTeam(team))))
                .stream().findFirst().orElseThrow(IllegalArgumentException::new);
        forecastDetail = new ForecastDetail();
        forecastDetail.setNextGame(nextResult);
        forecastDetail.setTeam(team);
        Team opponent = nextResult.getAwayTeam().getTeamId().equals(team.getTeamId())?nextResult.getHomeTeam():nextResult.getAwayTeam();
        forecastDetail.setOpponent(opponent);
        forecast.addForecastDetail(forecastDetail);
    }

    @Test
    @Sql(scripts = {"/boets/bts/backend/service/forecast/calculator/league-be.sql"})
    public void calculate_givingJupilerLeague2022_shouldCalculateCorrect() {
        initTest();
        assertThat(forecastDetail.getAwayScore()).isEqualTo(0);
        awayGame.calculate(forecast, forecastDetail);
        assertThat(forecastDetail.getAwayScore()).isGreaterThan(0);
    }
}