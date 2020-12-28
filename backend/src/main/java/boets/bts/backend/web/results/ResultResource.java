package boets.bts.backend.web.results;

import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.web.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(value = "/api/result/")
public class ResultResource {

    private Logger logger = LoggerFactory.getLogger(ResultResource.class);
    private ConcurrentHashMap<Long, List<ResultDto>> resultMap = new ConcurrentHashMap<>();

    private ResultService resultService;

    public ResultResource(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("all/{id}")
    public List<ResultDto> retrieveResultForLeague(@PathVariable("id") Long leagueId) {
        logger.info("get result for league  with id {} ", leagueId);
        try {
            if(!resultMap.containsKey(leagueId)) {
                List<ResultDto> resultDtos = resultService.retrieveAllResultsForLeague(leagueId);
                return resultDtos;
                //resultMap.put(leagueId, resultDtos);
            }
            //return resultMap.get(leagueId);
            return Collections.emptyList();

        } catch (Exception e) {
            logger.error("Something went wrong while getting results {} ", e);
            throw new GeneralException(e.getMessage());
        }
    }
}
