package boets.bts.backend.service;

import boets.bts.backend.web.results.ResultDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ResultServiceIntegrationTest {

    @Autowired
    public ResultService resultService;

    @Test
    public void testGetAllResults_givenJupilerLeague_shouldReturnList() {
        List<ResultDto> resultDtos = resultService.retrieveAllResultsForLeague(656L);
        assertThat(resultDtos.size()).isGreaterThan(0);

    }
}