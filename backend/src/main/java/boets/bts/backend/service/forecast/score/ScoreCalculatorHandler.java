package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.ForecastData;
import boets.bts.backend.service.forecast.ForecastDetail;
import boets.bts.backend.service.forecast.TeamPerformanceQualifier;
import boets.bts.backend.web.results.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class ScoreCalculatorHandler {

    private List<ScoreCalculator> calculatorList;

    @Autowired
    public ScoreCalculatorHandler(List<ScoreCalculator> scoreCalculatorList) {
        this.calculatorList = scoreCalculatorList;
    }

    public void calculateScore(ForecastDetail forecastDetail, ForecastData forecastData, List<ForecastDetail>forecastDetails) {
        for(ScoreCalculator scoreCalculator: calculatorList) {
            scoreCalculator.calculateScore(forecastDetail, forecastData,forecastDetails);
        }
    }
}
