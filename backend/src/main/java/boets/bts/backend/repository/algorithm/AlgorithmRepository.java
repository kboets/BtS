package boets.bts.backend.repository.algorithm;

import boets.bts.backend.domain.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, Long>, JpaSpecificationExecutor<Algorithm> {
}
