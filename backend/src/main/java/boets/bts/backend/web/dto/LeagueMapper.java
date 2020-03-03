package boets.bts.backend.web.dto;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Team;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TeamMapper.class, RoundMapper.class})
public interface LeagueMapper  {

    @Mappings({
            @Mapping(target = "league_id", source = "id"),
            @Mapping(target = "teamDtos", source = "teams"),
            @Mapping(target = "roundDtos", source = "rounds")
    })
    LeagueDto toLeagueDto(League league);

    List<LeagueDto> toLeagueDtoList(List<League> leagueList);

    @InheritInverseConfiguration
    League toLeague(LeagueDto leagueDto);

    @InheritInverseConfiguration
    List<League> toLeagues(List<LeagueDto> leagueDtoList);
}
