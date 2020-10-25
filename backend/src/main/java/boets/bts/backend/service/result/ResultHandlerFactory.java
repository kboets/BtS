package boets.bts.backend.service.result;

import org.springframework.stereotype.Service;

@Service
public class ResultHandlerFactory {

     private EmptyResultHandler emptyResultHandler;
     private NonEmptyResultHandler nonEmptyResultHandler;

    public ResultHandlerFactory(EmptyResultHandler emptyResultHandler, NonEmptyResultHandler nonEmptyResultHandler) {
        this.emptyResultHandler = emptyResultHandler;
        this.nonEmptyResultHandler = nonEmptyResultHandler;
    }

    public ResultHandler getResultHandler(boolean isEmpty) {
        if(isEmpty) {
            return emptyResultHandler;
        } else {
            return nonEmptyResultHandler;
        }
    }
}
