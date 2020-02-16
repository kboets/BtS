package boets.bts.backend.repository;

import boets.bts.backend.domain.League;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeagueRepository extends JpaRepository<League, Long> {

    List<League> findByCountryCodeAndSeason(String countryCode, int year);
}
