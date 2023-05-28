package boets.bts.backend.web.country;

import boets.bts.backend.service.CountryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/country/")
public class CountryResource {

    private CountryService countryService;

    public CountryResource(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("get")
    public List<CountryDto> retrieveAllSelectableCountries() {
        List<CountryDto> countryDtoList = countryService.retrieveAllowedCountries();
        return countryDtoList;
    }

    @PutMapping("/toSelected")
    public List<CountryDto> updateToSelectedCountry(@RequestBody List<String> countryCodes) {
        return countryService.updateToSelectedCountries(countryCodes);
    }
}
