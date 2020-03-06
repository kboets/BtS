package boets.bts.backend.domain;

import boets.bts.backend.web.WebUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "AVAILABLE_COUNTRY")
public class AvailableCountry implements Serializable {

    @Id
    @Column(name = "country_code")
    private String countryCode;
    @Column
    private String country;
    @Column
    private String flag;
    @Column
    private int season;

    public AvailableCountry(Country country) {
        this.country = country.getCountry();
        this.countryCode = country.getCountryCode();
        this.flag = country.getFlag();
        this.season = WebUtils.getCurrentSeason();
    }

    public AvailableCountry() {
    }

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

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvailableCountry)) return false;
        AvailableCountry that = (AvailableCountry) o;
        return countryCode.equals(that.countryCode) &&
                country.equals(that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, country);
    }
}
