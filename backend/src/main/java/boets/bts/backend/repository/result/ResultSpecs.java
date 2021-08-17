package boets.bts.backend.repository.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

public class ResultSpecs {

    public static Specification<Result> forLeague(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league);
            return predicate;
        };
    }

    public static Specification<Result> forRound(int roundNumber) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("roundNumber"), roundNumber);
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

    public static Specification<Result> allFinishedResultsCurrentRoundIncluded(League league, int roundNumber) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.lessThanOrEqualTo(
                            root.get("roundNumber"), roundNumber));
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
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


    public static Specification<Result> getFinishedResultForRounds(League league, List<String> rounds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("matchStatus"), "Match Finished"));
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get("round"));
            for(String round: rounds) {
                inClause.value(round);
            }
            predicate = criteriaBuilder.and(
                    predicate, inClause);

            return predicate;
        };
    }




}
