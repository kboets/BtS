package boets.bts.backend.web;

import boets.bts.backend.web.dto.CountryDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CountryClientIntegrationTest {

    @Autowired
    private CountryClient countryClient;

    @Test
    public void testGetAllCountries_shouldReturnNotEmptyList() {
        List<CountryDto> allCountries = countryClient.getAllCountries();
        assertThat(allCountries.size()).isGreaterThan(0);
    }

}