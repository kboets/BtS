package boets.bts.backend.service.forecast;

import boets.bts.backend.service.forecast.score.ScoreCalculatorHandler;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.results.ResultMapper;
import boets.bts.backend.web.team.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

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
        List<ForecastData> forecastDataList = forecastDataCollector.collectForecastData();

        //create forecast
        for(ForecastData forecastData: forecastDataList) {
            Forecast forecast = new Forecast();
            LeagueDto leagueDto = leagueMapper.toLeagueDto(forecastData.getLeague());
            forecast.setLeague(leagueDto);
            List<TeamDto> teams = leagueDto.getTeamDtos();
            //create forecast details
            for (TeamDto teamDto : teams) {
                ForecastDetail forecastDetail = createForecastDetail(forecastData, teamDto);
                forecast.getForecastDetails().add(forecastDetail);
            }
            forecasts.add(forecast);
        }

        //calculate score
        for(ForecastData forecastData: forecastDataList) {
            for(Forecast forecast: forecasts) {
                List<ForecastDetail> forecastDetails = forecast.getForecastDetails();
                for(ForecastDetail forecastDetail : forecastDetails) {
                    scoreCalculatorHandler.calculateScore(forecastDetail, forecastData, forecastDetails);
                }
                //final score
                for(ForecastDetail forecastDetail : forecastDetails) {
                    TeamDto opponent = forecastDetail.getNextOpponent();
                    ForecastDetail otherTeamForecastDetail = forecastDetails.stream().filter(forecastDetail1 -> forecastDetail1.getTeam().getTeamId().equals(opponent.getTeamId()))
                            .findFirst().orElseThrow(() -> new IllegalStateException(String.format("Could not find a team with teamid %s in the list of forecastdetail", opponent.getTeamId())));
                    int score = forecastDetail.getResultScore() - otherTeamForecastDetail.getResultScore();
                    forecastDetail.setScore(forecastDetail.getScore() + score);
                }
                forecastDetails.sort(Comparator.comparing(ForecastDetail::getScore, Comparator.reverseOrder()));
            }
        }
        return forecasts;
    }

    private ForecastDetail createForecastDetail(ForecastData forecastData, TeamDto teamDto) {
        ForecastDetail forecastDetail = new ForecastDetail();
        forecastDetail.setTeam(teamDto);
        List<ResultDto> allNextResults = resultMapper.toResultDtos(forecastData.getNextResults());
        // get next result for team
        Optional<ResultDto> nextResultOptional = this.nextResultForTeam(teamDto, allNextResults);
        if(nextResultOptional.isPresent()) {
            ResultDto nextResultDto = nextResultOptional.get();
            forecastDetail.setNextResult(nextResultDto);
            // get next opponent
            if(nextResultDto.getHomeTeam().getTeamId().equals(teamDto.getTeamId())) {
                forecastDetail.setNextOpponent(nextResultDto.getAwayTeam());
            } else {
                forecastDetail.setNextOpponent(nextResultDto.getHomeTeam());
            }
        }
        // get all previous finished result for team
        List<ResultDto> resultDtos = resultMapper.toResultDtos(forecastData.getFinishedResults());
        forecastDetail.setResults(resultDtos.stream().filter(resultDto -> resultDto.getHomeTeam().getTeamId().equals(teamDto.getTeamId())
                || resultDto.getAwayTeam().getTeamId().equals(teamDto.getTeamId())).collect(Collectors.toList()));

        return forecastDetail;
    }

    private Optional<ResultDto> nextResultForTeam(TeamDto teamDto, List<ResultDto> resultDtos) {
        return resultDtos.stream().filter(resultDto -> resultDto.getHomeTeam().getTeamId().equals(teamDto.getTeamId())
                || resultDto.getAwayTeam().getTeamId().equals(teamDto.getTeamId()))
                .findFirst();
    }





}
