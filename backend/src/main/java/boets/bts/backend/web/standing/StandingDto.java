package boets.bts.backend.web.standing;

import boets.bts.backend.web.team.TeamDto;

import java.util.Date;

public class StandingDto {

    private TeamDto team;
    private Integer rank;
    private Integer points;
    private Date lastUpdate;
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
        return allSubStanding;
    }

    public void setAllSubStanding(SubStandingDto allSubStanding) {
        this.allSubStanding = allSubStanding;
    }

    public SubStandingDto getAwaySubStanding() {
        return awaySubStanding;
    }

    public void setAwaySubStanding(SubStandingDto awaySubStanding) {
        this.awaySubStanding = awaySubStanding;
    }

    public SubStandingDto getHomeSubStanding() {
        return homeSubStanding;
    }

    public void setHomeSubStanding(SubStandingDto homeSubStanding) {
        this.homeSubStanding = homeSubStanding;
    }
}
