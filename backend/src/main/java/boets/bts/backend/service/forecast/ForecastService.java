package boets.bts.backend.service.forecast;

import boets.bts.backend.service.forecast.calculator.ForecastData;
import boets.bts.backend.service.forecast.calculator.ForecastDataCollector;
import boets.bts.backend.service.forecast.score.ScoreCalculatorHandler;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class ForecastService {
    private static final Logger logger = LoggerFactory.getLogger(ForecastService.class);

    private final ForecastDataCollector forecastDataCollector;
    private final LeagueMapper leagueMapper;
    private final ResultMapper resultMapper;
    private final ScoreCalculatorHandler scoreCalculatorHandler;

    public ForecastService(ForecastDataCollector forecastDataCollector, LeagueMapper leagueMapper,
                           ResultMapper resultMapper, ScoreCalculatorHandler scoreCalculatorHandler) {
        this.forecastDataCollector = forecastDataCollector;
        this.leagueMapper = leagueMapper;
        this.resultMapper = resultMapper;
        this.scoreCalculatorHandler = scoreCalculatorHandler;
    }

    public List<Forecast> calculateForecast() {
        List<Forecast> forecasts = new ArrayList<>();
        //get the forecast data
//        List<ForecastData> forecastDataList = forecastDataCollector.collectForecastData();
//
//        //create forecast
//        for(ForecastData forecastData: forecastDataList) {
//            Forecast forecast = new Forecast();
//            LeagueDto leagueDto = leagueMapper.toLeagueDto(forecastData.getLeague());
//            forecast.setLeague(leagueDto);
//            List<TeamDto> teams = leagueDto.getTeamDtos();
//            //create forecast details
//            for (TeamDto teamDto : teams) {
//                ForecastDetail forecastDetail = createForecastDetail(forecastData, teamDto);
//                forecast.getForecastDetails().add(forecastDetail);
//            }
//            forecasts.add(forecast);
//        }
//
//        //calculate score
//        for(Forecast forecast: forecasts) {
//            List<ForecastDetail> forecastDetails = forecast.getForecastDetails();
//            for(ForecastDetail forecastDetail : forecastDetails) {
//                scoreCalculatorHandler.calculateScore(forecastDetail, getForeCastDataForLeague(forecast.getLeague().getLeague_id(), forecastDataList), forecastDetails);
//            }
//            //final score
//            for(ForecastDetail forecastDetail : forecastDetails) {
//                TeamDto opponent = forecastDetail.getNextOpponent();
//                ForecastDetail otherTeamForecastDetail = forecastDetails.stream().filter(forecastDetail1 -> forecastDetail1.getTeam().getTeamId().equals(opponent.getTeamId()))
//                        .findFirst().orElseThrow(() -> new IllegalStateException(String.format("Could not find a team with teamid %s in the list of forecastdetail", opponent.getTeamId())));
//                int score = forecastDetail.getResultScore() - otherTeamForecastDetail.getResultScore();
//                forecastDetail.setScore(forecastDetail.getScore() + score);
//            }
//            forecastDetails.sort(Comparator.comparing(ForecastDetail::getScore, Comparator.reverseOrder()));
//        }


        return forecasts;
    }








}
