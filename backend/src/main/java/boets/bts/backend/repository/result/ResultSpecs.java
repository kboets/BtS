package boets.bts.backend.repository.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class ResultSpecs {

    public static Specification<Result> getResultByLeagueAndSeason(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league.getId());

            return predicate;
        };
    }
}
