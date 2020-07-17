package boets.bts.backend.repository.round;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.result.ResultSpecs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RoundRepositoryTest {

    @Autowired
    private RoundRepository roundRepository;

    @Test
    public void testUpdate() {
        List<Round> rounds = roundRepository.findAll();
        List<Round> updatedRound = rounds.stream().peek(round -> round.setCurrent(false)).collect(Collectors.toList());
        updatedRound = roundRepository.saveAll(updatedRound);
        assertThat(updatedRound.get(0).isCurrent()).isFalse();
    }
}