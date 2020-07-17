package boets.bts.backend.service;


import boets.bts.backend.domain.Country;
import boets.bts.backend.repository.country.CountryRepository;
import boets.bts.backend.web.country.CountryClient;
import boets.bts.backend.web.country.CountryDto;
import boets.bts.backend.web.country.CountryMapper;
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
    private CountryMapper countryMapper;
    private LeagueService leagueService;

    public InitService(CountryRepository countryRepository, CountryClient countryClient, CountryMapper countryMapper, LeagueService leagueService) {
        this.countryClient = countryClient;
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.leagueService = leagueService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initMetaData() {
        this.initAllCountries();
        this.initAllAvailableLeagues();
    }


    public void initAllCountries() {
        List<Country> countryList = countryRepository.findAll();
        if(countryList.isEmpty()) {
            logger.info("No country codes yet available, start download.");
            this.handleCountryDtos(countryClient.getAllCountries());
        }
    }

    public void initAllAvailableLeagues() {
        leagueService.getLeaguesCurrentSeason(false);
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
