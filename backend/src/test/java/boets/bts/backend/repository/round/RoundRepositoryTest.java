package boets.bts.backend.repository.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RoundRepositoryTest {

    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    private long leagueId;
    private League league;
    private League otherLeague;




    @Before
    public void init() {
        otherLeague = leagueRepository.findById(656L).orElse(null);
        List<Round> rounds = roundRepository.findAll(RoundSpecs.getRoundsByLeagueId(otherLeague));
        Round first = rounds.get(0);
        first.setCurrent(true);
        first.setCurrentDate(LocalDate.now());
        leagueId= 2660L;
        league = leagueRepository.findById(leagueId).orElse(null);
        roundRepository.save(first);
    }

    @After
    public void after() {
        List<Round> rounds = roundRepository.findAll(RoundSpecs.getRoundsByLeagueId(otherLeague));
        Round first = rounds.get(0);
        first.setCurrent(false);
        roundRepository.save(first);
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
        List<Round> rounds = roundRepository.findAll(RoundSpecs.getRoundsByLeagueId(otherLeague));
        assertThat(rounds.size()).isEqualTo(30);
    }

    @Test
    public void testGetCurrentRoundForSeason_givingJupilerLeague2019_shouldReturnRound() {
        Optional<Round> currentRound = roundRepository.findOne(RoundSpecs.getCurrentRoundForSeason(league, 2019));
        assertThat(currentRound.isPresent()).isTrue();
        assertThat(currentRound.get().isCurrent());
    }
}