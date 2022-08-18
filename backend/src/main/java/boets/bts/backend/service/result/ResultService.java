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
import boets.bts.backend.web.exception.NotFoundException;
import boets.bts.backend.web.forecast.LeagueResultsDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultService {

    private Logger logger = LoggerFactory.getLogger(ResultService.class);

    private final ResultRepository resultRepository;
    private final ResultHandlerSelector resultHandlerSelector;
    private final ResultMapper resultMapper;
    private final RoundService roundService;
    private final LeagueRepository leagueRepository;
    private final LeagueMapper leagueMapper;
    private final TeamService teamService;
    private final AdminService adminService;

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
        League league = leagueRepository.findById(leagueId).orElseThrow(()-> new NotFoundException(String.format("Could not find league with id %s", leagueId)));
        logger.info("start verifying missing results for league {} of season {} ", league.getName(), adminService.getCurrentSeason());
        List<Result> allResults = resultRepository.findAll(ResultSpecs.forLeague(league));
        Optional<ResultHandler> resultOptionalHandler = resultHandlerSelector.select(allResults);
        if(resultOptionalHandler.isPresent()) {
            ResultHandler resultHandler = resultOptionalHandler.get();
            resultHandler.getResult(league);
        }
        allResults = resultRepository.findAll(ResultSpecs.forLeague(league));

        return resultMapper.toResultDtos(allResults);
    }

    public List<ResultDto> retrieveAllResultForLeague(Long leagueId) throws Exception {
        //update first with new results
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not found a league with id %s", leagueId)));
        List<Result> resultForLeague = resultRepository.findAll(ResultSpecs.forLeague(league), Sort.by("id").descending());
        if(!adminService.isHistoricData() && !adminService.isTodayExecuted(AdminKeys.CRON_RESULTS) || resultForLeague.isEmpty()) {
            verifyMissingResults(leagueId);
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
                resultForLeague = resultRepository.findAll(ResultSpecs.getAllFinishedResult(selectedLeague.getId()), Sort.by("id").descending());
            } else {
                Round currentRound = roundService.getCurrentRoundForLeague(selectedLeague.getId(), adminService.getCurrentSeason());
                resultForLeague =  resultRepository.findAll(ResultSpecs.allFinishedResultsCurrentRoundIncluded(selectedLeague, currentRound.getRoundNumber()));
            }
            LeagueResultsDto leagueResultsDto = new LeagueResultsDto();
            leagueResultsDto.setResults(resultMapper.toResultDtos(resultForLeague));
            leagueResultsDto.setLeague(leagueMapper.toLeagueDto(selectedLeague));
            leaguesResults.add(leagueResultsDto);
        }
        return leaguesResults;
    }

    public boolean removeAllResultsForLeague (Long leagueId) {
        League league = leagueRepository.findById(leagueId).orElseThrow(() -> new NotFoundException(String.format("Could not find league with id %s ",leagueId)));
        resultRepository.deleteByLeague(league);
        adminService.executeAdmin(AdminKeys.CRON_RESULTS, "NOK");
        return true;
    }

    public void initResultService() {
        if(!adminService.isTodayExecuted(AdminKeys.CRON_RESULTS) && !adminService.isHistoricData()) {
            this.dailyUpdateResults();
        }
    }

    /**
     * Cron job each day at 3 AM
     */
    @Scheduled(cron ="0 0 3 * * *")
    public void scheduleResults()  {
        logger.info("Scheduler started to init results");
        this.initResultService();
    }

    private void dailyUpdateResults() {
        AtomicInteger numberOfAttempts = new AtomicInteger();
        RetryPolicy<Object> retryResultsPolicy = RetryPolicy.builder()
                .handle(Exception.class)
                .onRetry(executionEvent -> logger.warn("An exception occurred while updating results, retrying for the {} time", numberOfAttempts.incrementAndGet()))
                .withDelay(Duration.ofSeconds(30))
                .withMaxAttempts(3)
                .build();
        Failsafe.with(retryResultsPolicy)
                .onSuccess(result -> {
                    logger.info("Daily update of results successfully done");
                    adminService.executeAdmin(AdminKeys.CRON_RESULTS, "OK");
                })
                .onFailure(result -> {
                    logger.error("Daily update of results was not successfully after {} attempts, final attempt failed", numberOfAttempts.get());
                    adminService.executeAdmin(AdminKeys.CRON_RESULTS, "NOK");
                })
                .run(() -> {
                    List<Long> leagueIds = leagueRepository.findAll(LeagueSpecs.getLeagueBySeason(adminService.getCurrentSeason())).stream().map(League::getId).collect(Collectors.toList());
                    boolean allValidated = true;
                    for (Long leagueId : leagueIds) {
                        List<ResultDto> results = this.verifyMissingResults(leagueId);
                        if (results.isEmpty()) {
                            logger.error("Could not verify the results of league {}", leagueId);
                            allValidated = false;
                        }
                    }
                    if (!allValidated) {
                        throw new Exception("Not all results could be retrieved");
                    }
                });
    }



}
