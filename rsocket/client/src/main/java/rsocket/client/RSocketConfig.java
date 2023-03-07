package rsocket.client;

import io.rsocket.SocketAcceptor;
import io.rsocket.loadbalance.LoadbalanceRSocketClient;
import io.rsocket.loadbalance.LoadbalanceStrategy;
import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.loadbalance.RoundRobinLoadbalanceStrategy;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import rsocket.share.Constants;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class RSocketConfig {
    @Bean
    public RSocketStrategies rSocketStrategies() {
        return RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
                .metadataExtractorRegistry(metadataExtractorRegistry -> {
                    metadataExtractorRegistry.metadataToExtract(MimeType.valueOf(Constants.MIME_FILE_EXTENSION), String.class, Constants.FILE_EXTN);
                    metadataExtractorRegistry.metadataToExtract(MimeType.valueOf(Constants.MIME_FILE_NAME), String.class, Constants.FILE_NAME);
                })
                .build();
    }

    @Bean("rSocketClient")
    public RSocketRequester rSocketClient(RSocketRequester.Builder builder, RSocketStrategies strategies, Flux<List<LoadbalanceTarget>> targetFlux){
        SocketAcceptor acceptor = RSocketMessageHandler.responder(strategies, new ClientHandler());
        builder.rsocketConnector(connector -> connector
                .acceptor(acceptor)
                .reconnect(Retry.fixedDelay(Integer.MAX_VALUE, Duration.ofSeconds(5)))
//                .keepAlive(Duration.ofSeconds(30), Duration.ofSeconds(30))
            );
        var client = builder.transports(targetFlux, new RoundRobinLoadbalanceStrategy());
        return client;
    }

    @Bean
    public Flux<List<LoadbalanceTarget>> targets(){
        return Mono.fromSupplier(() -> List.of(
                        "localhost:7000",
                        "localhost:7001",
                        "localhost:7002"
                    )
                )
                .repeatWhen(longFlux -> longFlux.delayElements(Duration.ofMillis(50)))
                .map(this::toLoadBalanceTarget);
    }

    private List<LoadbalanceTarget> toLoadBalanceTarget(List<String> rSocketServers){
        return rSocketServers.stream()
                .map(server -> {
                    String[] split = server.split(":");
                    return LoadbalanceTarget.from(server, TcpClientTransport.create(split[0], Integer.parseInt(split[1])));
                })
                .collect(Collectors.toList());
    }
}
