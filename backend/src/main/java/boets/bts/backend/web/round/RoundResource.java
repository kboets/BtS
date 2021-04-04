package boets.bts.backend.web.round;

import boets.bts.backend.domain.Round;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.league.LeagueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/api/round/")
public class RoundResource {

    private Logger logger = LoggerFactory.getLogger(RoundResource.class);

    private RoundService roundService;
    private RoundMapper roundMapper;
    private AdminService adminService;
    private LeagueMapper leagueMapper;

    public RoundResource(RoundService roundService, RoundMapper roundMapper, AdminService adminService, LeagueMapper leagueMapper) {
        this.roundService = roundService;
        this.roundMapper = roundMapper;
        this.adminService = adminService;
        this.leagueMapper = leagueMapper;
    }

    @GetMapping("/current/{league_id}")
    public RoundDto getCurrentRoundForLeague(@PathVariable("league_id") Long league_id) {
        Round currentRound = roundService.getCurrentRoundForLeague(league_id, adminService.getCurrentSeason());
        return roundMapper.toRoundDto(currentRound);
    }

    @GetMapping("/all/{league_id}")
    public List<RoundDto> getRoundsForLeague(@PathVariable("league_id") Long league_id) {
        List<Round> rounds = roundService.getAllRoundsForLeague(league_id);
        List<RoundDto> roundDtos = roundMapper.toRoundDtos(rounds);
        roundDtos.sort(Comparator.comparing(RoundDto::getPlayRound));
        return roundDtos;
    }

    @GetMapping("/update/{league_id}/{round_id}")
    public RoundDto updateCurrentRounds(@PathVariable("league_id") Long league_id, @PathVariable("round_id") Long roundId) {
        return roundMapper.toRoundDto(roundService.updateCurrentRound(roundId, league_id));
    }

}
