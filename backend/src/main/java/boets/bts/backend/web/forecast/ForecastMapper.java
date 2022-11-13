package boets.bts.backend.web.forecast;

import boets.bts.backend.domain.Forecast;
import boets.bts.backend.domain.ForecastDetail;
import boets.bts.backend.web.algorithm.AlgorithmMapper;
import boets.bts.backend.web.league.LeagueMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LeagueMapper.class, AlgorithmMapper.class, ForecastDetailMapper.class})
public interface ForecastMapper {

    ForecastDto toDto(Forecast forecast);

    @InheritInverseConfiguration
    Forecast toDomainModel(ForecastDto forecastDto);

    List<ForecastDto> toDtos(List<ForecastDetail> forecastDetails);

    List<Forecast> toDomainModels(List<ForecastDto> forecastDtoList);
}
