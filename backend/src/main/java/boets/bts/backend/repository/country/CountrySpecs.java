package boets.bts.backend.repository.country;

import boets.bts.backend.domain.Country;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class CountrySpecs {

    public static Specification<Country> getAvailableCountryByCountryCode(String countryCode) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("countryCode"), countryCode);
            return predicate;
        };
    }
}
