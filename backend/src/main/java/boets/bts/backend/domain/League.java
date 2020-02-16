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

    @Column
    private boolean isCurrent;

    @Column
    private LocalDate start_season;

    @Column
    private LocalDate end_season;

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
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public LocalDate getStart_season() {
        return start_season;
    }

    public void setStart_season(LocalDate start_season) {
        this.start_season = start_season;
    }

    public LocalDate getEnd_season() {
        return end_season;
    }

    public void setEnd_season(LocalDate end_season) {
        this.end_season = end_season;
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


    public static final class LeagueBuilder {
        private Long id;
        private String name;
        private boolean isCurrent;
        private LocalDate start_season;
        private LocalDate end_season;
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
            this.isCurrent = isCurrent;
            return this;
        }

        public LeagueBuilder withStart_season(LocalDate start_season) {
            this.start_season = start_season;
            return this;
        }

        public LeagueBuilder withEnd_season(LocalDate end_season) {
            this.end_season = end_season;
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
            league.setStart_season(start_season);
            league.setEnd_season(end_season);
            league.setLogo(logo);
            league.setFlag(flag);
            league.isCurrent = this.isCurrent;
            return league;
        }
    }
}
