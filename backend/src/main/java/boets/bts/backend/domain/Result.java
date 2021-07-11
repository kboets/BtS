package boets.bts.backend.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "RESULT")
public class Result implements Serializable {

    @Id
    @Column(name="result_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "event_date")
    private LocalDate eventDate;

    @Column(nullable = false, name = "goals_home_team")
    private int goalsHomeTeam;

    @Column(nullable = false, name = "goals_away_team")
    private int goalsAwayTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", referencedColumnName = "league_id")
    private League league;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hometeam_id", referencedColumnName = "id")
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "awayteam_id", referencedColumnName = "id")
    private Team awayTeam;

    @Column(nullable = false)
    private String round;

    @Column(name = "status")
    private String matchStatus;

    @Column(name = "round_number")
    private Integer roundNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public int getGoalsHomeTeam() {
        return goalsHomeTeam;
    }

    public void setGoalsHomeTeam(int goalsHomeTeam) {
        this.goalsHomeTeam = goalsHomeTeam;
    }

    public int getGoalsAwayTeam() {
        return goalsAwayTeam;
    }

    public void setGoalsAwayTeam(int goalsAwayTeam) {
        this.goalsAwayTeam = goalsAwayTeam;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return goalsHomeTeam == result.goalsHomeTeam && goalsAwayTeam == result.goalsAwayTeam && Objects.equals(id, result.id) && Objects.equals(eventDate, result.eventDate) && Objects.equals(league, result.league) && Objects.equals(homeTeam, result.homeTeam) && Objects.equals(awayTeam, result.awayTeam) && Objects.equals(round, result.round) && Objects.equals(matchStatus, result.matchStatus) && Objects.equals(roundNumber, result.roundNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventDate, goalsHomeTeam, goalsAwayTeam, league, homeTeam, awayTeam, round, matchStatus, roundNumber);
    }


    public static final class ResultBuilder {
        private Long id;
        private LocalDate eventDate;
        private int goalsHomeTeam;
        private int goalsAwayTeam;
        private League league;
        private Team homeTeam;
        private Team awayTeam;
        private String round;
        private String matchStatus;
        private Integer roundNumber;

        private ResultBuilder() {
        }

        public static ResultBuilder aResult() {
            return new ResultBuilder();
        }

        public ResultBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ResultBuilder withEventDate(LocalDate eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public ResultBuilder withGoalsHomeTeam(int goalsHomeTeam) {
            this.goalsHomeTeam = goalsHomeTeam;
            return this;
        }

        public ResultBuilder withGoalsAwayTeam(int goalsAwayTeam) {
            this.goalsAwayTeam = goalsAwayTeam;
            return this;
        }

        public ResultBuilder withLeague(League league) {
            this.league = league;
            return this;
        }

        public ResultBuilder withHomeTeam(Team homeTeam) {
            this.homeTeam = homeTeam;
            return this;
        }

        public ResultBuilder withAwayTeam(Team awayTeam) {
            this.awayTeam = awayTeam;
            return this;
        }

        public ResultBuilder withRound(String round) {
            this.round = round;
            return this;
        }

        public ResultBuilder withMatchStatus(String matchStatus) {
            this.matchStatus = matchStatus;
            return this;
        }

        public ResultBuilder withRoundNumber(Integer roundNumber) {
            this.roundNumber = roundNumber;
            return this;
        }

        public Result build() {
            Result result = new Result();
            result.setId(id);
            result.setEventDate(eventDate);
            result.setGoalsHomeTeam(goalsHomeTeam);
            result.setGoalsAwayTeam(goalsAwayTeam);
            result.setLeague(league);
            result.setHomeTeam(homeTeam);
            result.setAwayTeam(awayTeam);
            result.setRound(round);
            result.setMatchStatus(matchStatus);
            result.setRoundNumber(roundNumber);
            return result;
        }
    }
}
