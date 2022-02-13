package boets.bts.backend.service;


import boets.bts.backend.domain.Country;
import boets.bts.backend.domain.League;
import boets.bts.backend.repository.country.CountryRepository;
import boets.bts.backend.service.round.RoundService;
import boets.bts.backend.service.standing.StandingService;
import boets.bts.backend.web.country.CountryClient;
import boets.bts.backend.web.country.CountryDto;
import boets.bts.backend.web.country.CountryMapper;
import boets.bts.backend.web.league.LeagueMapper;
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
public class InitService implements AdminChangeListener {

    private Logger logger = LoggerFactory.getLogger(InitService.class);

    private final CountryClient countryClient;
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final LeagueService leagueService;
    private final RoundService roundService;
    private final LeagueMapper leagueMapper;
    private final AdminService adminService;
    private final StandingService standingService;

    public InitService(CountryClient countryClient, CountryRepository countryRepository, CountryMapper countryMapper, LeagueService leagueService, RoundService roundService,
                       LeagueMapper leagueMapper, AdminService adminService, StandingService standingService) {
        this.countryClient = countryClient;
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.leagueService = leagueService;
        this.roundService = roundService;
        this.leagueMapper = leagueMapper;
        this.adminService = adminService;
        this.standingService = standingService;
        this.adminService.addAdminChangeListener(this);

    }

    @EventListener(ApplicationReadyEvent.class)
    public void initMetaData() {
        this.initAllCountries();
        List<League> leagues = this.initAllAvailableLeagues();
        //this.initCurrentRounds(leagues);
        //this.initStandings(leagues);
    }


    public void initAllCountries() {
        List<Country> countryList = countryRepository.findAll();
        if(countryList.isEmpty()) {
            logger.info("No country codes yet available, start download.");
            this.handleCountryDtos(countryClient.getAllCountries());
        }
    }

    public List<League> initAllAvailableLeagues() {
        return leagueMapper.toLeagues(leagueService.getCurrentLeagues());
    }

    public void initCurrentRounds(List<League> leagues) {
        if(! adminService.isHistoricData()) {
            leagues.forEach(league ->  roundService.getCurrentRoundForLeague(league.getId(), adminService.getCurrentSeason()));
        } else {
            leagues.forEach(league ->  roundService.setCurrentRoundForHistoricData(league.getId(), adminService.getCurrentSeason()));
        }
    }

    public void initStandings(List<League> leagues) {
        leagues.forEach(league -> standingService.getCurrentStandingForLeague(league.getId()));
    }

    private void handleCountryDtos(Optional <List<CountryDto>> countryDtos) {
        if(countryDtos.isPresent()) {
            logger.info("download of country codes done, saving in repository");
            countryRepository.saveAll(countryMapper.toCountries(countryDtos.get()));
        } else {
            logger.info("download of country codes not successful ...");
        }
    }

    @Override
    public void onAdminChanged() {
        logger.info("Season changed -> call the init method");
        this.initMetaData();
    }
}
