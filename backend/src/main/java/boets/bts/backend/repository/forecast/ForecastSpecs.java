package boets.bts.backend.repository.forecast;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.League;
import org.springframework.data.jpa.domain.Specification;

public class ForecastSpecs {

    public static Specification<Forecast> forAlgorithm(Algorithm algorithm) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("algorithm"), algorithm);
    }

    public static Specification<Forecast> forRound(Integer round) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("round"), round);
    }

    public static Specification<Forecast> forLeague(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("league"), league);
    }
}
