package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    private List<Team> teams;

    @OneToMany(mappedBy = "league" , cascade = CascadeType.ALL)
    private List<Round> rounds;

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

    public List<Team> getTeams() {
        if(this.teams == null) {
            return new ArrayList<>();
        }
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
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
}
