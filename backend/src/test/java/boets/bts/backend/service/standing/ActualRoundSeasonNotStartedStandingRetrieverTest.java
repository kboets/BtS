package boets.bts.backend.service.standing;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.standing.retriever.ActualRoundSeasonNotStartedStandingRetriever;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActualRoundSeasonNotStartedStandingRetrieverTest {

    @InjectMocks
    private ActualRoundSeasonNotStartedStandingRetriever actualRoundSeasonNotStartedStandingRetriever;

    @Mock
    private AdminService adminService;

    @Test
    public void accept_givenValidUnstartedSeasonActualRound_shouldReturnTrue() {
        LocalDate startSeason = LocalDate.of(2021, 12, 9);
        League league = League.LeagueBuilder.aLeague().withSeason(2021).withStartSeason(startSeason).build();
        Round currentRound = Round.RoundBuilder.aRound().withRoundNumber(3).build();
        when(adminService.getCurrentSeason()).thenReturn(2021);
        assertThat(actualRoundSeasonNotStartedStandingRetriever.accept(league, currentRound, 3)).isTrue();
    }

    @Test
    public void accept_givenValidStartedSeasonActualRound_shouldReturnFalse() {
        LocalDate startSeason = LocalDate.of(2021, 8, 9);
        League league = League.LeagueBuilder.aLeague().withSeason(2021).withStartSeason(startSeason).build();
        Round currentRound = Round.RoundBuilder.aRound().withRoundNumber(3).build();
        when(adminService.getCurrentSeason()).thenReturn(2021);
        assertThat(actualRoundSeasonNotStartedStandingRetriever.accept(league, currentRound, 3)).isFalse();
    }

    @Test
    public void accept_givenOldSeasonActualRound_shouldReturnFalse() {
        LocalDate startSeason = LocalDate.of(2020, 8, 9);
        League league = League.LeagueBuilder.aLeague().withSeason(2020).withStartSeason(startSeason).build();
        Round currentRound = Round.RoundBuilder.aRound().withRoundNumber(3).build();
        when(adminService.getCurrentSeason()).thenReturn(2021);
        assertThat(actualRoundSeasonNotStartedStandingRetriever.accept(league, currentRound, 3)).isFalse();
    }
}