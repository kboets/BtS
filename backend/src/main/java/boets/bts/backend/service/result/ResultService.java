package boets.bts.backend.service.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.TeamService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.forecast.LeagueResultsDto;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import javassist.runtime.Desc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultService {

    private Logger logger = LoggerFactory.getLogger(ResultService.class);

    private ResultRepository resultRepository;
    private ResultHandlerSelector resultHandlerSelector;
    private ResultMapper resultMapper;
    private RoundService roundService;
    private LeagueRepository leagueRepository;
    private LeagueMapper leagueMapper;
    private TeamService teamService;

    public ResultService(ResultRepository resultRepository, ResultHandlerSelector resultHandlerSelector, ResultMapper resultMapper, RoundService roundService,
                         LeagueRepository leagueRepository, TeamService teamService, LeagueMapper leagueMapper) {
        this.resultRepository = resultRepository;
        this.resultHandlerSelector = resultHandlerSelector;
        this.resultMapper = resultMapper;
        this.roundService = roundService;
        this.leagueRepository = leagueRepository;
        this.teamService = teamService;
        this.leagueMapper = leagueMapper;
    }

    public List<ResultDto> retrieveAllResultsForLeague(Long leagueId) throws Exception {
        Round currentRound = roundService.getCurrentRoundForLeague(leagueId, WebUtils.getCurrentSeason());
        League league = leagueRepository.findById(leagueId).orElseThrow(()-> new NotFoundException(String.format("Could not find league with id %s", leagueId)));
        List<Result> allResults = resultRepository.findAll(ResultSpecs.getResultByLeague(league));
        List<ResultDto> allHandledResults = resultMapper.toResultDtos(allResults);
        List<Result> allNonFinishedResults = resultRepository.findAll(ResultSpecs.getAllNonFinishedResultUntilRound(league, currentRound));
        Optional<ResultHandler> resultOptionalHandler = resultHandlerSelector.select(allResults, allNonFinishedResults, currentRound.getRound());
        if(resultOptionalHandler.isPresent()) {
            logger.info("Not all results are found for the current league {} ", leagueId);
            ResultHandler resultHandler = resultOptionalHandler.get();
            List<Result> results = resultHandler.getResult(leagueId, allNonFinishedResults, currentRound.getRound());
            allHandledResults.addAll(resultMapper.toResultDtos(results));
        }
        return allHandledResults;
    }

    public List<LeagueResultsDto> retrieveAllFinishedResults(boolean isSelected)  {
        List<LeagueResultsDto> leaguesResults = new ArrayList<>();
        List<League> allLeagues = new ArrayList<>();
        if(isSelected) {
            allLeagues.addAll(leagueRepository.findAll(LeagueSpecs.getLeagueBySeasonAndSelected(WebUtils.getCurrentSeason(), true)));
        } else {
            allLeagues.addAll(leagueRepository.findAll(LeagueSpecs.getLeagueBySeasonAndSelected(WebUtils.getCurrentSeason(), false)));
        }
        for (League selectedLeague : allLeagues) {
            List<Result> resultForLeague = resultRepository.findAll(ResultSpecs.getAllFinishedResult(selectedLeague), Sort.by("id").descending());
            LeagueResultsDto leagueResultsDto = new LeagueResultsDto();
            leagueResultsDto.setResults(resultMapper.toResultDtos(resultForLeague));
            leagueResultsDto.setLeague(leagueMapper.toLeagueDto(selectedLeague));
            leaguesResults.add(leagueResultsDto);
        }
        return leaguesResults;
    }

    @Scheduled(cron = "0/15 * * * * ?")
    public void scheduleResults() throws Exception {
        List<Long> leagueIds = leagueRepository.findAll().stream().map(league -> league.getId()).collect(Collectors.toList());
        for (Long leagueId : leagueIds) {
            this.retrieveAllResultsForLeague(leagueId);
        }
    }


}
