package boets.bts.backend.service.forecast;

import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class TeamPerformanceDefinerTest {

    private TeamPerformanceDefiner teamPerformanceDefiner;
    private String teamName;
    private TeamDto team;

    @Before
    public void init() {
        teamPerformanceDefiner = new TeamPerformanceDefiner();
        teamName = "Genk";
        team = ResultCreator.createTeamDTO4Name(teamName);
    }

    @Test
    public void determinePerformance_given6WonMatch_shouldReturnTopper() {
        List<ResultDto> winningTeams = ResultCreator.createWinningResultDTOS(teamName, 3, true);
        winningTeams.addAll(ResultCreator.createWinningResultDTOS(teamName, 3, false));
        assertThat(teamPerformanceDefiner.determinePerformance(team, winningTeams)).isEqualTo(TeamPerformanceQualifier.TOPPER);
    }

    @Test
    public void determinePerformance_given5WonMatchOneDraw_shouldReturnTopper() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 3, true);
        results4Team.addAll(ResultCreator.createWinningResultDTOS(teamName, 2, false));
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 1, false));
        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.TOPPER);
    }

    @Test
    public void determinePerformance_given5WonMatchOneLose_shouldReturnUitstekend() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 3, true);
        results4Team.addAll(ResultCreator.createWinningResultDTOS(teamName, 2, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 1, false));
        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.UITSTEKEND);
    }


    @Test
    public void determinePerformance_given4WonMatchTwoDraw_shouldReturnUitstekend() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 2, true);
        results4Team.addAll(ResultCreator.createWinningResultDTOS(teamName, 2, false));
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 2, false));
        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.UITSTEKEND);
    }

    @Test
    public void determinePerformance_given4Won1Draw1Lose_shouldReturnZeerGoed() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 2, true);
        results4Team.addAll(ResultCreator.createWinningResultDTOS(teamName, 2, false));
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 1, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 1, true));
        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.ZEER_GOED);
    }

    @Test
    public void determinePerformance_given4Won2Lost_shouldReturnGoed() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 4, true);
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 2, false));
        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.GOED);
    }

    @Test
    public void determinePerformance_given3Won3Draw_shouldReturnGoed() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 3, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 3, false));
        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.GOED);
    }

    @Test
    public void determinePerformance_given3Won2Draw1Lost_shouldReturnBehoorlijk() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 3, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 2, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 1, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.BEHOORLIJK);
    }

    @Test
    public void determinePerformance_given2Won3Draw1Lost_shouldReturnMatig() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 2, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 3, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 1, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.MATIG);
    }

    @Test
    public void determinePerformance_given2Won2Draw2Lost_shouldReturnOndermaats() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 2, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 2, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 2, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.ONDERMAATS);
    }

    @Test
    public void determinePerformance_given2Won1Draw3Lost_shouldReturnOndermaats() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 2, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 1, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 3, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.ONDERMAATS);
    }

    @Test
    public void determinePerformance_given2Won0Draw4Lost_shouldReturnSlecht() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 2, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 0, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 4, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.SLECHT);
    }

    @Test
    public void determinePerformance_given1Won5Draw0Lost_shouldReturnSlecht() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 1, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 5, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 0, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.SLECHT);
    }

    @Test
    public void determinePerformance_given1Won4Draw1Lost_shouldReturnZeerSlecht() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 1, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 4, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 1, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.ZEER_SLECHT);
    }

    @Test
    public void determinePerformance_given1Won3Draw2Lost_shouldReturnZeerSlecht() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 1, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 3, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 2, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.ZEER_SLECHT);
    }

    @Test
    public void determinePerformance_given1Won2Draw3Lost_shouldReturnZeerSlecht() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 1, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 2, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 3, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.ZEER_SLECHT);
    }

    @Test
    public void determinePerformance_given1Won1Draw4Lost_shouldReturnFlopper() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 1, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 1, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 4, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.FLOPPER);
    }

    @Test
    public void determinePerformance_given0Won5Draw1Lost_shouldReturnFlopper() {
        List<ResultDto> results4Team = ResultCreator.createWinningResultDTOS(teamName, 0, true);
        results4Team.addAll(ResultCreator.createDrawResultDTO(teamName, 5, false));
        results4Team.addAll(ResultCreator.createLosingResultDTO(teamName, 1, false));

        assertThat(teamPerformanceDefiner.determinePerformance(team, results4Team)).isEqualTo(TeamPerformanceQualifier.FLOPPER);
    }

}