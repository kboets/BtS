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
    @Column
    private Boolean selected;

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

    public boolean isSelected() {
        if (selected == null) {
            setSelected(false);
        }
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return selected == country.selected && Objects.equals(countryCode, country.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, selected);
    }
}
