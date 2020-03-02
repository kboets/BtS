package boets.bts.backend.web.dto;

import boets.bts.backend.domain.Team;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mappings({
            @Mapping(target = "teamId", source = "id"),
            @Mapping(target = "leagueDto", source = "league"),
    })
    TeamDto toTeamDto(Team team);

    List<TeamDto> toTeamDtos(List<Team> teamList);

    @InheritInverseConfiguration
    Team toTeam(TeamDto teamDto);


    List<Team> toTeams(List<TeamDto> dtos);

}

