package boets.bts.backend.repository.forecast;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.League;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.repository.league.LeagueRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("integration")
@Transactional
public class ForecastRepositoryTest {

    @Autowired
    private ForecastRepository forecastRepository;
    @Autowired
    private AlgorithmRepository algorithmRepository;
    @Autowired
    private LeagueRepository leagueRepository;


    @Test
    public void getResults_givingJupilerLeague2022_shouldReturnNoResult()  {
        Algorithm algorithm = algorithmRepository.getById(1L);
        League league = leagueRepository.getById(4366L);

        Optional<Forecast> optionalForecast = forecastRepository.findAll(Specification.where(ForecastSpecs.forAlgorithm(algorithm)).and(ForecastSpecs.forLeague(league)).and(ForecastSpecs.forRound(7)))
                .stream()
                .findFirst();
        assertThat(optionalForecast.isPresent()).isFalse();

    }

}