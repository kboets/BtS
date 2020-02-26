package boets.bts.backend.web.dto;

import boets.bts.backend.domain.Country;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDto toCountryDto(Country country);
    @InheritInverseConfiguration
    List<CountryDto> toCountryDtoList(List<Country> countryList);

    Country toCountry(CountryDto countryDto);

    List<Country> toCountries(List<CountryDto> countryDtos);
}
