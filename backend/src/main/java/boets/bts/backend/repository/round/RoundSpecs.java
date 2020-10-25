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

    public static Specification<Round> getRoundByNameAndLeague(long leagueId, String roundName) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("round"), roundName);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("league"), leagueId));
            return predicate;
        };
    }

    public static Specification<Round> getCurrentRoundForSeason(long leagueId, int season) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("current"), true);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("season"), season));

            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("league"), leagueId));

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