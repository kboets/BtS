package boets.bts.backend.web.dto;

public class LeagueDto {

    private String league_id;
    private String name;
    private String season;
    private String start_season;
    private String end_season;
    private String logo;
    private String  flag;
    private boolean isCurrent;

    public String getLeague_id() {
        return league_id;
    }

    public void setLeague_id(String league_id) {
        this.league_id = league_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getStart_season() {
        return start_season;
    }

    public void setStart_season(String start_season) {
        this.start_season = start_season;
    }

    public String getEnd_season() {
        return end_season;
    }

    public void setEnd_season(String end_season) {
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

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }
}
