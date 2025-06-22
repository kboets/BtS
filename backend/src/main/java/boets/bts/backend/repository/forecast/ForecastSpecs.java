package boets.bts.backend.repository.forecast;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.League;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public class ForecastSpecs {

    public static Specification<Forecast> forAlgorithm(Algorithm algorithm) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("algorithm"), algorithm);
    }

    public static Specification<Forecast> forRound(Integer round) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("round"), round);
    }

    public static Specification<Forecast> forRounds(List<Integer> rounds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            CriteriaBuilder.In<Integer> inClause = criteriaBuilder.in(root.get("round"));
            for(Integer roundNumber: rounds) {
                inClause.value(roundNumber);
            }
            return criteriaBuilder.and(inClause);
        };
    }

    public static Specification<Forecast> forLeague(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("league"), league);
    }

    public static Specification<Forecast> forCurrentSeason(int season) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("season"), season);
    }
}
