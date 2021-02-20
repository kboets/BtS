package boets.bts.backend.web.results;

import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.web.exception.GeneralException;
import boets.bts.backend.web.forecast.LeagueResultsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping(value = "/api/result/")
public class ResultResource {

    private Logger logger = LoggerFactory.getLogger(ResultResource.class);

    private ResultService resultService;
    private LeagueRepository leagueRepository;

    public ResultResource(ResultService resultService, LeagueRepository leagueRepository) {
        this.resultService = resultService;
        this.leagueRepository = leagueRepository;
    }

    @GetMapping("all/{id}")
    public List<ResultDto> retrieveResultForLeague(@PathVariable("id") Long leagueId) {
        logger.info("get result for league  with id {} ", leagueId);
        try {
            List<ResultDto> resultDtos = resultService.retrieveAllResultsForLeague(leagueId);
            return resultDtos;
        } catch (Exception e) {
            logger.error("Something went wrong while getting results {} ", e);
            throw new GeneralException(e.getMessage());
        }
    }

    @GetMapping("allSelected")
    public List<LeagueResultsDto> retrieveResultsSelectedLeagues() {
        logger.info("get result for all selected leagues");
        try {
            return resultService.retrieveAllFinishedResults(true);
        } catch (Exception e) {
            logger.error("Something went wrong while getting results of all selected leagues {} ", e);
            throw new GeneralException(e.getMessage());
        }

    }

    @GetMapping("allNonSelected")
    public List<LeagueResultsDto> retrieveResultsNonSelectedLeagues() {
        logger.info("get result for all non selected leagues");
        try {
            return resultService.retrieveAllFinishedResults(false);
        } catch (Exception e) {
            logger.error("Something went wrong while getting results of all non selected leagues {} ", e);
            throw new GeneralException(e.getMessage());
        }

    }
}
