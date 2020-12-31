package boets.bts.backend.repository.standing;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Standing;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class StandingSpecs {

    public static Specification<Standing> getStandingsByLeague(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league);

            return predicate;
        };
    }
}
