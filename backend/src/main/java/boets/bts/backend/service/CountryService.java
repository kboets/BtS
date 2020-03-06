package boets.bts.backend.service;

import boets.bts.backend.domain.AvailableCountry;
import boets.bts.backend.domain.Country;
import boets.bts.backend.repository.country.AvailableCountryRepository;
import boets.bts.backend.repository.country.CountryRepository;
import boets.bts.backend.web.country.AvailableCountryDto;
import boets.bts.backend.web.country.AvailableCountryMapper;
import boets.bts.backend.web.country.CountryDto;
import boets.bts.backend.web.country.CountryMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private CountryRepository countryRepository;
    private AvailableCountryRepository availableCountryRepository;
    private CountryMapper countryMapper;
    private AvailableCountryMapper availableCountryMapper;

    static final List<String> allowedCountries = Arrays.asList("BE", "GB", "NL", "IT", "ES", "DE", "FR", "IT", "PT");

    public CountryService(CountryRepository countryRepository, AvailableCountryRepository availableCountryRepository, CountryMapper countryMapper, AvailableCountryMapper availableCountryMapper) {
        this.countryRepository = countryRepository;
        this.availableCountryRepository = availableCountryRepository;
        this.countryMapper = countryMapper;
        this.availableCountryMapper = availableCountryMapper;
    }

    public List<CountryDto> retrieveAllSelectableCountries() {
        List<AvailableCountry> availableCountries = availableCountryRepository.findAll();
        List<Country> countryList = countryRepository.findAll();
        List<Country> selectableCountries = countryList.stream()
                .filter(country -> allowedCountries.contains(country.getCountryCode()))
                .filter(country -> !availableCountries.contains(country))
                .collect(Collectors.toList());
        return countryMapper.toCountryDtoList(selectableCountries);
    }

    public List<AvailableCountryDto> retrieveAllAvailableCountries() {
        return availableCountryMapper.toAvailableCountryDtos(availableCountryRepository.findAll());
    }
}
