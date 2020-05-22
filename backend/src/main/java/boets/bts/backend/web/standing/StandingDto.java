package boets.bts.backend.web.standing;

import java.util.Date;

public class StandingDto {

    private String league_id;
    private String team_id;
    private Integer rank;
    private Integer points;
    private Date lastUpdate;
    private SubStandingDto allSubStanding;
    private SubStandingDto awaySubStanding;
    private SubStandingDto homeSubStanding;

    public String getLeague_id() {
        return league_id;
    }

    public void setLeague_id(String league_id) {
        this.league_id = league_id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
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
