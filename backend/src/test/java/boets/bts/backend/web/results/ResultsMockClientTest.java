package boets.bts.backend.web.results;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("mock")
@RunWith(SpringRunner.class)
public class ResultsMockClientTest {

    @Autowired
    private ResultMockClient resultsMockClient;

    private Long jupilerLeague2020_id;

    @Before
    public void init() {
        jupilerLeague2020_id = 2660L;
    }
    @Test
    public void testRetrieveAllResultForLeague_givenResultJupilerLeague2020_shouldReturnResultsForFinished() {
        Optional<List<ResultDto>> optionalResultDtos = resultsMockClient.retrieveAllResultForLeague(jupilerLeague2020_id, 2020);
        assertThat(optionalResultDtos.isPresent()).isTrue();
        List<ResultDto> results = optionalResultDtos.get();
        assertThat(results.isEmpty()).isFalse();
        assertThat(results.get(0).getLeague().getLeague_id()).isEqualTo("2660");
    }
}