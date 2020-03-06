package boets.bts.backend.web.country;

public class AvailableCountryDto {

    private String countryCode;
    private String country;
    private String flag;
    private int season;
    private String seasonString;

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

    public String getSeasonString() {
        return seasonString;
    }

    public void setSeasonString(String seasonString) {
        this.seasonString = seasonString;
    }
}
