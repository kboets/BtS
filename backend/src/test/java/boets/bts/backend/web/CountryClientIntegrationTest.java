package boets.bts.backend.web;

import boets.bts.backend.web.country.CountryClient;
import boets.bts.backend.web.country.CountryDto;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@Ignore
@SpringBootTest
@RunWith(SpringRunner.class)
public class CountryClientIntegrationTest {

    @Autowired
    private CountryClient countryClient;

    @Test
    public void testGetAllCountries_shouldReturnNotEmptyList() {
        Optional<List<CountryDto>> allCountries = countryClient.getAllCountries();
        assertThat(allCountries.isPresent()).isTrue();
        assertThat(allCountries.get().size()).isGreaterThan(0);
    }

}