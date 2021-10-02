package boets.bts.backend.repository.standing;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Standing;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class StandingSpecs {

    public static Specification<Standing> forLeague(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league.getId());
            return predicate;
        };
    }

    public static Specification<Standing> forRound(int roundNumber) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("roundNumber"), roundNumber);
            return predicate;
        };
    }

    public static Specification<Standing> forSeason(int season) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("season"), season);
            return predicate;
        };
    }

    public static Specification<Standing> getStandingsByLeagueAndRound(League league, int roundNumber, int season) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league.getId());
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("roundNumber"), roundNumber));
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("season"), season));
            return predicate;
        };
    }

}
