package boets.bts.backend.web.algorithm;

import boets.bts.backend.domain.AlgorithmPoints;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlgorithmPointsMapper {

    AlgorithmPointsDto toDto(AlgorithmPoints algorithmPoints);

    List<AlgorithmPointsDto> toDtos(List<AlgorithmPoints> algorithmPointsList);

    @InheritInverseConfiguration
    AlgorithmPoints toDomainModel(AlgorithmPointsDto algorithmPointsDto);
    @InheritInverseConfiguration
    List<AlgorithmPoints> toDomainModels(List<AlgorithmPointsDto> algorithmPointsList);
}
