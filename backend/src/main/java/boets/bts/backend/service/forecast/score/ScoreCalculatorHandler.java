package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.service.forecast.TeamPerformanceQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ScoreCalculatorHandler {

    private List<ScoreCalculator> calculatorList;

    @Autowired
    public ScoreCalculatorHandler(List<ScoreCalculator> calculatorList) {
        this.calculatorList = calculatorList;
    }

    public void calculateScore(ForecastDetail forecastDetail, Map<String, TeamPerformanceQualifier> teamPerformanceQualifierMap) {
        for(ScoreCalculator scoreCalculator: calculatorList) {
            scoreCalculator.calculateScore(forecastDetail, teamPerformanceQualifierMap);
        }
    }
}
