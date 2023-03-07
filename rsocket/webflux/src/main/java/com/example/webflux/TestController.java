package com.example.webflux;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> get() {
        return Mono.just("test");
    }
}
