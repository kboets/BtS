package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ROUND")
public class Round implements Serializable {

    @Id
    @Column(name="round_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id")
    private League league;

    @Column(nullable = false)
    private String round;

    @Column(nullable = false)
    private int season;

    @Column
    private Boolean current;

    @Column(name = "actual_date")
    private LocalDate currentDate;

    @Column(name = "round_number")
    private Integer roundNumber;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Boolean isCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public static final class RoundBuilder {
        private Long id;
        private League league;
        private String round;
        private int season;
        private Boolean current;
        private LocalDate currentDate;
        private Integer roundNumber;

        private RoundBuilder() {
        }

        public static RoundBuilder aRound() {
            return new RoundBuilder();
        }

        public RoundBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public RoundBuilder withLeague(League league) {
            this.league = league;
            return this;
        }

        public RoundBuilder withRound(String round) {
            this.round = round;
            return this;
        }

        public RoundBuilder withSeason(int season) {
            this.season = season;
            return this;
        }

        public RoundBuilder withCurrent(Boolean current) {
            this.current = current;
            return this;
        }

        public RoundBuilder withCurrentDate(LocalDate currentDate) {
            this.currentDate = currentDate;
            return this;
        }

        public RoundBuilder withRoundNumber(Integer roundNumber) {
            this.roundNumber = roundNumber;
            return this;
        }

        public Round build() {
            Round round = new Round();
            round.setId(id);
            round.setLeague(league);
            round.setSeason(season);
            round.setCurrent(current);
            round.setCurrentDate(currentDate);
            round.setRoundNumber(roundNumber);
            return round;
        }
    }
}
