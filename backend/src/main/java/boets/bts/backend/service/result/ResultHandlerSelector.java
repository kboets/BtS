package boets.bts.backend.service.result;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Round;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ResultHandlerSelector {

    private List<ResultHandler> resultHandlers;

    @Autowired
    public ResultHandlerSelector(List<ResultHandler> resultHandlers) {
        this.resultHandlers = resultHandlers;
    }

    public Optional<ResultHandler> select(List<Result> allResults, List<Result> allNonFinishedResult, String currentRound) {
        for(ResultHandler resultHandler : resultHandlers) {
            if(resultHandler.accepts(allResults, allNonFinishedResult, currentRound)) {
                return Optional.of(resultHandler);
            }
        }
        return Optional.empty();

    }
}
