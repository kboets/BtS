package boets.bts.backend.repository.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
@Transactional
public class ResultRepositoryTest {

    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private LeagueRepository leagueRepository;


    @Test
    @Sql(scripts = {"/boets/bts/backend/service/result/league-be.sql", "/boets/bts/backend/service/result/result-be-valid.sql"})
    public void getResults_givingJupilerLeague2022_shouldReturnResult()  {
        List<Integer> rounds = new ArrayList<>();
        rounds.add(1);
        rounds.add(2);

        List<Result> result = resultRepository.findAll(ResultSpecs.forRounds(rounds));
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(18);
    }

    @Test
    @Sql(scripts = {"/boets/bts/backend/service/result/league-be.sql", "/boets/bts/backend/service/result/result-be-valid.sql"})
    public void getResultsAndLeague_givingJupilerLeague2022_shouldReturnResult()  {
        List<Integer> rounds = new ArrayList<>();
        rounds.add(1);
        rounds.add(2);
        League league = leagueRepository.findById(4366L).orElseThrow(IllegalArgumentException::new);
        List<Result> result = resultRepository.findAll(ResultSpecs.forRounds(rounds).and(ResultSpecs.forLeague(league)));
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(18);
    }

    @Test
    @Sql(scripts = {"/boets/bts/backend/service/result/league-be.sql", "/boets/bts/backend/service/result/result-be-valid.sql"})
    public void getResultsAndLeagueAndTeam_givingOHLeuven_shouldReturnResult()  {
        List<Integer> rounds = new ArrayList<>();
        rounds.add(1);
        rounds.add(2);
        League league = leagueRepository.findById(4366L).orElseThrow(IllegalArgumentException::new);
        Team ohLeuven = league.getTeams().stream().filter(team1 -> team1.getTeamId().equals(260L)).findFirst().orElseThrow(IllegalArgumentException::new);
        List<Result> result = resultRepository.findAll(ResultSpecs.forTeam(ohLeuven).and(ResultSpecs.forRounds(rounds).and(ResultSpecs.forLeague(league))));
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(2);
    }


}