package boets.bts.backend.repository.forecast;

import boets.bts.backend.domain.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> , JpaSpecificationExecutor<Forecast> {
}
