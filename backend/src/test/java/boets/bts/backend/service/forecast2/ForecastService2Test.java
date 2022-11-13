package boets.bts.backend.service.forecast2;

import boets.bts.backend.domain.*;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.league.LeagueRepository;

import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.round.RoundRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
@Sql(scripts = "/boets/bts/backend/service/forecast/calculator/league-be.sql")
@Transactional
public class ForecastService2Test {

    @Autowired
    private AlgorithmRepository algorithmRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private ForecastService forecastService;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private RoundRepository roundRepository;
    private League league;
    private Round currentRound;

    @Before
    public void init() {
        league = leagueRepository.getById(4366L);
        //set current round
        currentRound = league.getRounds().stream().filter(round -> round.getRoundNumber() == 7).findFirst().orElseThrow(() -> new IllegalStateException("no round with 7"));
        currentRound.setCurrentDate(LocalDate.now());
        currentRound.setCurrent(true);
        roundRepository.save(currentRound);
    }

    @Test
    public void calculateRounds_givenJupilerLeague_shouldReturnOneElement() {
        List<Integer> rounds = forecastService.calculateRounds(league);
        assertThat(rounds.size()).isEqualTo(2);
    }
}