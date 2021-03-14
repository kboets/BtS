package boets.bts.backend.repository.result;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResultRepository extends JpaRepository<Result, Long>, JpaSpecificationExecutor<Result> {

    void deleteByLeague(League league);

}
