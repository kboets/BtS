package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.League;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("integration")
@Transactional
public class ForecastServiceIntegrationTest {

    @Autowired
    private ForecastService forecastService;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private RoundService roundService;
    @Autowired
    private LeagueMapper leagueMapper;

    @Test
    public void calculateForecasts() {
        forecastService.calculateForecasts();
    }

    @Test
    public void getRecentFinishedResults_givenLeagues2019_shouldReturnCorrectResult() {
        //current round is 20 for jupiler league (verify this first if test is not working)
        List<League> leagueList = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(2019));
        Map<LeagueDto, List<ResultDto>> resultsForLeague = forecastService.getRecentFinishedResults(leagueMapper.toLeagueDtoList(leagueList));
        assertThat(resultsForLeague.isEmpty()).isFalse();
        for(Map.Entry<LeagueDto, List<ResultDto>> entry : resultsForLeague.entrySet()) {
            //jupiler league
            if(entry.getKey().getLeague_id().equals("656")) {
                List<ResultDto> results = entry.getValue();
                List<ResultDto> sortedResult = results.stream().sorted(Comparator.comparing(ResultDto::getRound)).collect(Collectors.toList());
                assertThat(sortedResult.get(0).getRound()).isEqualTo("Regular_Season_-_14");
                assertThat(sortedResult.get(sortedResult.size()-1).getRound()).isEqualTo("Regular_Season_-_19");
            }
        }
    }

    @Test
    public void initForecastWithTeams_givenJupilerLeague_shouldReturnForecast() {
        //current round is 20 for jupiler league (verify this first if test is not working)
        List<League> leagueList = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(2019));
        Map<LeagueDto, List<ResultDto>> resultsForLeague = forecastService.getRecentFinishedResults(leagueMapper.toLeagueDtoList(leagueList));
        for(Map.Entry<LeagueDto, List<ResultDto>> entry : resultsForLeague.entrySet()) {
            //jupiler league 2019
            if(entry.getKey().getLeague_id().equals("656")) {
                Forecast jupilerForeCast = forecastService.initForecastWithTeams(entry.getKey(), entry.getValue());
                assertThat(jupilerForeCast.getLeague()).isEqualTo(entry.getKey());
                List<ForecastDetail> forecastDetails = jupilerForeCast.getForecastDetails();
                ForecastDetail forecastDetail = forecastDetails.get(0);
                TeamDto team = forecastDetail.getTeam();
                forecastDetail.getResults()
                        .stream()
                        .forEach(result -> assertThat(result.getAwayTeam().equals(team) || result.getHomeTeam().equals(team)));
            }
        }
    }

}