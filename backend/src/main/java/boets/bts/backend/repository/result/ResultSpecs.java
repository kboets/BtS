package boets.bts.backend.repository.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;

public class ResultSpecs {

    public static Specification<Result> getResultByLeague(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league);

            return predicate;
        };
    }

    public static Specification<Result> getResultByLeagueAndRound(League league, String round) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("round"), round);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("league"), league));
            return predicate;
        };
    }

    public static Specification<Result> getAllNonFinishedResultUntilRound(League league, Round round) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.lessThan(
                            root.get("eventDate"), round.getCurrentDate()));
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.notEqual(
                            root.get("matchStatus"), "Match Finished"));

            return predicate;
        };
    }

    public static Specification<Result> getAllFinishedResult(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("matchStatus"), "Match Finished"));

            return predicate;
        };
    }



}
