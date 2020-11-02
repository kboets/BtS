package boets.bts.backend.service;

import boets.bts.backend.domain.Round;
import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.results.ResultDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ResultServiceIntegrationTest {

    @Autowired
    public ResultService resultService;
    @Autowired
    public RoundService roundService;

    @Test
    public void retrieveAllResultsForLeague_givingJupilerLeague2020_shouldReturnResult() throws Exception{
        List<ResultDto> resultDtos = resultService.retrieveAllResultsForLeague(2660L);
        assertThat(resultDtos.isEmpty()).isFalse();
        Round currentRound = roundService.retrieveCurrentRoundForLeagueAndSeason(2660L, WebUtils.getCurrentSeason());
        assertThat(currentRound).isNotNull();
        assertThat(resultDtos.get(resultDtos.size()-1).getRound()).isEqualTo(currentRound.getRound());
    }
}