package boets.bts.backend.service;

import boets.bts.backend.domain.League;
import boets.bts.backend.repository.league.LeagueRepository;
import boets.bts.backend.repository.league.LeagueSpecs;
import boets.bts.backend.web.dto.LeagueDto;
import boets.bts.backend.web.dto.LeagueMapper;
import boets.bts.backend.web.dto.LeagueMapperImpl;
import boets.bts.backend.web.league.ILeagueClient;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LeagueServiceIntegrationTest {


    @Autowired
    private LeagueService leagueService;


    @Test
    public void testCurrentSeasonLeaguesForCountry_given3Leagues_shouldOnlyReturnBettingAvailableLeagues() {
        List<LeagueDto> result = leagueService.getCurrentSeasonLeaguesForCountry("BE");
        //assert
        Assertions.assertThat(result.size()).isEqualTo(2);

    }



}