package boets.bts.backend.repository.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class ResultSpecs {

    public static Specification<Result> getResultByLeague(Long leagueId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), leagueId);

            return predicate;
        };
    }

    public static Specification<Result> getResultByLeagueAndRound(Long leagueId, String round) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("round"), round);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("league"), leagueId));
            return predicate;
        };
    }
}
