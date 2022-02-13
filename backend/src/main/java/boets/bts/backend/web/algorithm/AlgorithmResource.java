package boets.bts.backend.web.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/algorithm/")
public class AlgorithmResource {

    private static final Logger logger = LoggerFactory.getLogger(AlgorithmResource.class);

    @GetMapping("all")
    public List<AlgorithmDto> getAllAlgorithm() {
        return Collections.emptyList();
    }
}
