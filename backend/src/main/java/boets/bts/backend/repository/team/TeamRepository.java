package boets.bts.backend.repository.team;

import boets.bts.backend.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, String> {
}
