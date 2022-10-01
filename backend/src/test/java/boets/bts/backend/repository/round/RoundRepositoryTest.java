package boets.bts.backend.repository.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
@Ignore
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("mock")
public class RoundRepositoryTest {

    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    private League league;

    @Before
    public void init() {
        league = leagueRepository.findById(2660L).orElse(null);
    }

    @Test
    public void testUpdate() {
        List<Round> rounds = roundRepository.findAll();
        List<Round> updatedRound = rounds.stream().peek(round -> round.setCurrent(false)).collect(Collectors.toList());
        updatedRound = roundRepository.saveAll(updatedRound);
        assertThat(updatedRound.get(0).isCurrent()).isFalse();
    }

    @Test
    public void testRoundsByLeagueId() {
        List<Round> rounds = roundRepository.findAll(RoundSpecs.getRoundsByLeagueId(league));
        assertThat(rounds.size()).isEqualTo(34);
    }

    @Test
    public void testGetCurrentRoundForSeason_givingJupilerLeague2019_shouldReturnRound() {
        Optional<Round> currentRound = roundRepository.findOne(RoundSpecs.getCurrentRoundForSeason(league, 2020));
        assertThat(currentRound.isPresent()).isTrue();
        assertThat(currentRound.get().isCurrent());
    }
}