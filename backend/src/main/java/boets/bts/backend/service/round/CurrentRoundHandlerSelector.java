package boets.bts.backend.service.round;

import boets.bts.backend.domain.Round;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CurrentRoundHandlerSelector {

    private List<CurrentRoundHandler> roundHandlers;

    @Autowired
    public CurrentRoundHandlerSelector(List<CurrentRoundHandler> roundHandlers) {
        this.roundHandlers = roundHandlers;
    }

    public Optional<CurrentRoundHandler> select(Optional<Round> roundOptional) {
        CurrentRoundHandler roundHandler = null;
        for(CurrentRoundHandler currentRoundHandler: roundHandlers) {
            if(currentRoundHandler.accept(roundOptional)) {
                roundHandler = currentRoundHandler;
                break;
            }
        }
        return Optional.ofNullable(roundHandler);
    }
}
