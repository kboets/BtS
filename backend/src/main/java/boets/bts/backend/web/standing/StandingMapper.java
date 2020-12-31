package boets.bts.backend.web.standing;

import boets.bts.backend.domain.Standing;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.round.RoundMapper;
import boets.bts.backend.web.team.TeamMapper;
import liquibase.pro.packaged.I;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LeagueMapper.class, TeamMapper.class, SubStandingMapper.class})
public interface StandingMapper {

    StandingDto toStandingDto(Standing standing);

    @InheritInverseConfiguration
    Standing toStanding(StandingDto standingDto);

    List<StandingDto> toStandingDtos(List<Standing> standings);

    @InheritInverseConfiguration
    List<Standing> toStandings(List<StandingDto> standingDtos);

}
