package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.algorithm.AlgorithmService;
import boets.bts.backend.web.algorithm.AlgorithmDto;

public abstract class AbstractGameCalculator implements  ScoreCalculator {

    private final AlgorithmService algorithmService;

    public AbstractGameCalculator(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    protected AlgorithmDto getCurrentAlgorithm() {
        return this.algorithmService.getCurrent();
    }
}
