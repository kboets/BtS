package boets.bts.backend.service;

import boets.bts.backend.domain.Round;
import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.results.ResultDto;
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

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@ActiveProfiles("mock")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class ResultServiceTest {

    @Autowired
    public ResultService resultService;
    @Autowired
    public RoundService roundService;

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest.xml")
    public void retrieveAllResultsForLeague_givingEmptyResultForRound_shouldReturnResult() throws Exception{
        List<ResultDto> resultDtos = resultService.retrieveAllResultsForLeague(2660L);
        assertThat(resultDtos.isEmpty()).isFalse();
        assertThat(resultDtos.size()).isEqualTo(8);
    }

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest2.xml")
    public void retrieveAllResultsForLeague_givingNonCompleteResultForRound_shouldReturnResult() throws Exception{
        List<ResultDto> resultDtos = resultService.retrieveAllResultsForLeague(2660L);
        assertThat(resultDtos.isEmpty()).isFalse();
        assertThat(resultDtos.size()).isEqualTo(19);
    }

    @Test
    @DatabaseSetup(value = "/boets/bts/backend/service/result/ResultServiceIntegrationTest3.xml")
    public void retrieveAllResultsForLeague_givingResultForMoreRoundMissing_shouldReturnResult() throws Exception{
        List<ResultDto> resultDtos = resultService.retrieveAllResultsForLeague(2660L);
        assertThat(resultDtos.isEmpty()).isFalse();
        assertThat(resultDtos.size()).isEqualTo(20);
    }

}