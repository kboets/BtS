package boets.bts.backend.web.results;

import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.team.TeamDto;

import java.util.Date;

public class ResultDto {
    private LeagueDto league;
    private Date eventDate;
    private RoundDto round;
    private TeamDto homeTeam;
    private TeamDto awayTeam;
    private int goalsHomeTeam;
    private int goalsAwayTeam;

    public LeagueDto getLeague() {
        return league;
    }

    public void setLeague(LeagueDto league) {
        this.league = league;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public RoundDto getRound() {
        return round;
    }

    public void setRound(RoundDto round) {
        this.round = round;
    }

    public TeamDto getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(TeamDto homeTeam) {
        this.homeTeam = homeTeam;
    }

    public TeamDto getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(TeamDto awayTeam) {
        this.awayTeam = awayTeam;
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
}
