package boets.bts.backend.service;

import boets.bts.backend.domain.Country;
import boets.bts.backend.repository.CountryRepository;
import boets.bts.backend.web.CountryClient;
import boets.bts.backend.web.dto.CountryDto;
import boets.bts.backend.web.dto.CountryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InitService {

    private Logger logger = LoggerFactory.getLogger(InitService.class);

    private CountryClient countryClient;
    private CountryRepository countryRepository;
    private CountryMapper countryMapper;

    public InitService(CountryRepository countryRepository, CountryClient countryClient, CountryMapper countryMapper) {
        this.countryClient = countryClient;
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        this.initAllCountries();
    }

    public void initAllCountries() {
        List<Country> countryList = countryRepository.findAll();
        if(countryList.isEmpty()) {
            List<CountryDto> dtos = countryClient.getAllCountries();
            countryRepository.saveAll(countryMapper.toCountries(dtos));
        }
    }
}
