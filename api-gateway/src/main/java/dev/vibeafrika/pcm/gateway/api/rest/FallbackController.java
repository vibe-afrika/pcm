package dev.vibeafrika.pcm.gateway.api.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/profile")
    public Mono<String> profileFallback() {
        log.warn("Profile Service is down or timed out. Returning fallback response.");
        return Mono.just("Profile Service is currently unavailable. Please try again later.");
    }
}
