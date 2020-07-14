package boets.bts.backend.web.results;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)

public class ResultClientIntegrationTest {

    @Autowired
    public ResultClient resultClient;

    @Test
    public void testRetrieveAllResultForLeague_givenJupilierLeague_shouldReturnList() {
        Optional<List<ResultDto>> resultDtosOptional = resultClient.retrieveAllResultForLeague(656L);
        assertThat(resultDtosOptional.isPresent()).isTrue();
        assertThat(resultDtosOptional.get().size()).isGreaterThan(0);
    }
}