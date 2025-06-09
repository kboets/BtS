package boets.bts.backend.repository.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Team;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

public class ResultSpecs {

    public static Specification<Result> forLeague(League league) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("league"), league);
    }

    public static Specification<Result> forRound(int roundNumber) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("roundNumber"), roundNumber);
    }

    public static Specification<Result> forRounds(List<Integer> roundNumbers) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            CriteriaBuilder.In<Integer> inClause = criteriaBuilder.in(root.get("roundNumber"));
            for(Integer roundNumber: roundNumbers) {
                inClause.value(roundNumber);
            }
            return criteriaBuilder.and(inClause);
        };
    }

    public static Specification<Result> forTeam(Team team) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicateHome = criteriaBuilder.equal(root.get("homeTeam"), team.getId());
            Predicate predicateAway = criteriaBuilder.equal(root.get("awayTeam"), team.getId());
            return criteriaBuilder.or(predicateHome, predicateAway);
        };
    }

    public static Specification<Result> getResultByLeagueAndRound(League league, String round) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("round"), round);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("league"), league.getId()));
            return predicate;
        };
    }

    public static Specification<Result> allFinishedResultsCurrentRoundIncluded(League league, int roundNumber) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), league.getId());
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.lessThanOrEqualTo(
                            root.get("roundNumber"), roundNumber));
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("matchStatus"), "FT"));

            return predicate;
        };
    }

    public static Specification<Result> getAllFinishedResult(Long leagueId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("league"), leagueId);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("matchStatus"), "FT"));

            return predicate;
        };
    }

}
