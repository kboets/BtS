package boets.bts.backend.web.algorithm;

import boets.bts.backend.service.algorithm.AlgorithmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/algorithm/")
public class AlgorithmResource {

    private static final Logger logger = LoggerFactory.getLogger(AlgorithmResource.class);
    private final AlgorithmService algorithmService;

    public AlgorithmResource(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @GetMapping("all")
    public List<AlgorithmDto> getAllAlgorithm() {
        return algorithmService.getAll();
    }

    @GetMapping("allNotCurrent")
    public List<AlgorithmDto> getAllButCurrentAlgorithm() {
        return algorithmService.getAllButCurrent();
    }

    @GetMapping("current")
    public AlgorithmDto getCurrentAlgoritm() {
        return algorithmService.getCurrent();
    }

    @PostMapping("save")
    public AlgorithmDto save(@RequestBody AlgorithmDto algorithmDto) {
        logger.info("saving algorithm {}", algorithmDto.getName());
        return  algorithmService.save(algorithmDto, false);
    }

    @PostMapping("update")
    public AlgorithmDto update(@RequestBody AlgorithmDto algorithmDto) {
        logger.info("update algorithm {}", algorithmDto.getName());
        return  algorithmService.save(algorithmDto, true);
    }

    @PostMapping("delete")
    public boolean delete(@RequestBody AlgorithmDto algorithmDto) {
        logger.info("delete algorithm {}", algorithmDto.getName());
        return  algorithmService.delete(algorithmDto.getAlgorithm_id());
    }
}
