package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class MoreRoundMissingResultHandlerTest {

    @Autowired
    private MoreRoundMissingResultHandler resultHandler;
    @Autowired
    private ResultRepository resultRepository;

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest2.xml")
    public void accepts_givenOnlyMissingCurrentRound_shouldReturnFalse() {
        long jupilerLeague2020 = 2660L;
        List<Result> allResults = resultRepository.findAll(ResultSpecs.getResultByLeague(jupilerLeague2020));
        List<Result> allMissingResults = resultRepository.findAll(ResultSpecs.getAllNonFinishedResultUntilRound(jupilerLeague2020, "Regular_Season_-_2"));
        assertFalse(resultHandler.accepts(allResults, allMissingResults, "Regular_Season_-_2"));
    }

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest3.xml")
    public void accepts_givenMissingTwoRound_shouldReturnTrue() {
        long jupilerLeague2020 = 2660L;
        List<Result> allResults = resultRepository.findAll(ResultSpecs.getResultByLeague(jupilerLeague2020));
        List<Result> allMissingResults = resultRepository.findAll(ResultSpecs.getAllNonFinishedResultUntilRound(jupilerLeague2020, "Regular_Season_-_2"));
        assertTrue(resultHandler.accepts(allResults, allMissingResults, "Regular_Season_-_2"));
    }

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest3.xml")
    public void getResult_givenOnlyMissingResultMoreRound_shouldReturnTwoResult() throws Exception {
        long jupilerLeague2020 = 2660L;
        List<Result> allMissingResults = resultRepository.findAll(ResultSpecs.getAllNonFinishedResultUntilRound(jupilerLeague2020, "Regular_Season_-_2"));
        List<Result> result = resultHandler.getResult(jupilerLeague2020, allMissingResults, "Regular_Season_-_2");
        assertThat(result.size()).isEqualTo(5);
    }
}