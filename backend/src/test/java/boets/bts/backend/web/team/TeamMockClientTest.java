package boets.bts.backend.web.team;

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
public class TeamMockClientTest {

    @Autowired
    private TeamMockClient teamMockClient;

    @Test
    public void retrieveTeamsOfLeague_givenJupilerLeague2020_shouldReturn18Teams() {
        Optional<List<TeamDto>> teamOptionalDtos = teamMockClient.retrieveTeamsOfLeague(2660L);
        assertThat(teamOptionalDtos.isPresent()).isTrue();
        assertThat(teamOptionalDtos.get().size()).isEqualTo(18);
    }
}