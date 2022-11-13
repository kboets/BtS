package boets.bts.backend.web.forecast;

import boets.bts.backend.domain.ForecastDetail;
import boets.bts.backend.web.results.ResultMapper;
import boets.bts.backend.web.team.TeamMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TeamMapper.class, ResultMapper.class})
public interface ForecastDetailMapper {

    ForecastDetailDto toDto(ForecastDetail forecastDetail);

    @InheritInverseConfiguration
    ForecastDetail toDomainModel(ForecastDetailDto forecastDetailDto);

    List<ForecastDetailDto> toDtos(List<ForecastDetail> forecastDetailList);

    List<ForecastDetail> toDomainModels(List<ForecastDetailDto> forecastDetailDtos);



}
