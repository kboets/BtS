package boets.bts.backend.repository.algorithm;

import boets.bts.backend.domain.Algorithm;
import org.springframework.data.jpa.domain.Specification;

public class AlgorithmSpecs {

    public static Specification<Algorithm> forName(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<Algorithm> forType(String type) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Algorithm> current() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("current"), true);
    }
}
