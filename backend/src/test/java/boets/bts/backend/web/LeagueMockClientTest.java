package boets.bts.backend.web;

import boets.bts.backend.dto.LeagueDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("mock")
@RunWith(SpringRunner.class)
public class LeagueMockClientTest {

    @Autowired
    private LeagueMockClient mockClient;

    @Test
    public void testRetrieveAllLeagues_givenCorrectFile_shouldReturnDto() {
        List<LeagueDto> leagues = mockClient.retrieveAllLeagues("BE", 2019);
        assertThat(leagues).isEqualTo(null);
    }

}