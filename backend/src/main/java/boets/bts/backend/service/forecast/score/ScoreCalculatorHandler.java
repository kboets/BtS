package boets.bts.backend.service.forecast.score;

import boets.bts.backend.service.forecast.calculator.ForecastData;
import boets.bts.backend.service.forecast.ForecastDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoreCalculatorHandler {

    private List<ScoreCalculator> calculatorList;

    @Autowired
    public ScoreCalculatorHandler(List<ScoreCalculator> scoreCalculatorList) {
        this.calculatorList = scoreCalculatorList;
    }

    public void calculateScore(ForecastDetailDto forecastDetail, ForecastData forecastData, List<ForecastDetailDto>forecastDetails) {
        for(ScoreCalculator scoreCalculator: calculatorList) {
            scoreCalculator.calculateScore(forecastDetail, forecastData,forecastDetails);
        }
    }
}
