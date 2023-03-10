package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "LEAGUE")
public class League implements Serializable {

    @Id
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

    @Column
    private boolean selected;

    @OneToMany(mappedBy = "league", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Team> teams;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Round> rounds;

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

    public Set<Team> getTeams() {
        if(this.teams == null) {
            teams = new HashSet<>();
        }
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public Set<Round> getRounds() {
        if(this.rounds == null) {
            rounds = new HashSet<>();
        }
        return rounds;
    }

    public void setRounds(Set<Round> rounds) {
        this.rounds = rounds;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        League league = (League) o;
        return current == league.current &&
                season == league.season &&
                selected == league.selected &&
                name.equals(league.name) &&
                Objects.equals(startSeason, league.startSeason) &&
                Objects.equals(endSeason, league.endSeason) &&
                countryCode.equals(league.countryCode) &&
                Objects.equals(logo, league.logo) &&
                Objects.equals(flag, league.flag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, current, startSeason, endSeason, season, countryCode, logo, flag, selected);
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
        private boolean selected;
        private Set<Team> teams;
        private Set<Round> rounds;

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

        public LeagueBuilder withCurrent(boolean current) {
            this.current = current;
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

        public LeagueBuilder withSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public LeagueBuilder withTeams(Set<Team> teams) {
            this.teams = teams;
            return this;
        }

        public LeagueBuilder withRounds(Set<Round> rounds) {
            this.rounds = rounds;
            return this;
        }

        public League build() {
            League league = new League();
            league.setId(id);
            league.setName(name);
            league.setCurrent(current);
            league.setStartSeason(startSeason);
            league.setEndSeason(endSeason);
            league.setSeason(season);
            league.setCountryCode(countryCode);
            league.setLogo(logo);
            league.setFlag(flag);
            league.setSelected(selected);
            league.setTeams(teams);
            league.setRounds(rounds);
            return league;
        }
    }
}
