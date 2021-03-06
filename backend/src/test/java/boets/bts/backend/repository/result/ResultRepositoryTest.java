package boets.bts.backend.repository.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.service.round.RoundService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class ResultRepositoryTest {

    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private RoundRepository roundRepository;
    private long leagueId;
    private League league;

    private Round round;

    @Before
    public void init()  {
        round = roundRepository.findById(37L).orElseThrow(()-> new RuntimeException("No round found with id 37"));
        leagueId=2660L;
        league = leagueRepository.findById(leagueId).orElse(null);
    }

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest4.xml")
    public void getResultByLeagueAndRound_givingJupiler2020_shouldReturnResult()  {
        List<Result> result = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(league, round.getRound()));
        assertThat(result.isEmpty()).isFalse();
    }

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest3.xml")
    public void getAllNonFinishedResultUntilRound_givingJupiler2020()  {
        List<Result> result = resultRepository.findAll(ResultSpecs.getAllNonFinishedResultUntilRound(league, round));
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(3);
    }
}