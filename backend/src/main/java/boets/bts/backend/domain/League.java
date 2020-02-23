package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "LEAGUE")
public class League implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "league_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "current")
    private boolean current;

    @Column(nullable = false, name = "start_season")
    private LocalDate startSeason;

    @Column(nullable = false, name = "end_season")
    private LocalDate endSeason;

    @Column(nullable = false)
    private int season;

    @Column(nullable = false, name = "country_code")
    private String countryCode;

    @Column
    private String logo;

    @Column
    private String  flag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public LocalDate getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(LocalDate startSeason) {
        this.startSeason = startSeason;
    }

    public LocalDate getEndSeason() {
        return endSeason;
    }

    public void setEndSeason(LocalDate endSeason) {
        this.endSeason = endSeason;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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


    public static final class LeagueBuilder {
        private Long id;
        private String name;
        private boolean current;
        private LocalDate startSeason;
        private LocalDate endSeason;
        private int season;
        private String countryCode;
        private String logo;
        private String  flag;

        private LeagueBuilder() {
        }

        public static LeagueBuilder aLeague() {
            return new LeagueBuilder();
        }

        public LeagueBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public LeagueBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public LeagueBuilder withIsCurrent(boolean isCurrent) {
            this.current = isCurrent;
            return this;
        }

        public LeagueBuilder withStartSeason(LocalDate startSeason) {
            this.startSeason = startSeason;
            return this;
        }

        public LeagueBuilder withEndSeason(LocalDate endSeason) {
            this.endSeason = endSeason;
            return this;
        }

        public LeagueBuilder withSeason(int season) {
            this.season = season;
            return this;
        }

        public LeagueBuilder withCountryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public LeagueBuilder withLogo(String logo) {
            this.logo = logo;
            return this;
        }

        public LeagueBuilder withFlag(String flag) {
            this.flag = flag;
            return this;
        }

        public League build() {
            League league = new League();
            league.setId(id);
            league.setName(name);
            league.setStartSeason(startSeason);
            league.setEndSeason(endSeason);
            league.setSeason(season);
            league.setCountryCode(countryCode);
            league.setLogo(logo);
            league.setFlag(flag);
            league.current = this.current;
            return league;
        }
    }
}
