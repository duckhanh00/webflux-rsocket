package rsocket.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.client.*;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.netty.http.client.HttpClient;
import rsocket.share.Constants;
import rsocket.share.Message;
import rsocket.share.Status;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@ShellComponent
@AllArgsConstructor
public class RSocketShellClient {
    static final String CLIENT = "Client";
    static final String REQUEST = "Request";
    static final String STREAM = "Stream";
    static final String CHANNEL = "Channel";
    static final String FIRE_AND_FORGET = "Fire And Forget";

    // Add a global class variable for the RSocketRequester
    private final RSocketRequester rsocketRequester;

    private static Disposable disposable;

    @PostConstruct
    public void init() {
        String uuid = UUID.randomUUID().toString();
        log.info("Connecting using client ID: {}", uuid);
    }

    @ShellMethod("Send one request. One response will be printed.")
    public void requestResponse() throws InterruptedException {
        log.info("\n Sending one request. Waiting for one response...");
        Message message = this.rsocketRequester
                .route("request-response")
                .data(new Message(CLIENT, REQUEST))
                .retrieveMono(Message.class)
                .block();
        log.info("\n Response was: {}", message);
    }

    @ShellMethod("Test send one request. One response will be printed.")
    public void trr() throws InterruptedException {
        log.info("\n Start test sending multiple request. Waiting for one response...");

        List<Long> durations = new ArrayList<>();

        Flux.range(1, 5)
                .flatMap(l -> {
//                    long start = System.currentTimeMillis();
                    return this.rsocketRequester
                            .route("request-response-str")
//                            .data(new Message(CLIENT, REQUEST))
                            .retrieveMono(String.class)
//                            .retry(5)
                            .elapsed()
                            .doOnNext(msg -> {
                                log.info("Response: {} - time: {}", msg.getT2(), msg.getT1());
                                durations.add(msg.getT1());
                            })
//                            .doFinally(signalType -> {
//                                log.info("2 - Response: {} - time: {}", "test", Duration.ofMillis(System.currentTimeMillis() - start).toMillis());
//                            })
                            .doOnError(throwable -> {
                                log.error(" error requester {}", throwable.getMessage());
                            });
                }, 1000)
                .doFinally((signalType) -> {
                    log.info("max {} min {}", durations.stream().max(Long::compareTo), durations.stream().min(Long::compareTo));
                })
                .doOnError(throwable -> {
                    log.error(" error flux {}", throwable.getMessage());
                })
                .subscribe();
    }

    @ShellMethod("Test http send one request. One response will be printed.")
    public void thrr() throws InterruptedException {
        log.info("\n Start test sending http multiple request. Waiting for one response...");

        List<Long> durations = new ArrayList<>();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500000)
                .responseTimeout(Duration.ofMillis(500000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(500000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(500000, TimeUnit.MILLISECONDS)));

        WebClient client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        Flux.range(1, 1000)
                        .flatMap(i -> {
                            long start = System.currentTimeMillis();
                            return client.get()
                                    .uri("http://localhost:8080/test")
//                                    .uri("http://localhost:8888/greet/from/you")
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .elapsed()
                                    .doOnNext(s -> {
                                        log.info("response: {} - time {}", s.getT2(), s.getT1());
                                        durations.add(s.getT1());
                                    })
                                    .doFinally(signalType -> {
                                        log.info("2 - Response: {} - time: {}", "test", Duration.ofMillis(System.currentTimeMillis() - start).toMillis());
                                    });
                        }, 1000)
                .doOnComplete(() -> {
                    log.info("max {} min {}", durations.stream().max(Long::compareTo), durations.stream().min(Long::compareTo));
                })
                .subscribe();
    }

    @ShellMethod("Send one request. No response will be returned.")
    public void fireAndForget() throws InterruptedException {
        log.info("\nFire-And-Forget. Sending one request. Expect no response (check server log)...");
        this.rsocketRequester
                .route("fire-and-forget")
                .data(new Message(CLIENT, FIRE_AND_FORGET))
                .send()
                .block();
    }

    @ShellMethod("Send one request. Many responses (stream) will be printed.")
    public void stream() {
        log.info("\nRequest-Stream. Sending one request. Waiting for unlimited responses (Type 's' to stop)...");
        disposable =
                this.rsocketRequester
                .route("stream")
                .data(new Message(CLIENT, STREAM))
                .retrieveFlux(Message.class)
                .subscribe(er -> log.info("Response received: {}", er));
    }

    @ShellMethod("Stop streaming messages from the server.")
    public void s() {
        if (null != disposable) {
            disposable.dispose();
        }
    }

    @ShellMethod("Load balancing server")
    public void lb() {
        Flux.range(1, 10000)
                .delayElements(Duration.ofMillis(100))
                .flatMap(i -> this.rsocketRequester.route("square-calculator").data(i).retrieveMono(Integer.class).retry(1))
                .doOnNext(i -> System.out.println("Response : " + i))
                .blockLast();
    }

    @ShellMethod("Stream some settings to the server. Stream of responses will be printed.")
    public void channel() {
        Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(1));
        Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5));
        Mono<Duration> setting3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15));

        Flux<Duration> settings = Flux.concat(setting1, setting2, setting3)
                .doOnNext(d -> log.info("\nSending setting for {}-second interval.\n", d.getSeconds()));

        disposable = this.rsocketRequester
                .route("channel")
                .data(settings)
                .retrieveFlux(Message.class)
                .subscribe(message -> log.info("Received: {} \n(Type 's' to stop.)", message));
    }

    @Autowired
    private ResourceLoader resourceLoader;

    @ShellMethod("Upload file")
    public void uploadFile() {
        Resource resource = resourceLoader.getResource("classpath:input/rsc-0.9.1.jar");

        // read input file as 4096 chunks
        Flux<DataBuffer> readFlux = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096)
                .doOnNext(s -> System.out.println("Sent"));

        // rsocket request
        this.rsocketRequester
                .route("file.upload")
                .metadata(metadataSpec -> {
                    metadataSpec.metadata("jar", MimeType.valueOf(Constants.MIME_FILE_EXTENSION));
                    metadataSpec.metadata("output", MimeType.valueOf(Constants.MIME_FILE_NAME));
                })
                .data(readFlux)
                .retrieveFlux(Status.class)
                .doOnNext(s -> System.out.println("Upload Status : " + s))
                .subscribe();
    }
}
