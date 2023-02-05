package boets.bts.backend.service.algorithm;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.web.algorithm.AlgorithmDto;
import boets.bts.backend.web.algorithm.AlgorithmMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    public AlgorithmDto save(AlgorithmDto algorithmDto, boolean isUpdate) {
        Algorithm algorithm = algorithmMapper.toDomainModel(algorithmDto);
        // verify if algorithm is set to current
        if(!isUpdate && algorithm.isCurrent()) {
            List<Algorithm> algorithmList = algorithmRepository.findAll().stream()
                    .filter(Algorithm::isCurrent)
                    .filter(algorithm1 -> !algorithm1.getId().equals(algorithm.getId()))
                    .peek(algorithm1 -> algorithm1.setCurrent(false))
                    .collect(Collectors.toList());
            algorithmRepository.saveAll(algorithmList);
        }
        return algorithmMapper.toDto(algorithmRepository.save(algorithm));
    }

    public boolean delete(Long id) {
        try {
            algorithmRepository.deleteById(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<AlgorithmDto> getAll() {
        List<Algorithm> all = algorithmRepository.findAll();
        return algorithmMapper.toDtos(all);
    }

    public List<AlgorithmDto> getAllButCurrent() {
        return algorithmMapper.toDtos(algorithmRepository.findAll().stream()
                .filter(algorithm -> !algorithm.isCurrent())
                .collect(Collectors.toList()));
    }

    public AlgorithmDto getCurrent() {
        Optional<Algorithm> algorithmOptional = algorithmRepository.findAll().stream().filter(Algorithm::isCurrent).findFirst();
        return algorithmOptional.map(algorithmMapper::toDto).orElse(null);
    }
}
