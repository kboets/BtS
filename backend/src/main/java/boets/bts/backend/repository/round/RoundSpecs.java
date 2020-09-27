package boets.bts.backend.repository.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.List;

public class RoundSpecs {

    public static Specification<Round> getRoundByName(String roundName) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("round"), roundName);

            return predicate;
        };
    }

    public static Specification<Round> getCurrentRound() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("current"), true);
            return predicate;
        };
    }

    public static Specification<Round> getRoundsByLeagueId(Long leagueId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), leagueId);

            return predicate;
        };
    }
}
