package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "STANDING")
public class Standing implements Serializable  {

    @Id
    @Column(name="standing_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", referencedColumnName = "league_id")
    private League league;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;

    @Column
    private Integer rank;

    @Column
    private Integer points;

    @Column(name = "last_updated")
    private Date lastUpdate;

    @Column
    private String round;

    @Embedded
    @AttributeOverride(
            name = "matchPlayed",
            column = @Column( name = "all_match_played" )
    )
    @AttributeOverride(
            name = "win",
            column = @Column( name = "all_win" )
    )
    @AttributeOverride(
            name = "lose",
            column = @Column( name = "all_lose" )
    )
    @AttributeOverride(
            name = "draw",
            column = @Column( name = "all_draw" )
    )
    @AttributeOverride(
            name = "goalsFor",
            column = @Column( name = "all_goals_for" )
    )
    @AttributeOverride(
            name = "goalsAgainst",
            column = @Column( name = "all_goals_against" )
    )
    private SubStanding allSubStanding;

    @Embedded
    @AttributeOverride(
            name = "matchPlayed",
            column = @Column( name = "away_match_played" )
    )
    @AttributeOverride(
            name = "win",
            column = @Column( name = "away_win" )
    )
    @AttributeOverride(
            name = "lose",
            column = @Column( name = "away_lose" )
    )
    @AttributeOverride(
            name = "draw",
            column = @Column( name = "away_draw" )
    )
    @AttributeOverride(
            name = "goalsFor",
            column = @Column( name = "away_goals_for" )
    )
    @AttributeOverride(
            name = "goalsAgainst",
            column = @Column( name = "away_goals_against" )
    )
    private SubStanding awaySubStanding;


    @Embedded
    @AttributeOverride(
            name = "matchPlayed",
            column = @Column( name = "home_match_played" )
    )
    @AttributeOverride(
            name = "win",
            column = @Column( name = "home_win" )
    )
    @AttributeOverride(
            name = "lose",
            column = @Column( name = "home_lose" )
    )
    @AttributeOverride(
            name = "draw",
            column = @Column( name = "home_draw" )
    )
    @AttributeOverride(
            name = "goalsFor",
            column = @Column( name = "home_goals_for" )
    )
    @AttributeOverride(
            name = "goalsAgainst",
            column = @Column( name = "home_goals_against" )
    )
    private SubStanding homeSubStanding;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public SubStanding getAllSubStanding() {
        return allSubStanding;
    }

    public void setAllSubStanding(SubStanding allSubStanding) {
        this.allSubStanding = allSubStanding;
    }

    public SubStanding getAwaySubStanding() {
        return awaySubStanding;
    }

    public void setAwaySubStanding(SubStanding awaySubStanding) {
        this.awaySubStanding = awaySubStanding;
    }

    public SubStanding getHomeSubStanding() {
        return homeSubStanding;
    }

    public void setHomeSubStanding(SubStanding homeSubStanding) {
        this.homeSubStanding = homeSubStanding;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }
}
