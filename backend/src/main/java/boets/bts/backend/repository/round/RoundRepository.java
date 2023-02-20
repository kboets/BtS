package boets.bts.backend.repository.round;

import boets.bts.backend.domain.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoundRepository extends JpaRepository<Round, Long>, JpaSpecificationExecutor<Round> {


}
