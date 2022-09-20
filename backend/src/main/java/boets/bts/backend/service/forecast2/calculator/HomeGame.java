package boets.bts.backend.service.forecast2.calculator;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastDetail;
import boets.bts.backend.repository.result.ResultRepository;
import boets.bts.backend.repository.result.ResultSpecs;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class HomeGame implements ScoreCalculator {

    private final ResultRepository resultRepository;

    public HomeGame(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public void calculate(ForecastDetail forecastDetail) {
        // get all results for this team
        //List<In>
        //resultRepository.findAll(ResultSpecs.)


    }
}
