package boets.bts.backend.web.team;

import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.standing.StandingDto;

public class TeamDto {

    private String id;
    private String teamId;
    private String name;
    private String logo;
    private LeagueDto leagueDto;
    private String stadiumName;
    private int stadiumCapacity;
    private String city;
    private StandingDto standing;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public LeagueDto getLeagueDto() {
        return leagueDto;
    }

    public void setLeagueDto(LeagueDto leagueDto) {
        this.leagueDto = leagueDto;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public int getStadiumCapacity() {
        return stadiumCapacity;
    }

    public void setStadiumCapacity(int stadiumCapacity) {
        this.stadiumCapacity = stadiumCapacity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public StandingDto getStanding() {
        return standing;
    }

    public void setStanding(StandingDto standing) {
        this.standing = standing;
    }


    public static final class TeamDtoBuilder {
        private String id;
        private String teamId;
        private String name;
        private String logo;
        private LeagueDto leagueDto;
        private String stadiumName;
        private int stadiumCapacity;
        private String city;
        private StandingDto standing;

        private TeamDtoBuilder() {
        }

        public static TeamDtoBuilder aTeamDto() {
            return new TeamDtoBuilder();
        }

        public TeamDtoBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public TeamDtoBuilder withTeamId(String teamId) {
            this.teamId = teamId;
            return this;
        }

        public TeamDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public TeamDtoBuilder withLogo(String logo) {
            this.logo = logo;
            return this;
        }

        public TeamDtoBuilder withLeagueDto(LeagueDto leagueDto) {
            this.leagueDto = leagueDto;
            return this;
        }

        public TeamDtoBuilder withStadiumName(String stadiumName) {
            this.stadiumName = stadiumName;
            return this;
        }

        public TeamDtoBuilder withStadiumCapacity(int stadiumCapacity) {
            this.stadiumCapacity = stadiumCapacity;
            return this;
        }

        public TeamDtoBuilder withCity(String city) {
            this.city = city;
            return this;
        }

        public TeamDtoBuilder withStanding(StandingDto standing) {
            this.standing = standing;
            return this;
        }

        public TeamDto build() {
            TeamDto teamDto = new TeamDto();
            teamDto.setId(id);
            teamDto.setTeamId(teamId);
            teamDto.setName(name);
            teamDto.setLogo(logo);
            teamDto.setLeagueDto(leagueDto);
            teamDto.setStadiumName(stadiumName);
            teamDto.setStadiumCapacity(stadiumCapacity);
            teamDto.setCity(city);
            teamDto.setStanding(standing);
            return teamDto;
        }
    }
}
