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

    @Column(nullable = false)
    private boolean isCurrent;

    @Column(nullable = false)
    private LocalDate start_season;

    @Column(nullable = false)
    private LocalDate end_season;

    @Column(nullable = false)
    private int season;

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

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }
}
