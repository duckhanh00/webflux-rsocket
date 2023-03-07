package rsocket.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import reactor.core.publisher.Mono;

@Configuration
public class ExceptionHandler {
    @MessageExceptionHandler
    public Mono<Object> handleException(Exception e) {
        return Mono.just(e.toString());
    }
}
