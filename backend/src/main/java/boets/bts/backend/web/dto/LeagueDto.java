package boets.bts.backend.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LeagueDto {

    private String league_id;
    private String name;
    private int season;
    private LocalDate startSeason;
    private LocalDate endSeason;
    private String logo;
    private String  flag;
    private String countryCode;
    private boolean isCurrent;
    private List<TeamDto> teamDtos;
    private List<RoundDto> roundDtos;

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

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public LocalDate getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(LocalDate startSeason) {
        this.startSeason = startSeason;
    }

    public LocalDate getEndSeason() {
        return endSeason;
    }

    public void setEndSeason(LocalDate endSeason) {
        this.endSeason = endSeason;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public List<TeamDto> getTeamDtos() {
        return teamDtos;
    }

    public void setTeamDtos(List<TeamDto> teamDtos) {
        this.teamDtos = teamDtos;
    }

    public List<RoundDto> getRoundDtos() {
        return roundDtos;
    }

    public void setRoundDtos(List<RoundDto> roundDtos) {
        this.roundDtos = roundDtos;
    }
}
