package boets.bts.backend.repository.league;

import boets.bts.backend.domain.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LeagueRepository extends JpaRepository<League, Long>, JpaSpecificationExecutor<League> {

}
