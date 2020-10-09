package boets.bts.backend.repository.round;

import boets.bts.backend.domain.Round;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RoundRepositoryTest {

    @Autowired
    private RoundRepository roundRepository;

    @Test
    public void testUpdate() {
        System.out.println(LocalDate.now());
        List<Round> rounds = roundRepository.findAll();
        List<Round> updatedRound = rounds.stream().peek(round -> round.setCurrent(false)).collect(Collectors.toList());
        updatedRound = roundRepository.saveAll(updatedRound);
        assertThat(updatedRound.get(0).isCurrent()).isFalse();
    }

    @Test
    public void testRoundsByLeagueId() {
        List<Round> rounds = roundRepository.findAll(RoundSpecs.getRoundsByLeagueId(656L));
        assertThat(rounds.size()).isEqualTo(30);
    }
}