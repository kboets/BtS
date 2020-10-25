package boets.bts.backend.repository.team;

import boets.bts.backend.domain.Team;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class TeamSpecs {

    public static Specification<Team> getTeamByTeamId(Long teamId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("teamId"), teamId);

            return predicate;
        };
    }
}
