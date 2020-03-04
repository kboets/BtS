package boets.bts.backend.repository.country;

import boets.bts.backend.domain.AvailableCountry;
import boets.bts.backend.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AvailableCountryRepository extends JpaRepository<AvailableCountry, Long>, JpaSpecificationExecutor<AvailableCountry> {
}
