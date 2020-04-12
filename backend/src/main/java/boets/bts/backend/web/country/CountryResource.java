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
        List<CountryDto> countryDtoList = countryService.retrieveAllCountries();
        return countryDtoList;
    }

//    @PostMapping("save")
//    public void saveAvailableCountries(@RequestBody List<CountryDto> countryDtoList) {
//        countryServi(countryDtoList);
//    }
}
