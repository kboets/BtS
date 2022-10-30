package boets.bts.backend.service;

import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.results.ResultDto;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@ActiveProfiles("mock")
public class ResultServiceTest {

    @Autowired
    public ResultService resultService;
    @Autowired
    public RoundService roundService;

    @Test
    @Sql(scripts = {"/boets/bts/backend/service/result/league-be.sql"})
    public void retrieveAllResultsForLeague_givingEmptyResultForRound_shouldReturnResult() throws Exception{
        List<ResultDto> resultDtos = resultService.verifyMissingResults(4366L);
        assertThat(resultDtos.isEmpty()).isFalse();
        assertThat(resultDtos.size()).isEqualTo(8);
    }

    @Test
    public void retrieveAllResultsForLeague_givingNonCompleteResultForRound_shouldReturnResult() throws Exception{
        List<ResultDto> resultDtos = resultService.verifyMissingResults(2660L);
        assertThat(resultDtos.isEmpty()).isFalse();
        assertThat(resultDtos.size()).isEqualTo(19);
    }

    @Test
    public void retrieveAllResultsForLeague_givingResultForMoreRoundMissing_shouldReturnResult() throws Exception{
        List<ResultDto> resultDtos = resultService.verifyMissingResults(2660L);
        assertThat(resultDtos.isEmpty()).isFalse();
        assertThat(resultDtos.size()).isEqualTo(20);
    }

}