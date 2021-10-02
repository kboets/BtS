package boets.bts.backend.repository.team;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Team;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class TeamSpecs {

    public static Specification<Team> getTeamByTeamId(Long teamId, League league) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("teamId"), teamId);
            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("league"), league.getId()));
            return predicate;
        };
    }
}
