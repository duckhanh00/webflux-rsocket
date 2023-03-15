package rsocket.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RecordAlreadyExistException extends RuntimeException {

    public RecordAlreadyExistException() {
        super(String.format("Record already exist"));
    }
}