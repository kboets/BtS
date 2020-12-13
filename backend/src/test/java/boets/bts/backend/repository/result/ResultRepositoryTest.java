package boets.bts.backend.repository.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
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

    private String round;

    @Before
    public void init()  {
        round = "Regular_Season_-_3";
    }

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest4.xml")
    public void getResultByLeagueAndRound_givingJupiler2020_shouldReturnResult()  {
        List<Result> result = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(2660L, round));
        assertThat(result.isEmpty()).isFalse();
    }

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest3.xml")
    public void getAllNonFinishedResultUntilRound_givingJupiler2020()  {
        List<Result> result = resultRepository.findAll(ResultSpecs.getAllNonFinishedResultUntilRound(2660L, round));
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(3);
    }
}