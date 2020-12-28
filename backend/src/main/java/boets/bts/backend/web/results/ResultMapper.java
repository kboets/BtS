package boets.bts.backend.web.results;

import boets.bts.backend.domain.Result;
import boets.bts.backend.web.league.LeagueMapper;
import boets.bts.backend.web.round.RoundMapper;
import boets.bts.backend.web.team.TeamMapper;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LeagueMapper.class, TeamMapper.class, RoundMapper.class})
public interface ResultMapper {

    @Mappings({
            @Mapping(target = "result_id", source = "id"),
            @Mapping(target = "matchStatus", source = "matchStatus")
    })
    ResultDto toResultDto(Result result);

    List<ResultDto> toResultDtos(List<Result> results);

    @InheritInverseConfiguration
    Result toResult(ResultDto resultDto);

    @InheritInverseConfiguration
    List<Result> toResults(List<ResultDto> resultDtoList);


}
