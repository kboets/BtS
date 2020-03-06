package boets.bts.backend.web.country;

import boets.bts.backend.domain.AvailableCountry;
import org.mapstruct.InheritInverseConfiguration;

import java.util.List;

public interface AvailableCountryMapper {

    AvailableCountryDto toAvailableCountryDto(AvailableCountry availableCountry);

    @InheritInverseConfiguration
    List<AvailableCountryDto> toAvailableCountryDtos(List<AvailableCountry> availableCountryList);

    AvailableCountry toAvailableCountry(AvailableCountryDto availableCountryDto);

    List<AvailableCountry> toAvailableCountries(List<AvailableCountryDto> availableCountryDtos);
}
