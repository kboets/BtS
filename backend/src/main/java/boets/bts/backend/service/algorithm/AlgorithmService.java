package boets.bts.backend.service.algorithm;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.web.algorithm.AlgorithmDto;
import boets.bts.backend.web.algorithm.AlgorithmMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlgorithmService {

    private final AlgorithmMapper algorithmMapper;
    private final AlgorithmRepository algorithmRepository;

    public AlgorithmService(AlgorithmMapper algorithmMapper, AlgorithmRepository algorithmRepository) {
        this.algorithmMapper = algorithmMapper;
        this.algorithmRepository = algorithmRepository;
    }

    public AlgorithmDto save(AlgorithmDto algorithmDto) {
        Algorithm algorithm = algorithmMapper.toDomainModel(algorithmDto);
        // verify if algorithm is set to current
        if(algorithm.isCurrent()) {
            List<Algorithm> algorithmList = algorithmRepository.findAll().stream()
                    .filter(Algorithm::isCurrent)
                    .filter(algorithm1 -> !algorithm1.getId().equals(algorithm.getId()))
                    .peek(algorithm1 -> algorithm1.setCurrent(false))
                    .collect(Collectors.toList());
            algorithmRepository.saveAll(algorithmList);
        }
        return algorithmMapper.toDto(algorithmRepository.save(algorithm));
    }
}
