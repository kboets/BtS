package boets.bts.backend.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "COUNTRY")
public class Country implements Serializable {

    @Id
    @Column(name = "country_code")
    private String countryCode;
    @Column
    private String country;
    @Column
    private String flag;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;
        Country country1 = (Country) o;
        return countryCode.equals(country1.countryCode) &&
                country.equals(country1.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, country);
    }
}
