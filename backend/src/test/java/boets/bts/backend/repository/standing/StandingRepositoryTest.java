package boets.bts.backend.repository.standing;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.domain.Team;
import boets.bts.backend.repository.league.LeagueRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
@Transactional
public class StandingRepositoryTest {

    @Autowired
    private StandingRepository standingRepository;
    @Autowired
    private LeagueRepository leagueRepository;

    @Test
    @Sql(scripts = {"/boets/bts/backend/repository/standing/standing-be.sql"})
    public void getStanding_givingRoundAndTeam_shouldReturnStanding() {
        League league = leagueRepository.findById(4366L).orElseThrow(IllegalArgumentException::new);
        Team ohLeuven = league.getTeams().stream().filter(team1 -> team1.getTeamId().equals(260L)).findFirst().orElseThrow(IllegalArgumentException::new);
        Standing standing = standingRepository.findAll(StandingSpecs.forLeague(league).and(StandingSpecs.forTeam(ohLeuven).and(StandingSpecs.forRound(5)))).stream().findFirst().orElseThrow(IllegalArgumentException::new);
        assertThat(standing.getRoundNumber() == 5).isTrue();
        assertThat(standing.getTeam().getName()).isEqualTo("OH Leuven");
    }
}
