package boets.bts.backend.service.round;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrentRoundHandlerFactory {


    private ExistingCurrentRoundHandler existingCurrentRoundHandler;
    private NonExistingRoundHandler nonExistingRoundHandler;

    public CurrentRoundHandlerFactory(ExistingCurrentRoundHandler existingCurrentRoundHandler, NonExistingRoundHandler nonExistingRoundHandler) {
        this.existingCurrentRoundHandler = existingCurrentRoundHandler;
        this.nonExistingRoundHandler = nonExistingRoundHandler;
    }

    public CurrentRoundHandler getCurrentRoundHandler(boolean isExisting) {
        if (isExisting) {
            return existingCurrentRoundHandler;
        } else {
            return nonExistingRoundHandler;
        }

    }
}
