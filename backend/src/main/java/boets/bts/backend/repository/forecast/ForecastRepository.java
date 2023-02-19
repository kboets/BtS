package boets.bts.backend.repository.forecast;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


public interface ForecastRepository extends JpaRepository<Forecast, Long> , JpaSpecificationExecutor<Forecast> {

    void deleteByLeague(League league);

    void deleteByAlgorithm(Algorithm algorithm);
}
