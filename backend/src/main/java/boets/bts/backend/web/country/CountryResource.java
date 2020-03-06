package boets.bts.backend.web.country;

import boets.bts.backend.domain.AvailableCountry;
import boets.bts.backend.service.CountryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/")
public class CountryResource {

    private CountryService countryService;

    public CountryResource(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("getAllSelectableCountries")
    public List<CountryDto> retrieveAllCountries() {
        List<CountryDto> countryDtoList = countryService.retrieveAllSelectableCountries();
        return countryDtoList;
    }

    @GetMapping("getAllAvailableCountries")
    public List<AvailableCountryDto> retrieveAllAvailableCountries() {
        return countryService.retrieveAllAvailableCountries();
    }
}
