package boets.bts.backend.web.country;

import boets.bts.backend.domain.AvailableCountry;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvailableCountryMapper {

    AvailableCountryDto toAvailableCountryDto(AvailableCountry availableCountry);

    @InheritInverseConfiguration
    List<AvailableCountryDto> toAvailableCountryDtos(List<AvailableCountry> availableCountryList);

    AvailableCountry toAvailableCountry(AvailableCountryDto availableCountryDto);

    List<AvailableCountry> toAvailableCountries(List<AvailableCountryDto> availableCountryDtos);
}
