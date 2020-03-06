package boets.bts.backend.web.round;

import boets.bts.backend.domain.Round;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoundMapper {

    @Mappings({
            @Mapping(target = "roundId", source = "id"),
            @Mapping(target = "leagueDto", source = "league"),
    })
    RoundDto toRoundDto(Round round);

    @InheritInverseConfiguration
    Round toRound(RoundDto roundDto);

    List<RoundDto> toRoundDtos(List<Round> rounds);

    List<Round> toRounds(List<RoundDto> roundDtos);
}
