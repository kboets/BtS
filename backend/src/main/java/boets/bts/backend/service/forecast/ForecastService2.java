package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ForecastService2 {
    private static final Logger logger = LoggerFactory.getLogger(ForecastService.class);
    private final ForecastDataCollector forecastDataCollector;

    public ForecastService2(ForecastDataCollector forecastDataCollector) {
        this.forecastDataCollector = forecastDataCollector;
    }

    public void calculateForecast() {
        //get the forecast data
        List<ForecastData> forecastDataList = forecastDataCollector.collectForecastData();
        //get all home teams
        //List<Team> homeTeams = forecastDataList.stream().map(forecastData -> forecastData.getNextResult().getHomeTeam()).collect(Collectors.toList());
        //get the results of the home teams


    }

    protected List<Result> getLatestResultsOfTeam(List<Result> allFinishedResult, Team team) {
        //allFinishedResult.stream()
        return null;
    }


}
