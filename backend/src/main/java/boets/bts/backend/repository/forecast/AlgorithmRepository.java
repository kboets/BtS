package boets.bts.backend.repository.forecast;

import boets.bts.backend.domain.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlgorithmRepository extends JpaRepository<Algorithm, Long>, JpaSpecificationExecutor<Algorithm> {
}
