package boets.bts.backend.web.round;

import boets.bts.backend.domain.Round;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoundMapper {
    @Mappings({
            @Mapping(target = "roundId", source = "id"),
            @Mapping(target = "leagueDto", source = "league"),
            @Mapping(target = "round", source = "round"),
            @Mapping(target = "playRound", source = "round", qualifiedByName = "toPlayRound")

    })
    RoundDto toRoundDto(Round round);


    @InheritInverseConfiguration
    Round toRound(RoundDto roundDto);

    List<RoundDto> toRoundDtos(List<Round> rounds);

    List<Round> toRounds(List<RoundDto> roundDtos);

    @Named("toPlayRound")
    static int toPlayRound(String round) {
        int roundVal = 0;
        try {
            roundVal = new Integer(StringUtils.substringAfterLast(round, "_"));
        } catch(NumberFormatException e) {    }

        return roundVal;
    }
}
