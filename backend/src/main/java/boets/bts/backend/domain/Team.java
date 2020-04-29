package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "TEAM")
public class Team implements Serializable {

    @Id
    @Column(name = "team_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String logo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "league_id", referencedColumnName = "league_id")
    private League league;

    @Column(name = "stadium_name")
    private String stadiumName;

    @Column(name = "stadium_capacity")
    private int stadiumCapacity;

    @Column
    private String city;

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public int getStadiumCapacity() {
        return stadiumCapacity;
    }

    public void setStadiumCapacity(int stadiumCapacity) {
        this.stadiumCapacity = stadiumCapacity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public static final class TeamBuilder {
        private Long id;
        private String name;
        private String logo;
        private League league;
        private String stadiumName;
        private int stadiumCapacity;
        private String city;

        private TeamBuilder() {
        }

        public static TeamBuilder aTeam() {
            return new TeamBuilder();
        }

        public TeamBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public TeamBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public TeamBuilder withLogo(String logo) {
            this.logo = logo;
            return this;
        }

        public TeamBuilder withLeague(League league) {
            this.league = league;
            return this;
        }

        public TeamBuilder withStadiumName(String stadiumName) {
            this.stadiumName = stadiumName;
            return this;
        }

        public TeamBuilder withStadiumCapacity(int stadiumCapacity) {
            this.stadiumCapacity = stadiumCapacity;
            return this;
        }

        public TeamBuilder withCity(String city) {
            this.city = city;
            return this;
        }

        public Team build() {
            Team team = new Team();
            team.setId(id);
            team.setName(name);
            team.setLogo(logo);
            team.setLeague(league);
            team.setStadiumName(stadiumName);
            team.setStadiumCapacity(stadiumCapacity);
            team.setCity(city);
            return team;
        }
    }
}
