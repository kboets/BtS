package boets.bts.backend.service.algorithm;

import boets.bts.backend.domain.Algorithm;
import boets.bts.backend.repository.algorithm.AlgorithmRepository;
import boets.bts.backend.service.forecast2.ForecastService;
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
    private final ForecastService forecastService;

    public AlgorithmService(AlgorithmMapper algorithmMapper, AlgorithmRepository algorithmRepository, ForecastService forecastService) {
        this.algorithmMapper = algorithmMapper;
        this.algorithmRepository = algorithmRepository;
        this.forecastService = forecastService;
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
        // save or update the algorithm
        Algorithm saveOrUpdates = algorithmRepository.save(algorithm);
        // run the calculation of the forecast for the new algorithm
        if (!isUpdate) {
            new Thread(() -> forecastService.initForecastWithNewAlgorithm(saveOrUpdates)).start();
        }
        return algorithmMapper.toDto(saveOrUpdates);
    }

    public boolean delete(Long id) {
        Algorithm algorithmToBeDeleted = algorithmRepository.getReferenceById(id);
        try {
            boolean isForecastDeleted = forecastService.deleteByAlgorithm(algorithmToBeDeleted);
            if (isForecastDeleted) {
                algorithmRepository.delete(algorithmToBeDeleted);
            }
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
