package boets.bts.backend.service.standing.retriever;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Standing;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.repository.standing.StandingRepository;
import boets.bts.backend.repository.standing.StandingSpecs;
import boets.bts.backend.repository.team.TeamRepository;
import boets.bts.backend.repository.team.TeamSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.standing.StandingCalculator;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultMapper;
import boets.bts.backend.web.round.RoundMapper;
import boets.bts.backend.web.standing.StandingClient;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.standing.StandingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public abstract class AbstractStandingRetriever implements StandingRetriever {

    private static final Logger logger = LoggerFactory.getLogger(AbstractStandingRetriever.class);

    protected final LeagueMapper leagueMapper;
    protected final RoundMapper roundMapper;
    protected final StandingClient standingClient;
    protected final AdminService adminService;
    protected final StandingMapper standingMapper;
    protected final TeamRepository teamRepository;
    protected final StandingRepository standingRepository;
    protected final ResultRepository resultRepository;
    protected final ResultMapper resultMapper;
    private final StandingCalculator standingCalculator;

    public AbstractStandingRetriever(LeagueMapper leagueMapper, RoundMapper roundMapper, StandingClient standingClient, AdminService adminService, StandingMapper standingMapper, TeamRepository teamRepository, StandingRepository standingRepository, ResultRepository resultRepository, ResultMapper resultMapper) {
        this.leagueMapper = leagueMapper;
        this.roundMapper = roundMapper;
        this.standingClient = standingClient;
        this.adminService = adminService;
        this.standingMapper = standingMapper;
        this.teamRepository = teamRepository;
        this.standingRepository = standingRepository;
        this.resultRepository = resultRepository;
        this.resultMapper = resultMapper;
        this.standingCalculator = new StandingCalculator();
    }

    protected List<Standing> saveAndReturn(List<Standing> standings, League league, int roundNumber) {
        List<Standing> expandedStandings = standings.stream()
                .peek(standing -> standing.setLeague(league))
                .peek(standing -> standing.setTeam(teamRepository.findOne(TeamSpecs.getTeamByTeamId(standing.getTeam().getTeamId(), league))
                        .orElseThrow(() -> new NotFoundException(String.format("Could not find team with id %s", standing.getTeam().getTeamId())))))
                .peek(standing -> {
                    standing.setRoundNumber(roundNumber);
                    standing.setSeason(league.getSeason());
                })
                .collect(Collectors.toList());
        //save to db
        return standingRepository.saveAll(expandedStandings);
    }

        protected List<Standing> retrieveOrCalculateMissingStandings(int roundNumber, League league) {
        // verify if round is missing, if not, do nothing
        List<Standing> standingsForRound = standingRepository.findAll(StandingSpecs.forLeague(league).and(StandingSpecs.forRound(roundNumber)));
        if(!standingsForRound.isEmpty()) {
            return standingsForRound;
        }
        List<Result> results = resultRepository.findAll(ResultSpecs.allFinishedResultsCurrentRoundIncluded(league, roundNumber));
        List<StandingDto> calculatedStandingDtos = standingCalculator.calculateStandings(leagueMapper.toLeagueDto(league), resultMapper.toResultDtos(results), roundNumber);
        List<Standing> calculatedStandings = standingMapper.toStandings(calculatedStandingDtos);
        logStanding(calculatedStandings, roundNumber);
        //save to database
        return saveAndReturn(calculatedStandings, league, roundNumber);
    }

    private void logStanding(List<Standing> calculatedStanding, int roundNumber) {
        logger.info("Standing of round number {} : ", roundNumber);
        calculatedStanding.stream().forEach(calculatedStanding1 -> {
            logger.info("Place {} -> Team {} , points {}, won {}, lost {}, draw {} ", calculatedStanding1.getRank(), calculatedStanding1.getTeam().getName(),
                    calculatedStanding1.getPoints(), calculatedStanding1.getAllSubStanding().getWin(), calculatedStanding1.getAllSubStanding().getLose(), calculatedStanding1.getAllSubStanding().getDraw());
        });
        logger.info("**************************************************");
    }
}
