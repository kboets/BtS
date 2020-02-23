package boets.bts.backend.repository.league;

import boets.bts.backend.domain.League;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class LeagueSpecs {

    public static Specification<League> getLeagueByCountryAndSeason(String countryCode, int season) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("countryCode"), countryCode);

            predicate = criteriaBuilder.and(
                    predicate, criteriaBuilder.equal(
                            root.get("season"), season));
            return predicate;
        };
    }
}
