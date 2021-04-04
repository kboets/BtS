package boets.bts.backend.service.result;

import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.TeamService;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.forecast.LeagueResultsDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import liquibase.pro.packaged.E;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private AdminService adminService;

    public ResultService(ResultRepository resultRepository, ResultHandlerSelector resultHandlerSelector, ResultMapper resultMapper, RoundService roundService,
                         LeagueRepository leagueRepository, TeamService teamService, LeagueMapper leagueMapper, AdminService adminService) {
        this.resultRepository = resultRepository;
        this.resultHandlerSelector = resultHandlerSelector;
        this.resultMapper = resultMapper;
        this.roundService = roundService;
        this.leagueRepository = leagueRepository;
        this.teamService = teamService;
        this.leagueMapper = leagueMapper;
        this.adminService = adminService;
    }

    public List<ResultDto> verifyMissingResults(Long leagueId) throws Exception {
        Round currentRound = roundService.getCurrentRoundForLeague(leagueId, adminService.getCurrentSeason());
        League league = leagueRepository.findById(leagueId).orElseThrow(()-> new NotFoundException(String.format("Could not find league with id %s", leagueId)));
        List<Result> allResults = resultRepository.findAll(ResultSpecs.getResultByLeague(league));
        List<ResultDto> allResultDtos = resultMapper.toResultDtos(allResults);
        List<Result> allNonFinishedResults = resultRepository.findAll(ResultSpecs.getAllNonFinishedResultUntilRound(league, currentRound));
        Optional<ResultHandler> resultOptionalHandler = resultHandlerSelector.select(allResults, allNonFinishedResults, currentRound.getRound());
        if(resultOptionalHandler.isPresent()) {
            logger.info("Not all results are found for the current league {} and current round {}", league.getName(), currentRound.getRound());
            ResultHandler resultHandler = resultOptionalHandler.get();
            List<Result> results = resultHandler.getResult(leagueId, allNonFinishedResults, currentRound.getRound());
            allResultDtos.addAll(resultMapper.toResultDtos(results));
        }
        return allResultDtos;
    }

    public List<ResultDto> retrieveAllResultForLeague(Long leagueId) throws Exception {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not found a league with id %s", leagueId)));
        List<Result> resultForLeague = resultRepository.findAll(ResultSpecs.getResultByLeague(league), Sort.by("id").descending());
        if(resultForLeague.isEmpty() || !adminService.isTodayExecuted(AdminKeys.CRON_RESULTS) && !adminService.isHistoricData()) {
            verifyMissingResults(leagueId);
            adminService.executeAdmin(AdminKeys.CRON_RESULTS, null);
        }
        return resultMapper.toResultDtos(resultForLeague);
    }


    public List<LeagueResultsDto> retrieveAllFinishedResults(boolean isSelected)  {
        List<LeagueResultsDto> leaguesResults = new ArrayList<>();
        List<League> allLeagues = new ArrayList<>();
        //get all leagues
        if(isSelected) {
            allLeagues.addAll(leagueRepository.findAll(LeagueSpecs.getLeagueBySeasonAndSelected(adminService.getCurrentSeason(), true)));
        } else {
            allLeagues.addAll(leagueRepository.findAll(LeagueSpecs.getLeagueBySeasonAndSelected(adminService.getCurrentSeason(), false)));
        }
        //get all finished results for each league
        for (League selectedLeague : allLeagues) {
            List<Result> resultForLeague;
            if(! adminService.isHistoricData()) {
                resultForLeague = resultRepository.findAll(ResultSpecs.getAllFinishedResult(selectedLeague), Sort.by("id").descending());
            } else {
                Round currentRound = roundService.getCurrentRoundForLeague(selectedLeague.getId(), adminService.getCurrentSeason());
                resultForLeague =  resultRepository.findAll(ResultSpecs.allFinishedResultsCurrentRoundIncluded(selectedLeague, currentRound));
            }
            LeagueResultsDto leagueResultsDto = new LeagueResultsDto();
            leagueResultsDto.setResults(resultMapper.toResultDtos(resultForLeague));
            leagueResultsDto.setLeague(leagueMapper.toLeagueDto(selectedLeague));
            leaguesResults.add(leagueResultsDto);
        }
        return leaguesResults;
    }

    public boolean removeAllResultsForLeague (Long leagueId) throws Exception {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not find league with id %s ",leagueId)));
        resultRepository.deleteByLeague(league);
        verifyMissingResults(leagueId);
        return true;
    }

    // each 15 minutes
    @Scheduled(cron = "* 0/15 * * * ?")
    public void scheduleResults() throws Exception {
        if(!adminService.isTodayExecuted(AdminKeys.CRON_RESULTS)) {
            logger.info("Scheduler triggered to update results ..");
            List<Long> leagueIds = leagueRepository.findAll().stream().map(League::getId).collect(Collectors.toList());
            for (Long leagueId : leagueIds) {
                this.verifyMissingResults(leagueId);
            }
            adminService.executeAdmin(AdminKeys.CRON_RESULTS, "OK");
        }
    }


}
