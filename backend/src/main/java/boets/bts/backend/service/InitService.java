package boets.bts.backend.service;

import boets.bts.backend.domain.AvailableCountry;
import boets.bts.backend.domain.Country;
import boets.bts.backend.repository.country.AvailableCountryRepository;
import boets.bts.backend.repository.country.CountryRepository;
import boets.bts.backend.web.CountryClient;
import boets.bts.backend.web.dto.CountryDto;
import boets.bts.backend.web.dto.CountryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InitService {

    private Logger logger = LoggerFactory.getLogger(InitService.class);

    private CountryClient countryClient;
    private CountryRepository countryRepository;
    private AvailableCountryRepository availableCountryRepository;
    private CountryMapper countryMapper;

    public InitService(CountryRepository countryRepository, CountryClient countryClient, CountryMapper countryMapper, AvailableCountryRepository availableCountryRepository) {
        this.countryClient = countryClient;
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.availableCountryRepository = availableCountryRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initMetaData() {
        this.initAllCountries();
        this.initDefaultAvailableCountry();
    }

    private void initDefaultAvailableCountry() {
        List<AvailableCountry> availableCountries = availableCountryRepository.findAll();
        if(availableCountries.isEmpty()) {
            //TODO retrieve country from repo
        }

    }

    public void initAllCountries() {
        List<Country> countryList = countryRepository.findAll();
        if(countryList.isEmpty()) {
            logger.info("No country codes yet available, start download.");
            this.handleCountryDtos(countryClient.getAllCountries());
        }
    }

    private void handleCountryDtos(Optional <List<CountryDto>> countryDtos) {
        if(countryDtos.isPresent()) {
            logger.info("download of country codes done, saving in repository");
            countryRepository.saveAll(countryMapper.toCountries(countryDtos.get()));
        } else {
            logger.info("download of country codes not successful ...");
        }
    }
}
