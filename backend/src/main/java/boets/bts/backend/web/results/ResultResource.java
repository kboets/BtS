package boets.bts.backend.web.results;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.exception.GeneralException;
import boets.bts.backend.web.league.LeagueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    public Map<LeagueDto, List<ResultDto>> retrieveResultsSelectedLeagues() {
        logger.info("get result for all selected leagues");
        try {
            return resultService.retrieveAllResults(true);
        } catch (Exception e) {
            logger.error("Something went wrong while getting results of all selected leagues {} ", e);
            throw new GeneralException(e.getMessage());
        }

    }
}
