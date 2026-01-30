package dev.vibeafrika.pcm.gateway.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.V4Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "profile-service")
public class ProfileConsumerContractTest {

    @Pact(consumer = "api-gateway")
    public V4Pact createPact(PactDslWithProvider builder) {
        return builder
                .given("A profile exists with ID 12345")
                .uponReceiving("A request to retrieve a profile")
                .path("/api/v1/profiles/12345")
                .method("GET")
                .headers(Map.of("X-Tenant-Id", "default"))
                .willRespondWith()
                .status(200)
                .body("{\"id\": \"12345\", \"handle\": \"test_user\", \"tenantId\": \"default\"}")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void verifyGetProfile(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
                .baseUrl(mockServer.getUrl())
                .defaultHeader("X-Tenant-Id", "default")
                .build();

        Mono<String> response = webClient.get()
                .uri("/api/v1/profiles/12345")
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(response)
                .assertNext(json -> {
                    assertThat(json).contains("test_user");
                    assertThat(json).contains("12345");
                })
                .verifyComplete();
    }
}
