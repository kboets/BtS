package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ROUND")
public class Round implements Serializable {

    @Id
    @Column(name="round_id")
    private Integer id;

    @Column(nullable = false)
    private String round;

    @Column(nullable = false)
    private int season;

    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id")
    private League league;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }
}
