package boets.bts.backend.service;

import boets.bts.backend.domain.Country;
import boets.bts.backend.repository.country.CountryRepository;
import boets.bts.backend.repository.country.CountrySpecs;
import boets.bts.backend.service.leagueDefiner.LeagueBettingDefinerFactory;
import boets.bts.backend.web.country.CountryDto;
import boets.bts.backend.web.country.CountryMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final LeagueBettingDefinerFactory leagueBettingDefinerFactory;


    public CountryService(CountryRepository countryRepository, CountryMapper countryMapper, LeagueBettingDefinerFactory leagueBettingDefinerFactory) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.leagueBettingDefinerFactory = leagueBettingDefinerFactory;
    }

    public List<CountryDto> retrieveAllowedCountries() {
        List<Country> countryList = countryRepository.findAll();
        List<String> allowedCountryCodes = leagueBettingDefinerFactory.getAllowedCountryCodes();
        List<Country> selectableCountries = countryList.stream()
                .filter(country -> allowedCountryCodes.contains(country.getCountryCode()))
                .collect(Collectors.toList());
        return countryMapper.toCountryDtoList(selectableCountries);
    }

    public List<String> getSelectedCountryCodes() {
        List<Country> countryList = countryRepository.findAll();
        List<String> allowedCountryCodes = leagueBettingDefinerFactory.getAllowedCountryCodes();
        return countryList.stream()
                .filter(Country::isSelected)
                .map(Country::getCountryCode)
                .filter(allowedCountryCodes::contains)
                .collect(Collectors.toList());

    }

    public List<String> getAllowedCountryCodes() {
        List<Country> countryList = countryRepository.findAll();
        List<String> allowedCountryCodes = leagueBettingDefinerFactory.getAllowedCountryCodes();
        return countryList.stream()
                .map(Country::getCountryCode)
                .filter(allowedCountryCodes::contains)
                .collect(Collectors.toList());

    }

    public List<CountryDto> updateToSelectedCountries(List<String> countryCodes) {
        List<Country> toBeUpdated = new ArrayList<>();
        for (String coutryCode : countryCodes) {
            Country country = countryRepository.findAll(CountrySpecs.getAvailableCountryByCountryCode(coutryCode)).get(0);
            toBeUpdated.add(country);
        }
        countryRepository.saveAll(toBeUpdated);
        return countryMapper.toCountryDtoList(countryRepository.findAll(CountrySpecs.getCountryBySelected()));
     }

}
