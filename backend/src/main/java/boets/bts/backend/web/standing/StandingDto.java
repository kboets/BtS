package boets.bts.backend.web.standing;

import boets.bts.backend.web.team.TeamDto;
import liquibase.pro.packaged.I;

import java.util.Date;

public class StandingDto {

    private TeamDto team;
    private Integer rank;
    private Integer points;
    private Date lastUpdate;
    private int season;
    private int roundNumber;
    private SubStandingDto allSubStanding;
    private SubStandingDto awaySubStanding;
    private SubStandingDto homeSubStanding;

    public TeamDto getTeam() {
        return team;
    }

    public void setTeam(TeamDto team) {
        this.team = team;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getPoints() {
        if(points == null) {
            points = 0;
        }
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

    public SubStandingDto getAllSubStanding() {
        if(allSubStanding == null) {
            allSubStanding = new SubStandingDto();
        }
        return allSubStanding;
    }

    public void setAllSubStanding(SubStandingDto allSubStanding) {
        this.allSubStanding = allSubStanding;
    }

    public SubStandingDto getAwaySubStanding() {
        if(awaySubStanding == null) {
            awaySubStanding = new SubStandingDto();
        }
        return awaySubStanding;
    }

    public void setAwaySubStanding(SubStandingDto awaySubStanding) {
        this.awaySubStanding = awaySubStanding;
    }

    public SubStandingDto getHomeSubStanding() {
        if(homeSubStanding == null) {
            homeSubStanding = new SubStandingDto();
        }
        return homeSubStanding;
    }

    public void setHomeSubStanding(SubStandingDto homeSubStanding) {
        this.homeSubStanding = homeSubStanding;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }
}
