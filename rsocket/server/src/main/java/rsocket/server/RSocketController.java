//package rsocket.server;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.messaging.handler.annotation.Headers;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.rsocket.RSocketRequester;
//import org.springframework.messaging.rsocket.annotation.ConnectMapping;
//import org.springframework.stereotype.Controller;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import rsocket.share.Constants;
//import rsocket.share.model.Message;
//import rsocket.share.Status;
//
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Controller
//public class RSocketController {
//    static final String SERVER = "Server";
//    static final String RESPONSE = "Response";
//    static final String STREAM = "Stream";
//    static final String CHANNEL = "Channel";
//    @Autowired
//    private FileUploadService service;
//
//    private final List<RSocketRequester> CLIENTS = new ArrayList<>();
//
//    @MessageMapping("request-response-str")
//    String requestResponseString() {
//        Flux<String> firstFlux = Flux.just("1", "2", "3");
//        Flux<String> secondFlux = Flux.just("4", "5", "6");
//
//        String val = firstFlux.mergeWith(secondFlux).blockLast();
//
//        return String.valueOf(val);
//    }
//
//    @MessageMapping("request-response")
//    Message requestResponse(Message request) {
//        log.info("Received request-response request: {}", request);
//        // create a single Message and return it
//        return new Message(SERVER, RESPONSE);
//    }
//
//    @MessageMapping("fire-and-forget")
//    public void fireAndForget(Message request) {
//        log.info("Received fire-and-forget request: {}", request);
//    }
//
//    @MessageMapping("stream")
//    Flux<Message> stream(Message request) {
//        log.info("Received stream request: {}", request);
//        return Flux
//                .interval(Duration.ofSeconds(1))
//                .map(index -> new Message(SERVER, STREAM, index))
//                .log();
//    }
//
//    @MessageMapping("channel")
//    Flux<Message> channel(final Flux<Duration> settings) {
//        return settings
//                .doOnNext(setting -> log.info("\nFrequency setting is {} second(s).\n", setting.getSeconds()))
//                .switchMap(setting -> Flux.interval(setting)
//                        .map(index -> new Message(SERVER, CHANNEL, index)))
//                        .log();
//    }
//
//    @ConnectMapping("shell-client")
//    void connectShellClientAndForTelemetry(RSocketRequester requester, @Payload String client) {
//        requester.rsocket()
//                .onClose()
//                .doFirst(() -> {
//                    log.info("Client: {} CONNECTED.", client);
//                    CLIENTS.add(requester);
//                })
//                .doOnError(error -> {
//                    log.warn("Channel to client {} CLOSED.", client);
//                })
//                .doFinally(consumer -> {
//                    CLIENTS.remove(requester);
//                    log.info("Client {} DISCONNECTED.", client);
//                })
//                .subscribe();
//
//        requester.route("client-status")
//                .data("OPEN")
//                .retrieveFlux(String.class)
//                .doOnNext(s -> log.info("Client: {} Free Memory: {}.", client, s))
//                .subscribe();
//    }
//
//    @MessageMapping("square-calculator")
//    public Mono<Integer> square(Mono<Integer> input) {
//        return input
//                .doOnNext(i -> log.info("Received: {}", i))
//                .delayElement(Duration.ofMillis(500))
//                .map(i -> i * i);
//    }
//
//    @MessageMapping("file.upload")
//    public Flux<Status> upload(@Headers Map<String, Object> metadata,
//                               @Payload Flux<DataBuffer> content) throws IOException {
//        var fileName = metadata.get(Constants.FILE_NAME);
//        var fileExtn = metadata.get(Constants.FILE_EXTN);
//        var path = Paths.get(fileName + "." + fileExtn);
//        return Flux.concat(service.uploadFile(path, content), Mono.just(Status.COMPLETED))
//                .onErrorReturn(Status.FAILED);
//    }
//}
