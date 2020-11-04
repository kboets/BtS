package boets.bts.backend.web.round;

import boets.bts.backend.web.league.LeagueDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Profile("mock")
public class RoundMockClient implements IRoundClient {


    @Override
    public Optional<RoundDto> getCurrentRoundForLeagueAndSeason(long leagueId, int season) {
        RoundDto roundDto = new RoundDto();
        roundDto.setCurrent(true);
        roundDto.setRound("Regular_Season_-_11");
        LeagueDto leagueDto = new LeagueDto();
        leagueDto.setLeague_id(Long.toString(leagueId));
        roundDto.setLeagueDto(leagueDto);
        return Optional.of(roundDto);
    }

    @Override
    public Optional<List<RoundDto>> getAllRoundsForLeagueAndSeason(int season, long leagueId) {
        return Optional.empty();
    }
}
