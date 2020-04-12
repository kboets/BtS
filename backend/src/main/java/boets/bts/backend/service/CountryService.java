package boets.bts.backend.service;

import boets.bts.backend.domain.Country;
import boets.bts.backend.repository.country.CountryRepository;
import boets.bts.backend.web.country.CountryDto;
import boets.bts.backend.web.country.CountryMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private CountryRepository countryRepository;
    private CountryMapper countryMapper;

    public static final List<String> allowedCountries = Arrays.asList("BE", "GB", "NL", "IT", "ES", "DE", "FR", "PT");

    public CountryService(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    public List<CountryDto> retrieveAllCountries() {
        List<Country> countryList = countryRepository.findAll();
        List<Country> selectableCountries = countryList.stream()
                .filter(country -> allowedCountries.contains(country.getCountryCode()))
                .collect(Collectors.toList());
        return countryMapper.toCountryDtoList(selectableCountries);
    }

}
