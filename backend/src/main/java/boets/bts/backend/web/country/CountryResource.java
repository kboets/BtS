package boets.bts.backend.web.country;

import boets.bts.backend.service.CountryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/")
public class CountryResource {

    private CountryService countryService;

    public CountryResource(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("getAllSelectableCountries")
    public List<CountryDto> retrieveAllSelectableCountries() {
        List<CountryDto> countryDtoList = countryService.retrieveAllSelectableCountries();
        return countryDtoList;
    }

    @GetMapping("getAllAvailableCountries")
    public List<AvailableCountryDto> retrieveAllAvailableCountries() {
        return countryService.retrieveAllAvailableCountries();
    }

    @PostMapping("saveAvailableCountries")
    public void saveAvailableCountries(@RequestBody List<CountryDto> countryDtoList) {
        countryService.saveAvailableCountries(countryDtoList);
    }
}
