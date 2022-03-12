package boets.bts.backend.web.algorithm;

import boets.bts.backend.domain.Algorithm;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AlgorithmPointsMapper.class})
public interface AlgorithmMapper {

    @Mappings({
            @Mapping(target = "algorithm_id", source = "id")
    })
    AlgorithmDto toDto(Algorithm algorithm);

    @InheritInverseConfiguration
    Algorithm toDomainModel(AlgorithmDto algorithmDto);

    List<AlgorithmDto> toDtos(List<Algorithm> algorithmList);

    List<Algorithm> toDomainModels(List<AlgorithmDto> algorithmDtos);
}
