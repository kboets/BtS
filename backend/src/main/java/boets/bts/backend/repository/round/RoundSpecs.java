package boets.bts.backend.repository.round;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Round;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class RoundSpecs {

    public static Specification<Round> roundNumber(Integer roundNumber) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("roundNumber"), roundNumber);
            return predicate;
        };
    }



    public static Specification<Round> getRoundByNameAndLeague(League league, String roundName) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("round"), roundName);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("league"), league));
            return predicate;
        };
    }

    public static Specification<Round> getCurrentRoundForSeason(League league, int season) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("current"), 1);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("season"), season));

            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("league"), league));

            return predicate;
        };
    }

    public static Specification<Round> league(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league);

            return predicate;
        };
    }

    public static Specification<Round> getRoundsByRoundNumber(Integer roundNumber) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("roundNumber"), roundNumber);

            return predicate;
        };
    }

}
