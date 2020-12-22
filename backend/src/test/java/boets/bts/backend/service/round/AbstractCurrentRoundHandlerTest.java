package boets.bts.backend.service.round;

import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.round.RoundRepository;
import boets.bts.backend.web.round.IRoundClient;
import boets.bts.backend.web.round.RoundMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCurrentRoundHandlerTest {


    private CurrentRoundNotValidHandler currentRoundHandler;
    private Round round;

    @Before
    public void init() {
        round = new Round();
        currentRoundHandler = new CurrentRoundNotValidHandler(null, null, null);
    }

    @Test
    public void verifyRetrievedRound_givenCurrentRoundFriday_shouldReturnSameRound() {
        LocalDate friday = LocalDate.of(2020, 12, 18);
        assertThat(friday.getDayOfWeek() == DayOfWeek.FRIDAY);
        round.setRound("Regular_Season_-_6");
        Round verified = currentRoundHandler.verifyRetrievedRound(round, friday);
        assertThat(verified.getRound()).isEqualTo("Regular_Season_-_6");

    }

    @Test
    public void verifyRetrievedRound_givenCurrentRoundTuesday_shouldReturnSameRound() {
        LocalDate friday = LocalDate.of(2020, 12, 22);
        assertThat(friday.getDayOfWeek() == DayOfWeek.TUESDAY);
        round.setRound("Regular_Season_-_6");
        Round verified = currentRoundHandler.verifyRetrievedRound(round, friday);
        assertThat(verified.getRound()).isEqualTo("Regular_Season_-_5");

    }

    @Test
    public void verifyRetrievedRound_givenCurrentRoundTuesday2_shouldReturnSameRound() {
        LocalDate friday = LocalDate.of(2020, 12, 22);
        assertThat(friday.getDayOfWeek() == DayOfWeek.TUESDAY);
        round.setRound("1st_Phase_-_25");
        Round verified = currentRoundHandler.verifyRetrievedRound(round, friday);
        assertThat(verified.getRound()).isEqualTo("1st_Phase_-_24");

    }
}