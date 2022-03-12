package boets.bts.backend.domain;

import javax.persistence.*;

@Entity
@Table
public class Algorithm {

    @Id
    @Column(name="algorithm_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String name;
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
    private AlgorithmPoints homePoints;
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
    private AlgorithmPoints awayPoints;
    @Column
    private Integer homeBonus;
    @Column
    private Integer awayMalus;
    @Column
    private boolean current;
    @Column
    private Integer threshold;

    public Algorithm() {
        this.type = "WIN";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AlgorithmPoints getHomePoints() {
        return homePoints;
    }

    public void setHomePoints(AlgorithmPoints homePoints) {
        this.homePoints = homePoints;
    }

    public AlgorithmPoints getAwayPoints() {
        return awayPoints;
    }

    public void setAwayPoints(AlgorithmPoints awayPoints) {
        this.awayPoints = awayPoints;
    }

    public Integer getHomeBonus() {
        return homeBonus;
    }

    public void setHomeBonus(Integer homeBonus) {
        this.homeBonus = homeBonus;
    }

    public Integer getAwayMalus() {
        return awayMalus;
    }

    public void setAwayMalus(Integer awayMalus) {
        this.awayMalus = awayMalus;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
