package boets.bts.backend.web.standing;

import boets.bts.backend.domain.SubStanding;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubStandingMapper {

    SubStandingDto toSubStandingDto(SubStanding subStanding);

    List<SubStandingDto> toSubStandingDtos(List<SubStanding> subStandings);

    @InheritInverseConfiguration
    SubStanding toSubStanding(SubStandingDto subStandingDto);

    @InheritInverseConfiguration
    List<SubStanding> toSubStandings(List<SubStandingDto> subStandingDtos);

}
