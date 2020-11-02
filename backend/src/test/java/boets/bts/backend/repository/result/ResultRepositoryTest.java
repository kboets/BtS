package boets.bts.backend.repository.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.service.round.RoundService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ResultRepositoryTest {

    @Autowired
    private ResultRepository resultRepository;

    private String round;

    @Before
    public void init()  {
        round = "Regular_Season_-_10";
    }

    @Test
    public void getResultByLeagueAndRound_givingJupiler2020_shouldReturnResult()  {
        List<Result> result = resultRepository.findAll(ResultSpecs.getResultByLeagueAndRound(2660L, round));
        assertThat(result.isEmpty()).isFalse();
    }
}