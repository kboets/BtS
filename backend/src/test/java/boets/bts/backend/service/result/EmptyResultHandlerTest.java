package boets.bts.backend.service.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.round.RoundService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import liquibase.pro.packaged.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
public class EmptyResultHandlerTest {

    @Autowired
    private EmptyResultHandler resultHandler;
    @Autowired
    private ResultRepository resultRepository;

    @Test
    public void accepts_givenEmptyAllResultHandler_shouldReturnTrue() {
        List<Result> allResultHandler = new ArrayList<>();
        List<Result> allMissingResultHandler = new ArrayList<>();
        assertTrue(resultHandler.accepts(allResultHandler, allMissingResultHandler, "Regular_Season_-_2"));
    }

    @Test
    public void accepts_givenNonEmptyAllResultHandler_shouldReturnFalse() {
        List<Result> allResultHandler = new ArrayList<>();
        allResultHandler.add(Result.ResultBuilder.aResult()
                .withLeague(new League())
                .build());
        List<Result> allMissingResultHandler = new ArrayList<>();
        assertFalse(resultHandler.accepts(allResultHandler, allMissingResultHandler, "Regular_Season_-_2"));
    }

    @Test
    public void getResult_givenJupilerLeague_shouldReturnList() throws Exception {
        long jupilerLeague2020 = 2660L;
        List<Result> allMissingResults = resultRepository.findAll(ResultSpecs.getResultByLeague(jupilerLeague2020));
        assertThat(allMissingResults.isEmpty()).isTrue();
        List<Result> result = resultHandler.getResult(jupilerLeague2020, Collections.emptyList(), "Regular_Season_-_2");
        assertThat(result.size()).isEqualTo(27);
    }
}