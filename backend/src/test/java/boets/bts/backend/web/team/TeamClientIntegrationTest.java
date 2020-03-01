package boets.bts.backend.web.team;

import boets.bts.backend.web.dto.TeamDto;
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
public class TeamClientIntegrationTest {

    @Autowired
    private TeamClient teamClient;


    @Test
    public void testRetrieveTeamsOfLeague_givenJupilerLeague_shouldReturn16Teams() {
        Optional<List<TeamDto>> teamDtos = teamClient.retrieveTeamsOfLeague(656L);
        assertThat(teamDtos.isPresent()).isTrue();
        assertThat(teamDtos.get().size()).isEqualTo(16);
    }
}