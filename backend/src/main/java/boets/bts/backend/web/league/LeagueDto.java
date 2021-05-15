package boets.bts.backend.web.league;

import boets.bts.backend.web.round.RoundDto;
import boets.bts.backend.web.team.TeamDto;

import java.time.LocalDate;
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
    private boolean selected;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public static final class LeagueDtoBuilder {
        private String league_id;
        private String name;
        private int season;
        private LocalDate startSeason;
        private LocalDate endSeason;
        private String logo;
        private String  flag;
        private String countryCode;
        private boolean isCurrent;
        private boolean selected;
        private List<TeamDto> teamDtos;
        private List<RoundDto> roundDtos;

        private LeagueDtoBuilder() {
        }

        public static LeagueDtoBuilder aLeagueDto() {
            return new LeagueDtoBuilder();
        }

        public LeagueDtoBuilder withLeague_id(String league_id) {
            this.league_id = league_id;
            return this;
        }

        public LeagueDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public LeagueDtoBuilder withSeason(int season) {
            this.season = season;
            return this;
        }

        public LeagueDtoBuilder withStartSeason(LocalDate startSeason) {
            this.startSeason = startSeason;
            return this;
        }

        public LeagueDtoBuilder withEndSeason(LocalDate endSeason) {
            this.endSeason = endSeason;
            return this;
        }

        public LeagueDtoBuilder withLogo(String logo) {
            this.logo = logo;
            return this;
        }

        public LeagueDtoBuilder withFlag(String flag) {
            this.flag = flag;
            return this;
        }

        public LeagueDtoBuilder withCountryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public LeagueDtoBuilder withIsCurrent(boolean isCurrent) {
            this.isCurrent = isCurrent;
            return this;
        }

        public LeagueDtoBuilder withSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public LeagueDtoBuilder withTeamDtos(List<TeamDto> teamDtos) {
            this.teamDtos = teamDtos;
            return this;
        }

        public LeagueDtoBuilder withRoundDtos(List<RoundDto> roundDtos) {
            this.roundDtos = roundDtos;
            return this;
        }

        public LeagueDto build() {
            LeagueDto leagueDto = new LeagueDto();
            leagueDto.setLeague_id(league_id);
            leagueDto.setName(name);
            leagueDto.setSeason(season);
            leagueDto.setStartSeason(startSeason);
            leagueDto.setEndSeason(endSeason);
            leagueDto.setLogo(logo);
            leagueDto.setFlag(flag);
            leagueDto.setCountryCode(countryCode);
            leagueDto.setSelected(selected);
            leagueDto.setTeamDtos(teamDtos);
            leagueDto.setRoundDtos(roundDtos);
            leagueDto.isCurrent = this.isCurrent;
            return leagueDto;
        }
    }
}
