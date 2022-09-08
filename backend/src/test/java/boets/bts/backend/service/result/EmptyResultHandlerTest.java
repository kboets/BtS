package boets.bts.backend.service.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("integration")
public class EmptyResultHandlerTest {

    @Autowired
    private EmptyResultHandler resultHandler;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    private long leagueId;
    private League league;

    @Before
    public void init()  {
        leagueId= 2660L;
        league = leagueRepository.findById(leagueId).orElse(null);
    }

    @Test
    public void accepts_givenEmptyAllResultHandler_shouldReturnTrue() {
        List<Result> allResultHandler = new ArrayList<>();
        assertTrue(resultHandler.accepts(allResultHandler));
    }

    @Test
    public void accepts_givenNonEmptyAllResultHandler_shouldReturnFalse() {
        List<Result> allResultHandler = new ArrayList<>();
        allResultHandler.add(Result.ResultBuilder.aResult()
                .withLeague(new League())
                .build());
        assertFalse(resultHandler.accepts(allResultHandler));
    }

    @Test
    public void getResult_givenJupilerLeague_shouldReturnList() throws Exception {
        List<Result> allMissingResults = resultRepository.findAll(ResultSpecs.forLeague(league));
        assertThat(allMissingResults.isEmpty()).isTrue();
        List<Result> result = resultHandler.getResult(league);
        assertThat(result.size()).isEqualTo(27);
    }
}